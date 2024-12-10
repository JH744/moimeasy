package com.kosa.moimeasy.membership.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//회비 납부를 위한 DTO
@Getter
@NoArgsConstructor
public class PayDTO {

    private Long id; // 회비 id

    private Long user_id; // 회원 id

    private Long moeim_id; // 모임 id

    private BigDecimal money; // 입금액

    private LocalDateTime pay_date; // 입금일

}
