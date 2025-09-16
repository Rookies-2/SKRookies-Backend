package com.agit.peerflow.exception;//ErrorCode enum 상수정의

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common Errors
    INVALID_INPUT_VALUE("입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("%s(을)를 찾을 수 없습니다. (%s: %s)", HttpStatus.NOT_FOUND),
    RESOURCE_DUPLICATE("이미 존재하는 %s입니다. (%s: %s)", HttpStatus.CONFLICT),

    // Request Errors
    TOO_MANY_REQUESTS("요청 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.TOO_MANY_REQUESTS),


    // User Errors
    PASSWORD_SAME_AS_CURRENT("새 비밀번호는 현재 비밀번호와 동일할 수 없습니다.", HttpStatus.BAD_REQUEST),
    // 권한 관련
    ACCESS_DENIED("🚫 권한이 없습니다.", HttpStatus.FORBIDDEN),

    AI_BLOCKED("🚫 AI 보안 시스템에 의해 %s 차단되었습니다. 사용자: %s", HttpStatus.FORBIDDEN),

    // 서버 내부 오류 (500)
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String messageTemplate;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}