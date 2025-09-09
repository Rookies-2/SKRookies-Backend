package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.history.HistoryResponseDTO;
import com.agit.peerflow.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author    김현근
 * @version   1.0.0
 * @since     2025-09-08
 * @description
 * - 사용자 알림(History) 관련 REST API 컨트롤러
 * - 알림 목록 조회 및 읽음 처리 기능 제공
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/histories")
public class HistoryController {

    private final HistoryService historyService;

    /**
     * [모든 사용자] 현재 로그인된 사용자의 모든 알림 목록을 최신순으로 조회합니다.
     * @param user 현재 인증된 사용자의 엔티티 객체
     * @return 알림 목록 (HistoryResponseDTO) 리스트와 200 OK 응답
     */
    @GetMapping
    public ResponseEntity<List<HistoryResponseDTO>> getUserHistories(@AuthenticationPrincipal User user) {
        // 현재 사용자의 ID를 이용해 HistoryService에서 알림 목록을 가져옵니다.
        List<HistoryResponseDTO> histories = historyService.getUserHistories(user.getId());
        return ResponseEntity.ok(histories);
    }

    /**
     * [모든 사용자] 특정 알림을 '읽음' 상태로 변경합니다.
     * @param historyId 읽음 처리할 알림의 ID
     * @param user 현재 인증된 사용자의 엔티티 객체
     * @return 200 OK 응답
     */
    @PatchMapping("/{historyId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long historyId,
            @AuthenticationPrincipal User user) {
        // 알림 ID와 사용자 ID를 함께 넘겨서, 본인 알림만 수정할 수 있도록 합니다.
        historyService.markAsRead(historyId, user.getId());
        return ResponseEntity.ok().build();
    }
}