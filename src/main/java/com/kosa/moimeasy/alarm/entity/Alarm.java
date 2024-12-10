package com.kosa.moimeasy.alarm.entity;

import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Alarm extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALARM_ID")
    private Long alarmId;

    @ManyToOne
    @JoinColumn(name = "MOEIM_ID", nullable = false)
    private Moeim moeim;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "ALARM_TYPE", nullable = false)
    private AlarmType alarmType;

    @Column(name = "ALARM_MESSAGE", nullable = false)
    private String alarmMessage;

    @Column(name = "STATUS", nullable = false)
    private Boolean status;

    public enum AlarmType {
        REMINDER,
        ALERT
    }
}
