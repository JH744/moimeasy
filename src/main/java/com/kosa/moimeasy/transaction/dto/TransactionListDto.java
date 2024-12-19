package com.kosa.moimeasy.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class TransactionListDto { // 거래내역(리스트) 조회

    @Getter
    @Setter
    public static class Request {

        @NotNull(message = "모임 ID는 필수값입니다.")
        private Long moeimId;             // 거래 내역을 조회할 모임 계좌 id

        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate startDate;    // 조회 시작 날짜

        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate endDate;      // 조회 마지막 날짜
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Response {

        private List<TransactionDto> transactionList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class RemittanceListResponse {

        private List<RemittanceListDto> remittanceList;
    }


}
