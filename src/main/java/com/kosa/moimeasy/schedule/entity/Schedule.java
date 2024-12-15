package com.kosa.moimeasy.schedule.entity;

import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_ID")
    private Long scheduleId;

    @ManyToOne
    @JoinColumn(name = "MOEIM_ID", nullable = true)
    private Moeim moeim;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = true)
    private User creator;

    @Column(name="EVENT_CODE", nullable = false)
    private String eventCode;

    @Column(name = "SCHEDULE_Title", nullable = false)
    private String scheduleTitle;

    @Column(name = "SCHEDULE_TYPE", nullable = true)
    private String scheduleType;

    @Column(name="DESCRIPTION", nullable = false)
    private String description;

    @Column(name="IS_ALL_DAY_EVENT" ,nullable = false)
    private Boolean isAllDayEvent;

    @Column(name = "START_TIME", nullable = false)
    private Timestamp startTime;

    @Column(name = "END_TIME", nullable = false)
    private Timestamp endTime;


    @Column(name = "LOCATION")
    private String location;


}
