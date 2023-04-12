package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.model.hosp.Schedule;
import com.atguigu.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.vo.hosp.ScheduleQueryVo;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

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
}

