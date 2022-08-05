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

    BAD_REQUEST(2000, HttpStatus.BAD_REQUEST, "Bad request"),
    VALIDATION_ERROR(2001, HttpStatus.BAD_REQUEST, "Validation error"),
    PROVIDER_TYPE_NOT_VALID(2101, HttpStatus.BAD_REQUEST, "Provider type not valid(kakao, apple)"),
    ARTISTACK_ID_DUPLICATED(2102, HttpStatus.BAD_REQUEST, "Artistack id duplicated"),
    ARTISTACK_ID_FORMAT_ERROR(2103, HttpStatus.BAD_REQUEST, "Artistack id format error"),
    USER_DESCRIPTION_FORMAT_ERROR(2104, HttpStatus.BAD_REQUEST, "User description format error"),
    NICKNAME_FORMAT_ERROR(2105, HttpStatus.BAD_REQUEST, "Nickname format error"),
    INSTRUMENT_ID_NOT_VALID(2106, HttpStatus.BAD_REQUEST, "Instrument id not valid"),


    INTERNAL_ERROR(4000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error"),
    DATA_ACCESS_ERROR(4001, HttpStatus.INTERNAL_SERVER_ERROR, "Data access error"),
    KAKAO_SERVER_ERROR(4002, HttpStatus.INTERNAL_SERVER_ERROR , "Kakao server error"),
    APPLE_SERVER_ERROR(4003, HttpStatus.INTERNAL_SERVER_ERROR , "Apple server error"),
    S3_UPLOAD_ERROR(4004, HttpStatus.INTERNAL_SERVER_ERROR, "S3 upload error"),


    UNAUTHORIZED(5000, HttpStatus.UNAUTHORIZED, "User unauthorized"),
    NOT_REGISTERED(5001, HttpStatus.OK, "Need registration"),
    ALREADY_REGISTERED(5002, HttpStatus.BAD_REQUEST, "You're already registered"),
    INVALID_REFRESH_TOKEN(5003, HttpStatus.UNAUTHORIZED, "Invalid refresh token. Sign in again"),
    REFRESH_TOKEN_NOT_FOUND(5004, HttpStatus.UNAUTHORIZED, "Refresh token not found. Sign in again"),
    MALFORMED_JWT(5005, HttpStatus.UNAUTHORIZED, "Malformed jwt format"),
    EXPIRED_JWT(5006, HttpStatus.UNAUTHORIZED, "Jwt expired. Reissue it"),
    UNSUPPORTED_JWT(5007, HttpStatus.UNAUTHORIZED, "Unsupported jwt format"),
    ILLEGAL_JWT(5008, HttpStatus.UNAUTHORIZED, "Illegal jwt format"),

    EMPTY_VIDEO(2201, HttpStatus.BAD_REQUEST, "Video file is empty"),

    TITLE_TOO_LONG(2202, HttpStatus.BAD_REQUEST, "Title is too long"),
    DESCRIPTION_TOO_LONG(2203, HttpStatus.BAD_REQUEST, "Description is too long"),
    PREV_PROJECT_NOT_EXIST(2204, HttpStatus.BAD_REQUEST, "Previous project doesn't exist"),
    PREV_PROJECT_NOT_STACKABLE(2205, HttpStatus.BAD_REQUEST, "Previous project isn't stackable"),
    INVALID_SEQUENCE(2206, HttpStatus.BAD_REQUEST, "Invalid sequence"),
    PROJECT_NOT_FOUND(2207, HttpStatus.BAD_REQUEST, "Cannot find project from DB"),
    INVALID_INSTRUMENT(2301, HttpStatus.BAD_REQUEST, "Invalid instrument"),
    USER_NOT_FOUND(4100, HttpStatus.BAD_REQUEST, "Cannot find user from DB"),
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
