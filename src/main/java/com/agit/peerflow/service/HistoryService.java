package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.HistoryType;
import com.agit.peerflow.dto.history.HistoryResponseDTO;

import java.util.List;

public interface HistoryService {

    /**
     * 특정 사용자에게 새로운 알림을 생성하고 저장합니다.
     */
    void createHistory(User user, String content, String relatedUrl, HistoryType historyType);

    /**
     * 특정 사용자의 모든 알림 목록을 최신순으로 조회합니다.
     */
    List<HistoryResponseDTO> getUserHistories(Long userId);

    /**
     * 특정 알림을 '읽음' 상태로 변경합니다.
     */
    void markAsRead(Long historyId, Long userId);

    /**
     * 특정 알림을 삭제합니다.
     */
    void deleteHistory(Long historyId, Long userId);
}