package com.kosa.moimeasy.user.exception;

import java.util.List;

public class InvalidPasswordResetException extends RuntimeException {
    private List<String> errors;

    public InvalidPasswordResetException(List<String> errors) {
        super("비밀번호 초기화 요청에 실패했습니다.");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
