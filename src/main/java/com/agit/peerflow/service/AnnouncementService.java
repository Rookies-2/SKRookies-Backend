package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.dto.Announcement.AnnouncementDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnnouncementService {
    AnnouncementDTO.Response createAnnouncement(AnnouncementDTO.CreateRequest requestDto, User author);
    AnnouncementDTO.Response getAnnouncement(Long announcementId);
    Page<AnnouncementDTO.Response> getAnnouncements(Pageable pageable);
    AnnouncementDTO.Response updateAnnouncement(Long announcementId, AnnouncementDTO.UpdateRequest requestDto, User currentUser);
    void deleteAnnouncement(Long announcementId, User currentUser);
}