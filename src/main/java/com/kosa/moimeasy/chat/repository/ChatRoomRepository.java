package com.kosa.moimeasy.chat.repository;

import com.kosa.moimeasy.chat.entity.ChatRoom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @EntityGraph(attributePaths = {"members.user"})
    List<ChatRoom> findAll();

    @Query("SELECT DISTINCT cr FROM ChatRoom cr JOIN cr.members m WHERE m.user.userId = :userId")
    List<ChatRoom> findByUserId(@Param("userId") Long userId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.moeim.id = :moeimId")
    ChatRoom findByMoeimId(@Param("moeimId") Long moeimId);
}
