package com.kosa.moimeasy.transaction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class DepositDto { // 입금

    @Getter
    @Setter
    public static class Request {

        @NotNull(message = "유저 ID는 필수 값입니다.")
        private Long userId;

        @Min(value = 1, message = "입금 최소금액은 1원입니다.")
        private Long amount;
    }

    @Getter
    @Builder
    public static class Response {

        private String accountNumber;
        private String depositName;
        private Long amount;
        private LocalDateTime transacted_at;
    }
}
