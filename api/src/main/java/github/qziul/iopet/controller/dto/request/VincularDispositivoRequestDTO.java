package github.qziul.iopet.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VincularDispositivoRequestDTO(
        @NotNull UUID idPublicoPet,
        @NotBlank String enderecoMac
) {
}
