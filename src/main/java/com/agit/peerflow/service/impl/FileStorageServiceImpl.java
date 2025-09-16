package com.agit.peerflow.service.impl; // [변경] impl 패키지로 이동

import com.agit.peerflow.exception.BusinessException;
import com.agit.peerflow.exception.ErrorCode;
import com.agit.peerflow.exception.FileStorageException;
import com.agit.peerflow.service.FileStorageService;
import jakarta.annotation.PostConstruct; // [추가]
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // [추가]
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService { // [변경] 인터페이스 구현

    // [유지] 하드코딩된 경로 (최소 변경 원칙)
    private final Path fileStorageLocation = Paths.get("./peerflow/uploads").toAbsolutePath().normalize();

    // [변경] 생성자 대신 @PostConstruct로 초기화 로직 이동
    @PostConstruct
    public void init() {
        try {
            // 업로드 디렉터리 생성
            Files.createDirectories(this.fileStorageLocation);
        } catch(IOException ex) { // [변경] 명확한 예외 타입
            // [변경] 애플리케이션 구동 실패용 커스텀 예외
            throw new FileStorageException("파일을 업로드할 디렉터리를 생성할 수 없습니다.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        // [변경] StringUtils.cleanPath로 경로 조작 방지
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        if (file == null || file.isEmpty() || originalFilename == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "업로드할 파일이 없습니다.");
        }

        // 확장자 추출 (예: ".png")
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // [수정] 버그 수정 (uuid..png -> uuid.png)
        String storedFileName = UUID.randomUUID().toString() + extension;

        try {
            // [수정] 보안 검사 (path traversal 방지)
            if(storedFileName.contains("..")) {
                // [수정] RuntimeException -> BusinessException
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "파일 이름에 부적절한 문자가 포함되어 있습니다.");
            }

            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            // 파일 저장
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // [수정] 버그 수정: 실제 다운로드 컨트롤러 경로로 URL 생성
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/") // [수정] "/files/" -> "/api/files/download/"
                    .path(storedFileName)
                    .toUriString();
        } catch (IOException e) {
            // [수D] RuntimeException -> BusinessException
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 " + storedFileName + " 을(를) 저장할 수 없습니다.");
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                // [수정] RuntimeException -> BusinessException
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "파일을 찾을 수 없습니다: " + fileName);
            }
        } catch (MalformedURLException ex) {
            // [수정] RuntimeException -> BusinessException
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "파일 경로가 유효하지 않습니다: " + fileName, ex);
        }
    }
}