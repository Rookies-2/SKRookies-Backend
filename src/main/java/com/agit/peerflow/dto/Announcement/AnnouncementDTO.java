package com.agit.peerflow.dto.Announcement;

import com.agit.peerflow.domain.entity.Announcement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AnnouncementDTO {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        private String title;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String content;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String title;
        private final String content;
        private final String authorNickname;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public Response(Announcement announcement) {
            this.id = announcement.getId();
            this.title = announcement.getTitle();
            this.content = announcement.getContent();
            this.authorNickname = announcement.getAuthor().getNickname();
            this.createdAt = announcement.getCreatedAt();
            this.updatedAt = announcement.getUpdatedAt();
        }
    }
}