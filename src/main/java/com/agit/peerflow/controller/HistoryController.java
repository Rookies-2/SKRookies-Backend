package com.agit.peerflow.controller;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.history.HistoryResponseDTO;
import com.agit.peerflow.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/histories")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<List<HistoryResponseDTO>> getUserHistories(@AuthenticationPrincipal User user) {
        List<HistoryResponseDTO> histories = historyService.getUserHistories(user.getId());
        return ResponseEntity.ok(histories);
    }

    @PatchMapping("/{historyId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long historyId,
            @AuthenticationPrincipal User user) {
        historyService.markAsRead(historyId, user.getId());
        return ResponseEntity.ok().build();
    }
}