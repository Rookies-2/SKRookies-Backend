// repository/NotificationRepository.java

package com.agit.peerflow.repository;

import com.agit.peerflow.domain.Notification;
// import com.agit.peerflow.domain.User; // 더 이상 필요 없음
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // User 객체 대신 Long userId를 받도록 수정
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}