package github.qziul.iopet.controller.dto.response;

import java.util.UUID;

public record AlertaResponseDTO(
        Long id,
        UUID petId,
        String petName,
        String petAvatarUrl,
        String type,
        String severity,
        String title,
        String message,
        String timestamp,
        boolean read
) {}
