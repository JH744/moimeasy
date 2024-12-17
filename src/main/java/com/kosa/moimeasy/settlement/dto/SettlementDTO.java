package com.kosa.moimeasy.settlement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettlementDTO {
    private String title;        // 제목
    private String userName;     // 작성자 이름
    private String imageUrl;     // 영수증 이미지 URL
    private double amount;       // 요청 금액
    private String createdAt;    // 생성 날짜 (문자열로 변환)
}

