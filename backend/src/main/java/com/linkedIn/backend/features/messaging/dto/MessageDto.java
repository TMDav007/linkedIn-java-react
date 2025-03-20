package com.linkedIn.backend.features.messaging.dto;

import java.util.UUID;

public record MessageDto(UUID receiverId, String content) {

}
