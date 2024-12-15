package com.kosa.moimeasy.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "계좌의 거래내역 정보 반환 DTO")
public class GetTransactionResponseDTO {

    @Schema(description = "가게 이름", example = "49도시락")
    private String content;

    // 입금 1, 출금 0
    @Schema(description = "입/출금 구분", example = "1")
    private int transactionType;

    @Schema(description = "거래금액", example = "15000")
    private double transactionAmount;

    @Schema(description = "거래시간", example = "2024-12-25")
    private LocalDateTime transactionAt;
}
