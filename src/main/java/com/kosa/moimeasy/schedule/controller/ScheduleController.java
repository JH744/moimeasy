package com.kosa.moimeasy.schedule.controller;

import com.kosa.moimeasy.schedule.dto.ScheduleDTO;
import com.kosa.moimeasy.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    //일정 가져오기
    @GetMapping("/events")
    public ResponseEntity<List<ScheduleDTO>>  getAllScheduleEvents(Long moeimId){
        return ResponseEntity.ok(scheduleService.getAllScheduleEvents(moeimId));
    }

    //일정 추가하기
    @PostMapping("/events")
    public ResponseEntity<String> addEvent(@RequestBody ScheduleDTO scheduleDTO) {
        System.out.println("이벤트 데이터 수신: " + scheduleDTO);
        scheduleService.addScheduleEvent(scheduleDTO);
        return ResponseEntity.ok("이벤트 추가 성공");
    }

    //일정 삭제하기
    @DeleteMapping("/events/{id}")
    public ResponseEntity<?>  deleteEvent(@PathVariable(name = "id") String eventCode){
        System.out.println("삭제api호출됨:이벤트코드 >>>>>>>>>>>> "+eventCode);
        scheduleService.deleteByEventCode(eventCode);
        return ResponseEntity.ok("삭제");
    }


}
