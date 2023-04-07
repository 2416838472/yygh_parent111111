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
        if(scheduleExist != null){
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        }else {
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
        Pageable pageable = PageRequest.of(page-1, limit, sort);

        // 创建一个新的Schedule对象，并将给定的ScheduleQueryVo对象的属性复制到它。
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        // 将Schedule对象的isDeleted属性设置为0。
        schedule.setIsDeleted(0);

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
        if(schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    //根据医院编号，科室编号，工作日期查询排班规则数据
    @Override
    public Map<String,Object> findScheduleRule(Long page, Long limit, String hoscode, String depcode) {
        //根据医院编号和科室编号进行查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        //根据工作日期进行分组
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                                .count().as("docCount")
                                .sum("reservedNumber").as("reservedNumber")
                                .sum("availableNumber").as("availableNumber"),
                Aggregation.project("workDate").andExclude("_id"),
                //排序
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                //分页
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        //执行聚合查询
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.
                aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        //总记录数
        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> countAggregate = mongoTemplate.
                aggregate(countAggregation, Schedule.class, BookingScheduleRuleVo.class);
        int count = countAggregate.getMappedResults().size();
        //把日期对应星期获取
        for (BookingScheduleRuleVo bookingScheduleRuleVo : mappedResults) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", mappedResults);
        result.put("total", count);

        //获取医院名称
        String hosname = hospitalOneService.getHospName(hoscode);

        Map<String, Object> baseMap = new HashMap<>();
        baseMap.put("hoscode", hoscode);
        baseMap.put("baseMap", baseMap);
        //返回结果
        return result;
    }

    //根据医院编号，科室编号，工作日期，排班编号查询排班详细信息
    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        List<Schedule> scheduleList =  scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        scheduleList.stream().forEach(item -> {
            this.packageSchedule(item);
        });
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

    private String getDayOfWeek(DateTime dateTime){
        String week = "";
        switch (dateTime.getDayOfWeek()){
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


}
