package github.qziul.iopet.controller.dto.response;

import github.qziul.iopet.domain.model.Pet;
import lombok.Data;

import java.sql.Date;
import java.util.UUID;

@Data
public class PetResponseDTO
{
    private UUID idPublico, idPublicoTutor;
    private Date dataNascimento;
    private String nome,raca,sexo,especie,porte,urlFoto,descricao;

    public PetResponseDTO(Pet pet) {
        this.idPublico = pet.getUuid();
        this.idPublicoTutor = pet.getTutor().getUuid();
        this.nome = pet.getNome();
        this.raca = pet.getRaca();
        this.sexo = pet.getSexo();
        this.especie = pet.getEspecie();
        this.porte = pet.getPorte();
        this.urlFoto = pet.getUrlFoto();
        this.descricao = pet.getDescricao();
    }
}
