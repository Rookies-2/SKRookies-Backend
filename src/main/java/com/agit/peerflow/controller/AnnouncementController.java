package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.Announcement.AnnouncementDTO;
import com.agit.peerflow.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Announcement API", description = "공지사항 관리 API")
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @Operation(summary = "공지사항 생성", description = "ADMIN 또는 TEACHER 권한을 가진 사용자가 새로운 공지사항을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공지사항 생성 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AnnouncementDTO.Response> createAnnouncement(
            @RequestBody AnnouncementDTO.CreateRequest requestDto,
            @AuthenticationPrincipal User currentUser) {
        AnnouncementDTO.Response response = announcementService.createAnnouncement(requestDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "공지사항 단건 조회", description = "ID를 이용하여 특정 공지사항의 상세 내용을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 공지사항을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDTO.Response> getAnnouncement(
            @Parameter(description = "조회할 공지사항의 ID", example = "1") @PathVariable("id") Long announcementId) {
        AnnouncementDTO.Response response = announcementService.getAnnouncement(announcementId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공지사항 목록 조회", description = "모든 공지사항 목록을 최신순으로 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<Page<AnnouncementDTO.Response>> getAnnouncements(
            @Parameter(description = "페이지네이션 정보 (예: ?page=0&size=10&sort=createdAt,desc)") Pageable pageable) {
        Page<AnnouncementDTO.Response> responses = announcementService.getAnnouncements(pageable);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "공지사항 수정", description = "ADMIN 또는 작성자 본인이 ID를 이용하여 기존 공지사항의 제목과 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 공지사항을 찾을 수 없음")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AnnouncementDTO.Response> updateAnnouncement(
            @Parameter(description = "수정할 공지사항의 ID", example = "1") @PathVariable("id") Long announcementId,
            @RequestBody AnnouncementDTO.UpdateRequest requestDto,
            @AuthenticationPrincipal User currentUser) {
        AnnouncementDTO.Response response = announcementService.updateAnnouncement(announcementId, requestDto, currentUser);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공지사항 삭제", description = "ADMIN 또는 작성자 본인이 ID를 이용하여 공지사항을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 공지사항을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteAnnouncement(
            @Parameter(description = "삭제할 공지사항의 ID", example = "1") @PathVariable("id") Long announcementId,
            @AuthenticationPrincipal User currentUser) {
        announcementService.deleteAnnouncement(announcementId, currentUser);
        return ResponseEntity.noContent().build();
    }
}