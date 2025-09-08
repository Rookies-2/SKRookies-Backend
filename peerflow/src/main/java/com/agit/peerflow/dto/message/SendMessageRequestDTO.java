package com.agit.peerflow.dto.message;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequestDTO(
   @NotBlank String roomId,
   @NotBlank String content
) {}
