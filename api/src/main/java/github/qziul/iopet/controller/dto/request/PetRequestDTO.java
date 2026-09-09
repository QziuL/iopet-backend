package github.qziul.iopet.controller.dto.request;

import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.model.Tutor;

import java.sql.Date;
import java.util.UUID;

public record PetRequestDTO(
    UUID idPublicoTutor,
    String nome,
    String raca,
    String sexo,
    String especie,
    String porte,
    String urlFoto,
    Date dataNascimento,
    String descricao
) {
    public static Pet toEntity(PetRequestDTO dto, Tutor tutor) {
        Pet pet = new Pet();
        pet.setTutor(tutor);
        pet.setNome(dto.nome());
        pet.setRaca(dto.raca());
        pet.setSexo(dto.sexo());
        pet.setEspecie(dto.especie());
        pet.setPorte(dto.porte());
        pet.setUrlFoto(dto.urlFoto());
        pet.setDataNascimento(dto.dataNascimento());
        pet.setDescricao(dto.descricao());
        return pet;
    }
}
