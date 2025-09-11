package com.agit.peerflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * @author    백두현
 * @version   1.0
 * @since     2025-09-11
 * @description
 * - 파일 업로드 및 다운로드를 처리하는 서비스 클래스
 * - 업로드 디렉터리 생성, 파일 저장, 파일 로드 기능 제공
 */
@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("./peerflow/uploads").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch(Exception ex) {
            throw new RuntimeException("파일을 업로드할 디렉터리를 생성할 수 없습니다.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String storedFileName = UUID.randomUUID().toString() + "." + extension;

        try {
            if(storedFileName.contains(".")) {
                throw new RuntimeException("파일 이름에 부적절한 문자가 포함되어 있습니다.");
            }

            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 파일에 접근할 수 있는 URL을 생성하여 반환
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/") // 파일을 제공할 경로
                    .path(storedFileName)
                    .toUriString();
        } catch (IOException e) {
            throw new RuntimeException("파일 " + storedFileName + " 을(를) 저장할 수 없습니다.");
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName, ex);
        }
    }
}
