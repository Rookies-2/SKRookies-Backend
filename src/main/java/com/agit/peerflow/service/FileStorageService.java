package com.agit.peerflow.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * 파일을 저장하고 접근 URL을 반환합니다.
     * @param file 업로드된 파일
     * @return 저장된 파일에 접근 가능한 URL
     */
    String storeFile(MultipartFile file);

    /**
     * 파일명을 기반으로 파일을 리소스로 로드합니다.
     * @param fileName 로드할 파일명
     * @return 파일 리소스
     */
    Resource loadFileAsResource(String fileName);
}