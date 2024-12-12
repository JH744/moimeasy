package com.kosa.moimeasy.transaction.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class RemittanceDto {

    @Getter
    @Setter
    public static class Request {

        @NotBlank(message = "보내는 계좌번호는 필수값입니다.")
        private String sentAccountNumber;

        @NotBlank(message = "받는 계좌번호는 필수값입니다.")
        private String receivedAccountNumber;

        @NotNull(message = "송금액은 필수값입니다.")
        @Min(value = 1, message = "송금 최소금액은 1원입니다.")
        private Long amount;
    }

    @Getter
    @Builder
    public static class Response {

        private String sentAccountNumber;

        private String receivedAccountNumber;

        private String receivedName;

        private Long amount;
    }
}