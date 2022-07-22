package com.artistack.base.constant;

import com.artistack.base.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum Code {

    // 충돌 방지를 위한 Code format
    // X1XXX: 제이
    // X2XXX: 셀리나
    // X3XXX: 메이슨
    // ex) 메이슨이 닉네임 중복 에러코드를 만든다면
    // USER_NICKNAME_DUPLICATED(13010, HttpStatus.BAD_REQUEST, "User nickname duplicated"),

    OK(0, HttpStatus.OK, "Ok"),

    BAD_REQUEST(10000, HttpStatus.BAD_REQUEST, "Bad request"),
    VALIDATION_ERROR(10001, HttpStatus.BAD_REQUEST, "Validation error"),
    NOT_FOUND(10002, HttpStatus.NOT_FOUND, "Requested resource is not found"),
    PROVIDER_TYPE_NOT_VALID(13001, HttpStatus.BAD_REQUEST, "Provider type not valid(kakao, apple)"),
    ARTISTACK_ID_DUPLICATED(13002, HttpStatus.BAD_REQUEST, "Artistack id duplicated"),
    ARTISTACK_ID_FORMAT_ERROR(13003, HttpStatus.BAD_REQUEST, "Artistack id format error"),
    USER_DESCRIPTION_FORMAT_ERROR(13004, HttpStatus.BAD_REQUEST, "User description format error"),
    NICKNAME_FORMAT_ERROR(13003, HttpStatus.BAD_REQUEST, "Nickname format error"),
    INSTRUMENT_NOT_VALID(13003, HttpStatus.BAD_REQUEST, "Instrument id not valid"),


    INTERNAL_ERROR(20000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error"),
    DATA_ACCESS_ERROR(20001, HttpStatus.INTERNAL_SERVER_ERROR, "Data access error"),
    KAKAO_SERVER_ERROR(23001, HttpStatus.INTERNAL_SERVER_ERROR , "Kakao server error"),


    UNAUTHORIZED(40000, HttpStatus.UNAUTHORIZED, "User unauthorized"),
    NOT_REGISTERED(43001, HttpStatus.OK, "Need registration"),
    ALREADY_REGISTERED(43002, HttpStatus.BAD_REQUEST, "You're already registered"),
    INVALID_REFRESH_TOKEN(43003, HttpStatus.UNAUTHORIZED, "Invalid refresh token. Sign in again"),
    REFRESH_TOKEN_NOT_FOUND(43004, HttpStatus.UNAUTHORIZED, "Refresh token not found. Sign in again"),
    MALFORMED_JWT(43005, HttpStatus.UNAUTHORIZED, "Malformed jwt format"),
    EXPIRED_ACCESS_TOKEN(43006, HttpStatus.UNAUTHORIZED, "Access token expired. Reissue it"),
    UNSUPPORTED_JWT(43007, HttpStatus.UNAUTHORIZED, "Unsupported jwt format"),
    ILLEGAL_JWT(43008, HttpStatus.UNAUTHORIZED, "Illegal jwt format"),

    ;



    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(Throwable e) {
        return this.getMessage(this.getMessage() + " - " + e.getMessage());
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
            .filter(Predicate.not(String::isBlank))
            .orElse(this.getMessage());
    }

    public static Code valueOf(HttpStatus httpStatus) {
        if (httpStatus == null) {
            throw new GeneralException("HttpStatus is null.");
        }

        return Arrays.stream(values())
            .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
            .findFirst()
            .orElseGet(() -> {
                if (httpStatus.is4xxClientError()) {
                    return Code.BAD_REQUEST;
                } else if (httpStatus.is5xxServerError()) {
                    return Code.INTERNAL_ERROR;
                } else {
                    return Code.OK;
                }
            });
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", this.name(), this.getCode());
    }
}
