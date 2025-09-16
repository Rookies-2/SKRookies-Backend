package com.agit.peerflow.service.impl; // [변경] impl 패키지로 이동

import com.agit.peerflow.domain.entity.History;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.HistoryType;
import com.agit.peerflow.dto.history.HistoryResponseDTO;
import com.agit.peerflow.exception.BusinessException; // [추가]
import com.agit.peerflow.exception.ErrorCode; // [추가]
import com.agit.peerflow.repository.HistoryRepository;
import com.agit.peerflow.service.HistoryService; // [추가]
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // [유지] 클래스 레벨 readOnly (좋은 습관입니다)
public class HistoryServiceImpl implements HistoryService { // [변경] 인터페이스 구현

    private final HistoryRepository historyRepository;

    @Override
    @Transactional // 쓰기 작업이므로 readOnly 해제
    public void createHistory(User user, String content, String relatedUrl, HistoryType historyType) {
        // History 엔티티의 정적 팩토리 메소드를 사용하여 객체를 생성합니다.
        History history = History.createHistory(user, content, relatedUrl, historyType);
        historyRepository.save(history);
    }

    @Override
    public List<HistoryResponseDTO> getUserHistories(Long userId) {
        // Repository를 통해 사용자 ID로 알림 목록을 조회하고, 최신순으로 정렬합니다.
        List<History> histories = historyRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        // 조회된 엔티티 리스트를 DTO 리스트로 변환하여 반환합니다.
        return histories.stream()
                .map(HistoryResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional // 쓰기 작업이므로 readOnly 해제
    public void markAsRead(Long historyId, Long userId) {
        // [변경] 헬퍼 메서드를 사용하여 조회 및 권한 확인
        History history = findHistoryByIdAndCheckOwnership(historyId, userId);

        // 알림 엔티티의 상태를 '읽음'으로 변경합니다.
        history.markAsRead();
    }

    @Override
    @Transactional // 쓰기 작업이므로 readOnly 해제
    public void deleteHistory(Long historyId, Long userId) {
        // [변경] 헬퍼 메서드를 사용하여 조회 및 권한 확인
        History history = findHistoryByIdAndCheckOwnership(historyId, userId);

        // 알림을 삭제합니다.
        historyRepository.delete(history);
    }

    /**
     * [추가] 알림 조회 및 소유권 확인 헬퍼 메서드 (중복 제거)
     */
    private History findHistoryByIdAndCheckOwnership(Long historyId, Long userId) {
        // 알림 ID로 알림 엔티티를 조회합니다.
        History history = historyRepository.findById(historyId)
                // [수정] IllegalArgumentException -> BusinessException
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "History", "id", String.valueOf(historyId)));

        // 현재 요청을 보낸 사용자가 알림의 주인인지 확인합니다.
        if (!history.getUser().getId().equals(userId)) {
            // [수정] SecurityException -> BusinessException
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "자신의 알림에만 접근할 수 있습니다.");
        }

        return history;
    }
}