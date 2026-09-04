package github.qziul.iopet.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TutorRequestDTO(
        @NotBlank @Size(max = 255) String nome,
        @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8) String senha,
        @NotBlank @Size(min = 11) String telefone,
        String urlFoto
) {}
