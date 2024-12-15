package com.kosa.moimeasy.common.exception;

public class NotExistMoeimAccountException extends RuntimeException {

    // 기본 생성자
    public NotExistMoeimAccountException() {
        super("존재하는 계좌가 없습니다");
    }

    // 사용자 정의 메시지 생성자
    public NotExistMoeimAccountException(String message) {
        super(message);
    }
}
