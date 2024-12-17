package com.kosa.moimeasy.board.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ID")
    private Long boardId;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    @JsonIgnore // 순환참조방지
    private User writer;
    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = true)
    private String content;
    @Column(name = "Tag" , nullable = true)
    private String tag = "일반";
    @Column(name = "Is_Notice",nullable = false)
    private Boolean isNotice= false;

}
