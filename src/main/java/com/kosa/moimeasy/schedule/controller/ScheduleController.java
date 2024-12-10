package com.kosa.moimeasy.schedule.controller;


import com.kosa.moimeasy.schedule.dto.ScheduleDTO;
import com.kosa.moimeasy.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("/events")
    public ResponseEntity<String> addEvent(@RequestBody ScheduleDTO scheduleDTO) {
        System.out.println("이벤트 데이터 수신: " + scheduleDTO);
        scheduleService.addScheduleEvent(scheduleDTO);
        return ResponseEntity.ok("이벤트 추가 성공");
    }

}
