package com.agit.peerflow.dto.history;

import com.agit.peerflow.domain.entity.History;
import com.agit.peerflow.domain.enums.HistoryType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HistoryResponse {
    private final Long id;
    private final String content;
    private final String relatedUrl;
    private final boolean isRead;
    private final HistoryType historyType;
    private final LocalDateTime createdAt;

    public static HistoryResponse from(History history) {
        return HistoryResponse.builder()
                .id(history.getId())
                .content(history.getContent())
                .relatedUrl(history.getRelatedUrl())
                .isRead(history.isRead())
                .historyType(history.getHistoryType())
                .createdAt(history.getCreatedAt())
                .build();
    }
}