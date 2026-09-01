package github.qziul.iopet.controller.dto.response;

import github.qziul.iopet.domain.model.Tutor;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TutorResponseDTO {
    private UUID uuid;
    private String nome, email, telefone, urlFoto;

    public TutorResponseDTO(Tutor tutor) {
        this.uuid = tutor.getUuid();
        this.nome = tutor.getNome();
        this.email = tutor.getEmail();
        this.telefone = tutor.getTelefone();
        this.urlFoto = tutor.getUrlFoto();
    }
}
