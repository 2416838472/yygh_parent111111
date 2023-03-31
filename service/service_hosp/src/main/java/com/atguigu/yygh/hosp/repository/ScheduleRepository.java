package com.atguigu.yygh.hosp.repository;

import com.atguigu.model.hosp.Department;
import com.atguigu.model.hosp.Schedule;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    Schedule getScheduleByHoscodeAndDepcode(String hoscode, String depcode);

    Page<Schedule> findAll1(Example<Schedule> example, Pageable pageable);

    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}
