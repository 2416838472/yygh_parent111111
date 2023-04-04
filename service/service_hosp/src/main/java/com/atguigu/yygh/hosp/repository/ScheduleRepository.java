package com.atguigu.yygh.hosp.repository;

import com.atguigu.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    Schedule getScheduleByHoscodeAndDepcode(String hoscode, String depcode);

    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    // 根据医院编号、科室编号、工作日期，查询排班详细信息
    List<Schedule> getScheduleDetail(String hoscode, String depcode, Date toDate);
}
