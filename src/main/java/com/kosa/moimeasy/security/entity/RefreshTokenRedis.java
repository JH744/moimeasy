package com.kosa.moimeasy.security.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("RefreshToken")
public class RefreshTokenRedis implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long userId; // userId를 키로 사용

    private String token;
    private LocalDateTime expiryDate;

    @TimeToLive
    private Long ttl; // TTL을 초 단위로 설정
}
