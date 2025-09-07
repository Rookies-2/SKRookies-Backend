package com.agit.peerflow.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
   @NotBlank String roomId,
   @NotBlank String content
) {}
