package com.mentit.mento.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    // 500 - 일반적인 예외
    NOT_HANDLED_EXCEPTION(INTERNAL_SERVER_ERROR, "처리되지 않은 예외가 발생했습니다.", 500),
    REDIS_CONNECTION_FAILURE(INTERNAL_SERVER_ERROR, "Redis에 연결할 수 없습니다.", 500),
    UNEXPECTED_ERROR(INTERNAL_SERVER_ERROR, "예기치 않은 오류가 발생했습니다.", 500),

    // 400 - 잘못된 요청
    NOT_FOUND_REFRESH_TOKEN_IN_COOKIE(BAD_REQUEST, "쿠키에서 리프레시 토큰을 찾을 수 없습니다.", 400),
    INVALID_PARAMETER(BAD_REQUEST, "잘못된 요청 파라미터입니다.", 400),
    INVALID_FILE_EXTENTION(BAD_REQUEST, "잘못된 파일 확장자입니다.", 400),

    // 사용자 관련 에러 (800)
    TOO_SHORT_NICKNAME(BAD_REQUEST, "닉네임은 2자 이상이어야 합니다.", 800),
    TOO_LONG_NICKNAME(BAD_REQUEST, "닉네임은 10자 이하이어야 합니다.", 801),
    NICKNAME_PATTERN_INVALIDATION(BAD_REQUEST, "닉네임은 띄어쓰기 없이 한글, 영문, 숫자만 가능합니다.", 802),
    NOT_FOUND_MEMBER(CONFLICT, "사용자를 찾을 수 없습니다.", 803),
    MEMBER_ALREADY_WITHDRAW(CONFLICT, "이미 탈퇴한 사용자입니다.", 805),
    DUPLICATE_LOGIN(UNAUTHORIZED, "중복 로그인입니다.", 806),
    ALREADY_ENROLLED_ACCOUNT(CONFLICT, "이미 등록된 계정입니다.", 807),
    NICKNAME_NOT_MATCH(CONFLICT, "닉네임이 일치하지 않습니다.", 809),
    CANT_FIND_USERSTATUS(CONFLICT, "사용자 상태를 찾을 수 없습니다.", 810),

    // 게시판 관련 에러 (700)
    NOT_FOUND_BOARD(CONFLICT, "게시글을 찾을 수 없습니다.", 700),
    NOT_FOUND_MORE_THAN_3_BOARDS(CONFLICT, "3개 이상의 게시글을 찾을 수 없습니다.", 702),
    NOT_MATCHED_WRITER(CONFLICT, "작성자가 일치하지 않습니다.", 703),
    FILE_IS_EMPTY(CONFLICT, "파일이 비어 있습니다.", 704),
    NO_FILE_EXTENTION(CONFLICT, "파일 확장자가 없습니다.", 705),
    IO_EXCEPTION_ON_IMAGE_UPLOAD(CONFLICT, "이미지 업로드 중 IO 예외가 발생했습니다.", 706),
    PUT_OBJECT_EXCEPTION(CONFLICT, "객체 업로드 중 예외가 발생했습니다.", 707),
    IO_EXCEPTION_ON_IMAGE_DELETE(CONFLICT, "이미지 삭제 중 IO 예외가 발생했습니다.", 708),

    // 도토리 토큰 관련 에러 (600)
    NOT_FOUND_DOTORI_TOKEN(CONFLICT, "도토리 토큰을 찾을 수 없습니다.", 600),

    // 댓글 관련 에러 (710)
    NOT_FOUND_COMMENT(CONFLICT, "댓글을 찾을 수 없습니다.", 710),

    // 토큰 관련 에러 (901)
    TOKEN_EXPIRED(UNAUTHORIZED, "토큰이 만료되었습니다.", 901),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다.", 902),
    UNSUPPORTED_TOKEN(UNAUTHORIZED, "지원되지 않는 토큰 형식입니다.", 903),
    NOT_FOUND_TOKEN(UNAUTHORIZED, "토큰을 찾을 수 없습니다.", 904),
    NOT_FOUND_REFRESH_TOKEN(UNAUTHORIZED, "사용자의 리프레시 토큰을 찾을 수 없습니다.", 905),
    MALFORMED_TOKEN(UNAUTHORIZED, "손상된 토큰입니다.", 906),

    // 등록 관련 에러 (403)
    INVALID_ENUM_PARAMETER(CONFLICT, "잘못된 열거형 파라미터입니다.", 403),
    ACCESS_DENIED(UNAUTHORIZED, "접근이 거부되었습니다.", 401);

    private final HttpStatus httpStatus;
    private final String message;
    private final Integer code;
}