package com.kosa.moimeasy.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "계좌ㅏ의 카테고리별 거래 내역 반환 DTO")
public class GetTransactionByCategoryResponseDTO {

    @Schema(description = "카테고리 이름", example = "회식")
    private String category;

    @Schema(description = "카테고리 총 지출액", example = "100000")
    private int amount;

    @Schema(description = "총 지출액에 대한 카테고리의 비율", example = "30")
    private double rate;

}
