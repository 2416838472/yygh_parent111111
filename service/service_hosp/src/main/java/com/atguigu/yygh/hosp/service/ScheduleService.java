package com.atguigu.yygh.hosp.service;

import com.atguigu.model.hosp.Department;
import com.atguigu.model.hosp.Schedule;
import com.atguigu.vo.hosp.ScheduleOrderVo;
import com.atguigu.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {

    //上传排班
    void saveSchedule(Map<String, Object> parmMap);

    //分页查询
    Page<Schedule> selectPage(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    //删除
    void remove(String hoscode, String hosScheduleId);

    //根据医院编号和科室编号，查询排班规则数据
    Map<String,Object> findScheduleRule(Long page, Long limit, String hoscode, String depcode);

    // 根据医院编号、科室编号、工作日期，查询排班详细信息
    List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate);

    //上传排班
    void save(Map<String, Object> paramMap);

    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    Department getDepartment(String hoscode, String depcode);

    Schedule getById(String id);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);
}
