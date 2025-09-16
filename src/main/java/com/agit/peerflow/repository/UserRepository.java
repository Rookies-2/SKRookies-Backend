package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author  백두현
 * @version 1.0
 * @since   2025-09-16
 * @description User 엔티티에 대한 데이터 접근을 담당하는 JPA Repository 인터페이스.
 *              이메일, 닉네임, 상태, 역할 등을 기준으로 사용자 조회 및 검색 기능을 제공한다.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 검색
    Optional<User> findByEmail(String email);
    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);
    // 닉네임 존재 여부 확인
    boolean existsByNickname(String nickname);
    //Paging 과 Search(검색) 관련 메서드들
    // 사용자 승인상태
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    // 사용자 역할로 검색
    Page<User> findByRole(UserRole role, Pageable pageable);
    //공지사항 역할
    List<User> findAllByRole(UserRole role);
    //상태가 ACTIVE인 유저 전체 조회
    List<User> findAllByStatus(UserStatus status);

    // 상태가 ACTIVE인 유저를 이메일이나 이름으로 알아내기
    @Query("""
        SELECT u 
        FROM User u
        WHERE u.status = com.agit.peerflow.domain.enums.UserStatus.ACTIVE
          AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    List<User> findActiveUsersByUsernameOrEmail(@Param("keyword") String keyword);

    List<User> findByLastLoggedInAtBeforeAndStatus(LocalDateTime dateTime, UserStatus status);

    @EntityGraph(attributePaths = "userChatRooms")
    Optional<User> findWithChatRoomsById(Long id);
}
