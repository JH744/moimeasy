package com.kosa.moimeasy.schedule.repository;

import com.kosa.moimeasy.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    //모임 id로 스케쥴 이벤트 가져오기
    List<Schedule> findByMoeim_MoeimId(Long moeimId);
    //이벤트코드로 스케쥴 이벤트 삭제
    void deleteByEventCode(String eventCode);

    Optional<Schedule> findByEventCode(String eventCode);
}
