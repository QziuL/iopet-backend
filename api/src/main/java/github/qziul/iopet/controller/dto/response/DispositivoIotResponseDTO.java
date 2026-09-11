package github.qziul.iopet.controller.dto.response;

import java.time.LocalDateTime;

public record DispositivoIotResponseDTO(
        String enderecoMac,
        int bateriaNivel,
        boolean ativo,
        LocalDateTime ultimaLocalizacao
) { }
