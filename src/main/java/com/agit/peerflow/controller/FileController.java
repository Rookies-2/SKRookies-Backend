package com.agit.peerflow.controller;

import com.agit.peerflow.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService fileStorageService;

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file")MultipartFile file) {
        if(file.isEmpty()) {
            return ResponseEntity.badRequest().body("업로드할 파일을 선택해주세요.");
        }

        // 파일을 저장하고, 접근 가능한 URL을 반환
        String  fileUrl = fileStorageService.storeFile(file);

        // view에게 JSON으로 파일 URL 반환
        return ResponseEntity.ok(Map.of("fileUrl", fileUrl));

    }

    // 파일 다운로드
    @PostMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        // 파일의 MIME 타입을 결정합니다.
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // MIME 타입을 결정할 수 없는 경우 기본값 사용
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // Content-Disposition 헤더 설정: 이 헤더가 있어야 "파일 다운로드" 다이얼로그가 뜸.
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
