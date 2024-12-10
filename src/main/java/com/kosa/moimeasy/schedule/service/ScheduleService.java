package com.kosa.moimeasy.schedule.service;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.schedule.dto.ScheduleDTO;
import com.kosa.moimeasy.schedule.entity.Schedule;
import com.kosa.moimeasy.schedule.repository.ScheduleRepository;
import com.kosa.moimeasy.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kosa.moimeasy.schedule.dto.ScheduleDTO.fromEntity;
import static com.kosa.moimeasy.schedule.dto.ScheduleDTO.toEntity;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;


    public ScheduleDTO addScheduleEvent(ScheduleDTO scheduleDTO) {
//        Moeim moeim = moeimRepository.findById(dto.getMoeimId())
//                .orElseThrow(() -> new RuntimeException("Moeim 못찾음"));
//
//        User creator = userRepository.findById(dto.getUserId())
//                .orElse(null);
        Moeim moeim = new Moeim();   //임시 null 데이터로 삽입
        moeim.setMoeimId(1L);
        User creator = new User();  //임시 null 데이터로 삽입
        creator.setUserId(1L);
        Schedule schedule = toEntity(scheduleDTO, moeim, creator);
        Schedule savedSchedule = scheduleRepository.save(schedule); // 저장된 엔티티를 반환
        return fromEntity(savedSchedule);
    }
}
