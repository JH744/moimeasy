package com.kosa.moimeasy.transaction.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class RemittanceDto { // 계좌 이체

    @Getter
    @Setter
    public static class Request {

        private Long userId;

        @NotNull(message = "송금액은 필수값입니다.")
        @Min(value = 1, message = "송금 최소금액은 1원입니다.")
        private double amount;

        private Long moeimId;
    }

    @Getter
    @Builder
    public static class Response {

        private String sentAccountNumber;

        private String receivedAccountNumber;

        private String receivedName;

        private String sentName;

        private double amount;
    }
}