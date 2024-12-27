package com.kosa.moimeasy.common.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 삽입시간, 수정시간을 모든 Entity에 공통으로 가져가기 위해 BaseEntity 클래스 생성
@Getter
@MappedSuperclass // 이 클래스를 상속받을 때 필드를 column으로 인식
@EntityListeners(AuditingEntityListener.class) // BaseTimeEntity 클래스에 Auditing 기능 포함
public abstract class BaseEntity {

    @CreatedDate // 엔티티가 생성되어 저장될 때 시간도 자동으로 저장
    private LocalDateTime createAt;

    @LastModifiedDate // 엔티티가 마지막으로 수정된 시간이 자동으로 저장
    private LocalDateTime updateAt;

}
