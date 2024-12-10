package com.kosa.moimeasy.schedule.dto;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.schedule.entity.Schedule;
import com.kosa.moimeasy.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Builder
public class ScheduleDTO {

    private Long scheduleId; // SCHEDULE_ID
    private Long moeimId; // MOEIM_ID
    private Long userId; // USER_ID
    private String eventCode; // EVENT_CODE
    private String scheduleTitle; // SCHEDULE_TITLE
    private String description; // DESCRIPTION
    private Boolean isAllDayEvent; // IS_ALL_DAY_EVENT
    private Timestamp startTime; // START_TIME
    private Timestamp endTime; // END_TIME
    private String location; // LOCATION
    private String priority; // PRIORITY


    //DTO -> Entity 변환
    public static Schedule toEntity(ScheduleDTO dto, Moeim moeim, User creator) {
        return Schedule.builder()
                .scheduleId(dto.getScheduleId())
                .moeim(moeim)
                .creator(creator)
                .eventCode(dto.getEventCode())
                .scheduleTitle(dto.getScheduleTitle())
                .description(dto.getDescription())
                .isAllDayEvent(dto.getIsAllDayEvent())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .location(dto.getLocation())
                .priority(dto.getPriority())
                .build();
    };

    //Entity -> DTO 변환
    public static ScheduleDTO fromEntity(Schedule schedule) {
        return ScheduleDTO.builder()
                .scheduleId(schedule.getScheduleId())
                .moeimId(schedule.getMoeim().getMoeimId())
                .userId(schedule.getCreator() != null ? schedule.getCreator().getUserId() : null)
                .eventCode(schedule.getEventCode())
                .scheduleTitle(schedule.getScheduleTitle())
                .description(schedule.getDescription())
                .isAllDayEvent(schedule.getIsAllDayEvent())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .location(schedule.getLocation())
                .priority(schedule.getPriority())
                .build();
    }
}