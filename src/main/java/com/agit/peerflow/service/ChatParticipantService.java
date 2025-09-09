package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.ChatParticipant;
import com.agit.peerflow.domain.entity.ChatRoom;
import com.agit.peerflow.domain.entity.User;

import java.util.List;

public interface ChatParticipantService {
    void joinRoom(User user, ChatRoom chatRoom);
    void leaveRoom(User user, ChatRoom chatRoom);
    List<ChatParticipant> getParticipants(ChatRoom chatRoom);
}