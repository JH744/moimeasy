package com.kosa.moimeasy.transaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class PasswordCheckDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request{
        @NotNull
        private Long userId;

        @NotNull
        private String password;
    }

    @Getter
    @Builder
    public static class Response{
        private boolean isValid;
        private String message;
        private int remainingAttempts;
    }

}
