package com.doubleo.adminservice.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AdminErrorCode implements BaseErrorCode {
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "관리자 정보를 찾을 수 없습니다."),
    ADMIN_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 관리자입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "유효한 비밀번호가 아닙니다."),
    DUPLICATED_PASSWORD(HttpStatus.BAD_REQUEST, "변경되는 비밀번호는 기존 비밀번호와 동일할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
