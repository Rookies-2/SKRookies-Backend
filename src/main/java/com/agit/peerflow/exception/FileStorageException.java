package com.agit.peerflow.exception;

/**
 * 애플리케이션 시작 시 파일 저장소 초기화 실패를 위한 예외
 */
public class FileStorageException extends RuntimeException {
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}