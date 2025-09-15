package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.history.HistoryResponseDTO;
import com.agit.peerflow.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "History API", description = "사용자 알림(히스토리) 조회 및 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/histories")
public class HistoryController {

    private final HistoryService historyService;

    @Operation(summary = "내 알림 목록 조회", description = "현재 로그인된 사용자의 모든 알림 목록을 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<HistoryResponseDTO>> getUserHistories(@AuthenticationPrincipal User user) {
        List<HistoryResponseDTO> histories = historyService.getUserHistories(user.getId());
        return ResponseEntity.ok(histories);
    }

    @Operation(summary = "특정 알림 읽음 처리", description = "ID에 해당하는 알림을 '읽음' 상태로 변경합니다.")
    @PatchMapping("/{historyId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long historyId,
            @AuthenticationPrincipal User user) {
        historyService.markAsRead(historyId, user.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 알림 삭제", description = "ID에 해당하는 자신의 알림을 삭제합니다.")
    @DeleteMapping("/{historyId}")
    public ResponseEntity<Void> deleteHistory(
            @PathVariable Long historyId,
            @AuthenticationPrincipal User user) {
        historyService.deleteHistory(historyId, user.getId());
        // 성공적으로 삭제되면 204 No Content 응답을 보냅니다.
        return ResponseEntity.noContent().build();
    }
}