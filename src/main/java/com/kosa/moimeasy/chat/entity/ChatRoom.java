package com.kosa.moimeasy.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import com.kosa.moimeasy.user.entity.User;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 채팅방 이름

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 순환 참조 방지
    private List<ChatRoomUser> members = new ArrayList<>();

    @Column(nullable = false)
    private Long createdBy; // 채팅방 생성자
}





