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

    private Long scheduleId;
    private Long moeimId;
    private Long userId;
    private String eventCode;
    private String scheduleTitle;
    private String description;
    private String scheduleType;
    private Boolean isAllDayEvent;
    private Timestamp startTime;
    private Timestamp endTime;
    private String location;


    //DTO -> Entity 변환
    public static Schedule toEntity(ScheduleDTO dto, Moeim moeim, User creator) {
        return Schedule.builder()
                .scheduleId(dto.getScheduleId())
                .moeim(moeim)
                .creator(creator)
                .eventCode(dto.getEventCode())
                .scheduleTitle(dto.getScheduleTitle())
                .description(dto.getDescription())
                .scheduleType(dto.getScheduleType())
                .isAllDayEvent(dto.getIsAllDayEvent())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .location(dto.getLocation())
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
                .scheduleType(schedule.getScheduleType())
                .isAllDayEvent(schedule.getIsAllDayEvent())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .location(schedule.getLocation())
                .build();
    }
}