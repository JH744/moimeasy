package com.kosa.moimeasy.gallery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.moeim.entity.Moeim;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Gallery extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="moeim_id")
    @JsonIgnore
    private Moeim moeim;

    @Column(nullable = false, length = 2000) // url명이 200~500자 이상일 경우를 위함
    private String imageUrl;

}
