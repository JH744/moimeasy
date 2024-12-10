package com.kosa.moimeasy.chat.entity;

import jakarta.persistence.*;
import lombok.*;
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
    private String name;

    @ManyToMany
    @JoinTable(
        name = "chat_room_members",
        joinColumns = @JoinColumn(name = "chat_room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;
}


