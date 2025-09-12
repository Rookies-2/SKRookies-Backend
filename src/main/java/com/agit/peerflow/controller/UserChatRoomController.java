package com.agit.peerflow.controller;

import com.agit.peerflow.service.UserChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms")
public class UserChatRoomController {
    private final UserChatRoomService userChatRoomService;

    @PatchMapping("/{roomId}/mute")
    public ResponseEntity<Void> toggleMute(@PathVariable Long roomId, Principal principal) {
        userChatRoomService.toggleMute(principal.getName(), roomId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{roomId}/pin")
    public ResponseEntity<Void> togglePin(@PathVariable Long roomId, Principal principal) {
        userChatRoomService.togglePin(principal.getName(), roomId);
        return ResponseEntity.ok().build();
    }

}
