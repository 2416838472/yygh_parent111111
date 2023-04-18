package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.atguigu.model.hosp.BookingRule;
import com.atguigu.model.hosp.Department;
import com.atguigu.model.hosp.Hospital;
import com.atguigu.model.hosp.Schedule;
import com.atguigu.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.vo.hosp.ScheduleOrderVo;
import com.atguigu.vo.hosp.ScheduleQueryVo;
import com.atguigu.yygh.exception.YyghException;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalOneService hospitalOneService;

    @Autowired
    private DepartmentService departmentService;


    @Override
    public void saveSchedule(Map<String, Object> parmMap) {
        // 1.根据医院编号和科室编号查询科室是否存在
        String paraMapString = JSONObject.toJSONString(parmMap);
        JSONObject.parseObject(paraMapString, Schedule.class);

        //根据医院编号和科室编号查询科室是否存在
        Schedule scheduleExist =
                scheduleRepository.getScheduleByHoscodeAndDepcode(parmMap.get("hoscode").toString(), parmMap.get("depcode").toString());
        // 2.判断
        if (scheduleExist != null) {
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        } else {
            scheduleExist.setCreateTime(new Date());
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        }
    }

    @Override
    public Page<Schedule> selectPage(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        // 按创建时间降序排序日程。
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 根据给定的页码和限制设置要检索的页面。
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        // 创建一个新的Schedule对象，并将给定的ScheduleQueryVo对象的属性复制到它。
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        // 将Schedule对象的isDeleted属性设置为0。
        //schedule.setIsDeleted(0);

        // 为Schedule对象创建匹配器。
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        // 使用Schedule对象和匹配器创建一个Example对象。
        Example<Schedule> example = Example.of(schedule, matcher);
        // 检索与Example对象和pageable匹配的日程页面。
        Page<Schedule> pages = scheduleRepository.findAll(example, pageable);

        // 返回检索到的日程页面。
        return pages;
    }


    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    //根据医院编号，科室编号，工作日期查询排班规则数据
    @Override
    public Map<String, Object> findScheduleRule(Long page, Long limit, String hoscode, String depcode) {
        //根据医院编号和科室编号进行查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        //根据工作日期进行分组
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria), //筛选符合条件的记录
                Aggregation.group("workDate").first("workDate").as("workDate") //按照工作日期分组，取第一个工作日期作为工作日期
                        .count().as("docCount") //统计医生数量
                        .sum("reservedNumber").as("reservedNumber") //统计已预约数量
                        .sum("availableNumber").as("availableNumber"), //统计剩余数量
                Aggregation.project("workDate").andExclude("_id"), //只保留工作日期字段
                //排序
                Aggregation.sort(Sort.Direction.DESC, "workDate"), //按照工作日期降序排序
                //分页
                Aggregation.skip((page - 1) * limit), //跳过前面的记录
                Aggregation.limit(limit) //只取指定数量的记录
        );
        //执行聚合查询
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.
                aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults(); //获取查询结果
        //总记录数
        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.match(criteria), //筛选符合条件的记录
                Aggregation.group("workDate") //按照工作日期分组
        );
        AggregationResults<BookingScheduleRuleVo> countAggregate = mongoTemplate.
                aggregate(countAggregation, Schedule.class, BookingScheduleRuleVo.class);
        int count = countAggregate.getMappedResults().size(); //获取记录总数
        //把日期对应星期获取
        for (BookingScheduleRuleVo bookingScheduleRuleVo : mappedResults) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate)); //获取日期对应的星期
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek); //设置星期
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", mappedResults); //设置查询结果
        result.put("total", count); //设置记录总数

        //获取医院名称
        String hosname = hospitalOneService.getHospName(hoscode);

        Map<String, Object> baseMap = new HashMap<>();
        baseMap.put("hoscode", hoscode);
        baseMap.put("hosname", hosname);
        //返回结果
        return result;
    }

    //根据医院编号，科室编号，工作日期，排班编号查询排班详细信息
    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        List<Schedule> scheduleList = scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        scheduleList.forEach(this::packageSchedule);
        return scheduleList;
    }

    //根据排班id获取排班数据
    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname", hospitalOneService.getHospName(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname", departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    private String getDayOfWeek(DateTime dateTime) {
        String week = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.MONDAY:
                week = "星期一";
                break;
            case DateTimeConstants.TUESDAY:
                week = "星期二";
                break;
            case DateTimeConstants.WEDNESDAY:
                week = "星期三";
                break;
            case DateTimeConstants.APRIL:
                week = "星期四";
                break;
            case DateTimeConstants.MAY:
                week = "星期五";
                break;
            case DateTimeConstants.JUNE:
                week = "星期六";
                break;
            case DateTimeConstants.JULY:
                week = "星期日";
                break;
        }
        return week;
    }

    //上传排班
    @Override
    public void save(Map<String, Object> paramMap) {
        //1、paramMap转换department对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramMapString, Schedule.class);

        String hoscode = schedule.getHoscode();
        String HosScheduleId = schedule.getHosScheduleId();//医院端排班id

        //2、根据医院编号 和 排班编号查询
        Schedule scheduleExist = scheduleRepository
                .getScheduleByHoscodeAndHosScheduleId(hoscode, HosScheduleId);

        //判断
        if (scheduleExist != null) {
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {

        Map<String, Object> result = new HashMap<>();

        //获取预约规则
        Hospital hospital = hospitalOneService.getHosp(hoscode);
        if (null == hospital) {
            throw new YyghException();
        }
        BookingRule bookingRule = hospital.getBookingRule();

        //获取可预约日期分页数据
        IPage iPage = this.getListDate(page, limit, bookingRule);

        //当前页可预约日期
        List<Date> dateList = iPage.getRecords();

        //获取可预约日期科室剩余预约数
        Criteria criteria = Criteria.where("hoscode").is(hoscode).
                and("depcode").is(depcode)
                .and("workDate").in(dateList);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
                        .first("workDate").as("workDateMd")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );

        AggregationResults<BookingScheduleRuleVo> aggregationResults =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList = aggregationResults.getMappedResults();

        //获取科室剩余预约数
        //合并数据 将统计数据ScheduleVo根据“安排日期”合并到BookingRuleVo
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleVoMap = scheduleVoList.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }

        //获取可预约排班规则
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for (int i = 0; i < dateList.size(); i++) {
            Date date = dateList.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            if (null == bookingScheduleRuleVo) { // 说明当天没有排班医生
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setWorkDate(date);
                bookingScheduleRuleVo.setWorkDateMd(date);
                //科室剩余预约数  -1表示无号
                bookingScheduleRuleVo.setDocCount(-1);
                bookingScheduleRuleVo.setAvailableNumber(-1);
                bookingScheduleRuleVo.setReservedNumber(-1);
            }

            //计算当前预约日期为周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //最后一页最后一条记录为即将预约   
            //状态 0：正常, 1：即将放号, -1：当天已停止挂号
            if (i == dateList.size() - 1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //当天预约如果过了停号时间， 不能预约
            //第一页第一条
            if (i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopTime.isBeforeNow()) {
                    //停止预约
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        //其他基础数据（hosname+bigname+depname+workDateString+releaseTime+stopTime）
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hospitalOneService.getHospName(hoscode));
        Department department = departmentService.getDepartment(hoscode, depcode);
        baseMap.put("bigname", department.getBigname());
        baseMap.put("depname", department.getDepname());
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        baseMap.put("stopTime", bookingRule.getStopTime());

        //可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());//总日期个数
        result.put("baseMap", baseMap);

        return result;
    }

    private IPage getListDate(Integer page, Integer limit, BookingRule bookingRule) {

        //当天放号时间
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        //预约周期
        int cycle = bookingRule.getCycle();
        //如果当天放号时间已过，则预约周期后一天为即将放号时间，周期加1
        if (releaseTime.isBeforeNow()) {
            cycle += 1;
        }

        //可预约所有日期，最后一天显示即将放号倒计时
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            //计算当前预约日期
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }

        //日期分页，由于预约周期不一样，页面一排最多显示7天数据，多了就要分页显示
        List<Date> pageDateList = new ArrayList<>();

        int start = (page - 1) * limit;
        int end = (page - 1) * limit + limit;

        if (end > dateList.size()) {
            end = dateList.size();
        }

        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, limit, dateList.size());
        iPage.setRecords(pageDateList);

        return iPage;
    }

    private DateTime getDateTime(Date date, String releaseTime) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd");
        DateTime dateTime =
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }


    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }

    @Override
    public Schedule getById(String id) {
        Schedule schedule = scheduleRepository.findById(id).get();
        this.packageSchedule(schedule);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        //排班信息
        Schedule schedule = this.getById(scheduleId);
        if(null == schedule) {
            throw new YyghException();
        }
        //获取预约规则信息
        Hospital hospital = hospitalOneService.getHosp(schedule.getHoscode());
        if(null == hospital) {
            throw new YyghException();
        }
        BookingRule bookingRule = hospital.getBookingRule();
        if(null == bookingRule) {
            throw new YyghException();
        }

        scheduleOrderVo.setHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospitalOneService.getHospName(schedule.getHoscode()));
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname(departmentService.getDepartment(schedule.getHoscode(), schedule.getDepcode()).getDepname());
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());
        //退号截止天数（如：就诊前一天为-1，当天为0）
        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());
        //预约开始时间
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());
        //预约截止时间
        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());
        //当天停止挂号时间
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStopTime(stopTime.toDate());

        return scheduleOrderVo;
    }
}

