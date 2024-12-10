package com.kosa.moimeasy.schedule.repository;

import com.kosa.moimeasy.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
