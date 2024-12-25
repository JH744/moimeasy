package com.kosa.moimeasy.schedule.controller;

import com.kosa.moimeasy.schedule.dto.ScheduleDTO;
import com.kosa.moimeasy.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name="일정 캘린더 페이지" , description="Schedule Calendar API")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    //일정 가져오기
    @Operation(summary = "모임일정 조회" ,description ="모임 일정 조회하기" )
    @GetMapping
    public ResponseEntity<List<ScheduleDTO>>  getAllScheduleEvents(Long moeimId){
        return ResponseEntity.ok(scheduleService.getAllScheduleEvents(moeimId));
    }

    //일정 추가하기
    @Operation(summary = "모임일정 추가" ,description ="모임 일정 추가하기" )
    @PostMapping
    public ResponseEntity<String> addEvent(@RequestBody ScheduleDTO scheduleDTO) {
        System.out.println("이벤트 데이터 수신: " + scheduleDTO);
        scheduleService.addScheduleEvent(scheduleDTO);
        return ResponseEntity.ok("이벤트 추가 성공");
    }

    //일정 삭제하기
    @Operation(summary = "모임일정 삭제" ,description ="모임 일정 삭제하기" )
    @DeleteMapping("/{id}")
    public ResponseEntity<?>  deleteEvent(@PathVariable(name = "id") String eventCode){
        System.out.println("삭제api호출됨:이벤트코드 >>>>>>>>>>>> "+eventCode);
        scheduleService.deleteByEventCode(eventCode);
        return ResponseEntity.ok("삭제");
    }


    // 일정 부분 업데이트(PATCH)
    @Operation(summary = "모임일정 수정" ,description ="모임일정 수정하기" )
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePartialEvent(
            @PathVariable(name = "id") String eventCode,
            @RequestBody ScheduleDTO partialUpdateDto) {
        scheduleService.updatePartialEvent(eventCode, partialUpdateDto);
        return ResponseEntity.ok("이벤트 업데이트 성공");
    }

}
