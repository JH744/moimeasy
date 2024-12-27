package com.kosa.moimeasy.board.dto;


import com.kosa.moimeasy.board.entity.Board;
import com.kosa.moimeasy.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BoardDTO {
    private Long boardId;
    private String title;
    private String content;
    private Boolean isNotice;
    private String writerName;
    private String profileImage;
    private String tag;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;


    public Board toEntity(User writer) {
        return Board.builder()
                .boardId(this.boardId)
                .title(this.title)
                .content(this.content)
                .isNotice(this.isNotice)
                .writer(writer) // 연관된 User 엔티티
                .tag(this.tag)
                .build();
    }
}

