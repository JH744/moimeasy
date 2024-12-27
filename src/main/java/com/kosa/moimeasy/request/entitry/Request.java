package com.kosa.moimeasy.request.entitry;

import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Request extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "MOEIM_ID", nullable = false)
    private Moeim moeim;

    @Enumerated(EnumType.STRING)
    @Column(name = "REQUEST_TYPE", nullable = false)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private RequestStatus status;

    @Column(name = "DETAILS", nullable = false)
    private String details;

    public enum RequestType {
        JOIN,
        LEAVE,
        MODIFY
    }

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
