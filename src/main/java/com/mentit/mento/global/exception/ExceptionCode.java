package com.mentit.mento.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    // 500 - General Exceptions
    NOT_HANDLED_EXCEPTION(INTERNAL_SERVER_ERROR, "Unhandled exception occurred.", 500),
    REDIS_CONNECTION_FAILURE(INTERNAL_SERVER_ERROR, "Failed to connect to Redis.", 500),
    UNEXPECTED_ERROR(INTERNAL_SERVER_ERROR, "An unexpected error occurred.", 500),

    // 400 - Bad Request
    PASSWORD_MISMATCH(BAD_REQUEST, "Passwords do not match.", 400),
    INVALID_CURRENT_PASSWORD(BAD_REQUEST, "The current password is incorrect.", 400),
    INVALID_AUTH_CODE(BAD_REQUEST, "Invalid email authentication code.", 400),
    NOT_FOUND_REFRESH_TOKEN_IN_COOKIE(BAD_REQUEST, "Refresh token not found in cookie.", 400),
    INVALID_PARAMETER(BAD_REQUEST, "Invalid request parameter.", 400),
    INVALID_FILE_EXTENTION(BAD_REQUEST, "Invalid File Extention", 400),

    // User-related Errors (800)
    TOO_SHORT_NICKNAME(BAD_REQUEST, "닉네임은 2자 이상 적어주세요.", 800),
    TOO_LONG_NICKNAME(BAD_REQUEST, "닉네임은 10자 이하 적어주세요.", 801),
    NICKNAME_PATTERN_INVALIDATION(BAD_REQUEST, "닉네임은 띄어쓰기 없이 한글, 영문, 숫자만 가능합니다.", 802),
    NOT_FOUND_MEMBER(CONFLICT, "Member not found.", 803),
    MEMBER_ALREADY_EXISTS(CONFLICT, "Member already exists.", 804),
    MEMBER_ALREADY_WITHDRAW(CONFLICT, "Member has already withdrawn.", 805),
    DUPLICATE_LOGIN(UNAUTHORIZED, "Duplicate Login", 806),
    ALREADY_ENROLLED_ACCOUNT(CONFLICT, "Already Enrolled Account", 807),
    NICKNAME_NOT_MATCH(CONFLICT, "Nickname does not match.", 809),
    CANT_FIND_USERSTATUS(CONFLICT, "Can't Find UserStatus", 810),

    // Board-related Errors (700)
    NOT_FOUND_BOARD(CONFLICT, "Board not found.", 700),
    INVALID_BOARD(CONFLICT, "Invalid board.", 701),
    NOT_FOUND_MORE_THAN_3_BOARDS(CONFLICT, "Can't find more than 3 Boards.", 702),
    NOT_MATCHED_WRITER(CONFLICT, "Not matched Writer",703 ),
    FILE_IS_EMPTY(CONFLICT, "File is Empty.", 704),
    NO_FILE_EXTENTION(CONFLICT, "No file extension.", 705),
    IO_EXCEPTION_ON_IMAGE_UPLOAD(CONFLICT, "IO Exception happened on Image Upload.", 706),
    PUT_OBJECT_EXCEPTION(CONFLICT, "Put Object Exception.", 707),
    IO_EXCEPTION_ON_IMAGE_DELETE(CONFLICT, "IO Exception on Image Delete.", 708),

    // Comment-related Errors (710)
    NOT_FOUND_COMMENT(CONFLICT, "Can't Find Comment.", 710),

    // Token-related Errors (901)
    TOKEN_EXPIRED(UNAUTHORIZED, "Token has expired.", 902),
    INVALID_TOKEN(UNAUTHORIZED, "Invalid token provided.", 902),
    UNSUPPORTED_TOKEN(UNAUTHORIZED, "Token format is unsupported.", 903),
    NOT_FOUND_TOKEN(UNAUTHORIZED, "Token not found.", 904),
    NOT_FOUND_REFRESH_TOKEN(UNAUTHORIZED, "Refresh token not found for the user.", 905),
    MALFORMED_TOKEN(UNAUTHORIZED, "Malformed token.", 906),

    // Registration-related Errors (403)
    INVALID_ENUM_PARAMETER(CONFLICT, "Invalid Enum Parameter", 404),
    ACCESS_DENIED(UNAUTHORIZED,"access_denied" ,401 )
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private final Integer code;
}