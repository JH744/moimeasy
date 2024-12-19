package com.kosa.moimeasy.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kosa.moimeasy.transaction.type.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RemittanceListDto { // 계좌 내역 조회

        private Long userId; // 회원 Id
        private String receivedAccount; // 송금받는 계좌
        private String userName; // 이름
        private String photo; // 사진
        private double amount; // 금액

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDateTime transactionAt; // 납부 날짜

        private TransactionType transactionType; // 납부 상태 (납부완료, 회비미납)

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate startDate;    // 조회 시작 날짜

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate endDate;      // 조회 마지막 날짜
}
