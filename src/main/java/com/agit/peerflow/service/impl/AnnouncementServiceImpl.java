package com.agit.peerflow.service.impl;

import com.agit.peerflow.domain.entity.Announcement;
import com.agit.peerflow.domain.entity.History;
import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.domain.enums.UserRole;
import com.agit.peerflow.dto.Announcement.AnnouncementDTO;
import com.agit.peerflow.repository.AnnouncementRepository;
import com.agit.peerflow.repository.HistoryRepository;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.AnnouncementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.agit.peerflow.domain.enums.HistoryType;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    @Override
    @Transactional
    public AnnouncementDTO.Response createAnnouncement(AnnouncementDTO.CreateRequest requestDto, User author) {
        Announcement announcement = new Announcement(requestDto.getTitle(), requestDto.getContent(), author);
        announcementRepository.save(announcement);

        List<User> students = userRepository.findAllByRole(UserRole.STUDENT);
        String notificationContent = String.format("새로운 공지사항이 등록되었습니다: %s", announcement.getTitle());

        List<History> histories = students.stream()
                .map(student -> new History(student, notificationContent, "/announcements/" + announcement.getId(), HistoryType.ANNOUNCEMENT))
                .collect(Collectors.toList());

        historyRepository.saveAll(histories);

        return new AnnouncementDTO.Response(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementDTO.Response getAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + announcementId));
        return new AnnouncementDTO.Response(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO.Response> getAnnouncements(Pageable pageable) {
        return announcementRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(AnnouncementDTO.Response::new);
    }

    @Override
    @Transactional
    public AnnouncementDTO.Response updateAnnouncement(Long announcementId, AnnouncementDTO.UpdateRequest requestDto, User currentUser) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + announcementId));

        if (!announcement.getAuthor().getId().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("공지사항을 수정할 권한이 없습니다.");
        }

        announcement.update(requestDto.getTitle(), requestDto.getContent());
        return new AnnouncementDTO.Response(announcement);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long announcementId, User currentUser) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + announcementId));

        if (!announcement.getAuthor().getId().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("공지사항을 삭제할 권한이 없습니다.");
        }

        announcementRepository.delete(announcement);
    }
}