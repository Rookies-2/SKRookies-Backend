package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.History;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.HistoryType;
import com.agit.peerflow.dto.history.HistoryResponseDTO;
import com.agit.peerflow.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 김현근
 * @version 1.0.0
 * @since 2025-09-10
 * @description
 * - 알림(History) 관련 비즈니스 로직을 처리하는 서비스 클래스
 * - 알림 생성, 조회, 읽음 처리 기능 제공
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final HistoryRepository historyRepository;

    /**
     * 특정 사용자에게 새로운 알림을 생성하고 저장합니다.
     *
     * @param user        알림을 받을 사용자 엔티티
     * @param content     알림 내용
     * @param relatedUrl  알림 클릭 시 이동할 URL
     * @param historyType 알림의 종류 (예: 과제, 메시지)
     */
    @Transactional
    public void createHistory(User user, String content, String relatedUrl, HistoryType historyType) {
        // History 엔티티의 정적 팩토리 메소드를 사용하여 객체를 생성합니다.
        History history = History.createHistory(user, content, relatedUrl, historyType);
        historyRepository.save(history);
    }

    /**
     * 특정 사용자의 모든 알림 목록을 최신순으로 조회합니다.
     *
     * @param userId 알림을 조회할 사용자의 ID
     * @return 알림 정보(DTO) 리스트
     */
    public List<HistoryResponseDTO> getUserHistories(Long userId) {
        // Repository를 통해 사용자 ID로 알림 목록을 조회하고, 최신순으로 정렬합니다.
        List<History> histories = historyRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        // 조회된 엔티티 리스트를 DTO 리스트로 변환하여 반환합니다.
        return histories.stream()
                .map(HistoryResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 알림을 '읽음' 상태로 변경합니다.
     *
     * @param historyId 읽음 처리할 알림의 ID
     * @param userId    현재 요청을 보낸 사용자의 ID
     */
    @Transactional
    public void markAsRead(Long historyId, Long userId) {
        // 알림 ID로 알림 엔티티를 조회합니다. 없으면 예외를 발생시킵니다.
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        // 현재 요청을 보낸 사용자가 알림의 주인인지 확인하여 보안을 강화합니다.
        if (!history.getUser().getId().equals(userId)) {
            throw new SecurityException("자신의 알림만 읽음 처리할 수 있습니다.");
        }

        // 알림 엔티티의 상태를 '읽음'으로 변경합니다.
        history.markAsRead();
    }
}