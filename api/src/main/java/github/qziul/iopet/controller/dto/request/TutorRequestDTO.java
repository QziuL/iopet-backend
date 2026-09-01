package github.qziul.iopet.controller.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TutorRequestDTO {
    private String nome, email, senha, telefone, urlFoto;
}
