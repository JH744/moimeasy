package com.kosa.moimeasy.board.entity;

import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ID")
    private Long boardId;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User author;

    @Column(name = "TITLE", nullable = false)
    private String title;

    private String content;

    private Boolean isNotice;

}
