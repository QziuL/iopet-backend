package github.qziul.iopet.controller.dto.response;

import github.qziul.iopet.domain.model.DispositivoIot;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispositivoResponseDTO(
        String enderecoMac,
        UUID petUuid,
        String petNome,
        String petFoto,
        int bateriaNivel,
        boolean ativo,
        LocalDateTime ultimaComunicacao,
        String status
) {
    public DispositivoResponseDTO(DispositivoIot d) {
        this(
                d.getEnderecoMac(),
                d.getPet() != null ? d.getPet().getUuid() : null,
                d.getPet() != null ? d.getPet().getNome() : null,
                d.getPet() != null ? d.getPet().getUrlFoto() : null,
                d.getBateriaNivel(),
                d.isAtivo(),
                d.getUltimaComunicacao(),
                d.isAtivo() ? "online" : "offline"
        );
    }
}
