package com.kosa.moimeasy.attendants.entity;
import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.schedule.entity.Schedule;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Attendants extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendantsId;

    @ManyToOne
    @JoinColumn(name = "SCHEDULE_ID", nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
}
