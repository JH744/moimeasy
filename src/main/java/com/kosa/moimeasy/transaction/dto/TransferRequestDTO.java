package com.kosa.moimeasy.transaction.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class TransferRequestDTO {
    private Long userId; // 회원 id
    private Long moeimId; // 모임 id
    private double transferAmount; // 이체 금액
    private String content; // 메모
    private int transferType; // 거래 유형 (출금 : 0, 입금 : 1)
}
