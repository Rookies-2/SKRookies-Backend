package com.agit.peerflow.repository;

import com.agit.peerflow.domain.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author  김현근
 * @version 1.0
 * @since   2025-09-16
 * @description 공지사항(Announcement) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository.
 *              생성일시 기준 내림차순으로 정렬된 공지사항 목록을 페이징 처리하여 조회하는 기능을 제공한다.
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Page<Announcement> findAllByOrderByCreatedAtDesc(Pageable pageable);
}