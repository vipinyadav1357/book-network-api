package com.vipin.book.exceptionhandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BussinessErrorCodes {

    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No Code"),

    INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "current password does not matched"),

    NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "new password does not matched"),

    ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "User Account is locked"),

    ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "User Account is disabled"),

    BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "credential not matched");

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    private BussinessErrorCodes(Integer code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

}
