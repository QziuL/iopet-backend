package github.qziul.iopet.controller.dto.response;

public record AuthResponseDTO(
        String token,
        TutorResponseDTO tutor)
{}
