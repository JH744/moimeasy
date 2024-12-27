package com.kosa.moimeasy.schedule.service;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.schedule.dto.ScheduleDTO;
import com.kosa.moimeasy.schedule.entity.Schedule;
import com.kosa.moimeasy.schedule.repository.ScheduleRepository;
import com.kosa.moimeasy.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.kosa.moimeasy.schedule.dto.ScheduleDTO.fromEntity;
import static com.kosa.moimeasy.schedule.dto.ScheduleDTO.toEntity;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    // 일정 가져오기
    public List<ScheduleDTO> getAllScheduleEvents(Long moeimId) {
        List<Schedule> eventList = scheduleRepository.findByMoeim_MoeimId(1L); //임시 모임
        if (eventList.isEmpty()) {
            throw new RuntimeException("일정 조회불가");
        }
        return eventList.stream()  // Schedule 엔티티 → DTO 변환 스트림사용
                .map(ScheduleDTO::fromEntity) // 각 Schedule을 ScheduleDTO로 변환
                .collect(Collectors.toList()); // List로 변환하여 반환하기
    }

    // 일정 추가하기
    public ScheduleDTO addScheduleEvent(ScheduleDTO scheduleDTO) {
//        Moeim moeim = moeimRepository.findById(dto.getMoeimId())
//                .orElseThrow(() -> new RuntimeException("Moeim 못찾음"));
//
//        User creator = userRepository.findById(dto.getUserId())
//                .orElse(null);
        Moeim moeim = new Moeim();
        moeim.setMoeimId(1L);
        User creator = new User();
        creator.setUserId(1L);
        Schedule schedule = toEntity(scheduleDTO, moeim, creator);
        Schedule savedSchedule = scheduleRepository.save(schedule); // 저장 성공시 저장된 엔티티 반환함
        return fromEntity(savedSchedule);
    }

    //일정 삭제하기
    @Transactional
    public void deleteByEventCode(String eventCode) {
        scheduleRepository.deleteByEventCode(eventCode);
    }

    @Transactional
    public void updatePartialEvent(String eventCode, ScheduleDTO partialUpdateDto) {
        // 기존 일정 조회
        Schedule schedule = scheduleRepository.findByEventCode(eventCode)
                .orElseThrow(() -> new RuntimeException("해당 이벤트를 찾을 수 없습니다."));

        // partialUpdateDto에 필드가 null이 아닌 경우만 업데이트
        if (partialUpdateDto.getScheduleTitle() != null) {
            schedule.setScheduleTitle(partialUpdateDto.getScheduleTitle());
        }

        if (partialUpdateDto.getStartTime() != null) {
            schedule.setStartTime(partialUpdateDto.getStartTime());
        }

        if (partialUpdateDto.getEndTime() != null) {
            schedule.setEndTime(partialUpdateDto.getEndTime());
        }

        if (partialUpdateDto.getDescription() != null) {
            schedule.setDescription(partialUpdateDto.getDescription());
        }

        // 필요한 필드만 선택적으로 업데이트한 뒤, JPA 영속성 컨텍스트에 의해 자동으로 flush됨
        // @Transactional로 인해 메서드 종료 시점에 변경사항이 반영
    }
}
