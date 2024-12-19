package com.kosa.moimeasy.notification.repository;

import com.kosa.moimeasy.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @Query("SELECT n FROM Notification n " +
            "WHERE n.moeim.moeimId = :moeimId " +
            "AND n.createAt > :lastViewedAt")
    List<Notification> findNotificationsAfterLastViewedAt(
            @Param("moeimId") Long moeimId,
            @Param("lastViewedAt") LocalDateTime lastViewedAt
    );
}
