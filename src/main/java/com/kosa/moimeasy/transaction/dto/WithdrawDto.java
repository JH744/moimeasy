package com.kosa.moimeasy.transaction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class WithdrawDto {

    @Getter
    @Setter
    public static class Request {

        @NotBlank(message = "계좌번호는 필수값입니다.")
        private String accountNumber;

        @NotBlank(message = "출금자명은 필수값입니다.")
        private String withdrawName;

        @Min(value = 1, message = "출금 최소금액은 1원입니다.")
        private Long amount;

        @NotBlank(message = "카테고리는 필수입니다.")
        private String categoryName;
    }

    @Getter
    @Builder
    public static class Response {

        private String accountNumber;
        private String withdrawName;
        private Long amount;
        private String categoryName;
        private LocalDateTime transacted_at;
    }
}
