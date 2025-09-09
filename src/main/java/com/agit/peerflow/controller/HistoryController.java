package com.agit.peerflow.controller;

import com.agit.peerflow.dto.history.HistoryResponse;
import com.agit.peerflow.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author  김현근
 * @version 1.1
 * @since   2025-09-08
 * @description 히스토리(알림) 컨트롤러 (파일 저장 기능 임시 비활성화)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/histories")
public class HistoryController {

    private final HistoryService historyService;

    /**
     * 특정 사용자의 모든 알림 조회
     */
    @GetMapping
    public ResponseEntity<List<HistoryResponse>> getUserHistories(@RequestParam Long userId) {
        List<HistoryResponse> histories = historyService.getUserHistories(userId);
        return ResponseEntity.ok(histories);
    }

    /**
     * 특정 알림을 읽음으로 표시
     */
    @PatchMapping("/{historyId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long historyId,
            @RequestParam Long userId) {
        historyService.markAsRead(historyId, userId);
        return ResponseEntity.ok().build();
    }
}