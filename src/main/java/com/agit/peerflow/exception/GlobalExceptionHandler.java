package com.agit.peerflow.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 프로젝트 전역에서 발생하는 모든 예외를 중앙에서 처리하는 핸들러입니다.
 * @RestControllerAdvice 는 모든 @RestController에서 발생하는 예외를 가로챕니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. 우리가 직접 정의한 BusinessException을 처리합니다.
     * 서비스 로직 전반에서 발생하는 대부분의 비즈니스 예외를 담당합니다.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        // BusinessException에 이미 HTTP 상태와 메시지가 잘 정의되어 있습니다.
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", ex.getHttpStatus().value());
        errorBody.put("message", ex.getMessage());

        log.warn("BusinessException occurred: {}", ex.getMessage());

        return new ResponseEntity<>(errorBody, ex.getHttpStatus());
    }

    /**
     * 2. @Valid 애노테이션을 통한 DTO 유효성 검증 실패 시 발생하는 예외를 처리합니다.
     * (예: @NotBlank, @Email 등에 맞지 않는 값이 들어올 경우)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 여러 유효성 검증 오류 중 첫 번째 오류 메시지를 가져옵니다.
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", HttpStatus.BAD_REQUEST.value());
        errorBody.put("message", errorMessage);

        log.warn("Validation failed: {}", errorMessage);

        return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    /**
     * 3. Spring Security에서 발생하는 AccessDeniedException을 처리합니다. (권한 없음 - 403)
     * (참고: SecurityConfig에 설정한 CustomAccessDeniedHandler가 이보다 먼저 동작할 수 있습니다.)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        BusinessException businessEx = new BusinessException(ErrorCode.ACCESS_DENIED);

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", businessEx.getHttpStatus().value());
        errorBody.put("message", businessEx.getMessage());

        log.warn("Access Denied: {}", ex.getMessage());

        return new ResponseEntity<>(errorBody, businessEx.getHttpStatus());
    }

    /**
     * 4. 위에서 처리하지 못한 모든 예상치 못한 예외 (500 Error)를 처리합니다. (안전망 역할)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllUncaughtException(Exception ex) {
        // 예상치 못한 오류는 반드시 로그에 전체 스택 트레이스를 남겨야 디버깅이 가능합니다.
        log.error("Unhandled server error occurred: {}", ex.getMessage(), ex);

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorBody.put("message", "서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요.");

        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}