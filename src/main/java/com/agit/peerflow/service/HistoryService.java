package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.History;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.HistoryType;
import com.agit.peerflow.dto.history.HistoryResponse;
import com.agit.peerflow.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final HistoryRepository historyRepository;

    @Transactional
    public void createHistory(User user, String content, String relatedUrl, HistoryType historyType) {
        History history = History.createHistory(user, content, relatedUrl, historyType);
        historyRepository.save(history);
    }

    public List<HistoryResponse> getUserHistories(Long userId) {
        List<History> histories = historyRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        return histories.stream()
                .map(HistoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long historyId, Long userId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        if (!history.getUser().getUserId().equals(userId)) {
            throw new SecurityException("자신의 알림만 읽음 처리할 수 있습니다.");
        }
        history.markAsRead();
    }
}