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
    private String enderecoMac;
    private Integer bateriaNivel;
    private Boolean dispositivoAtivo;
    private String ultimaComunicacao;
    private Boolean temZonaSeguranca;

    public PetResponseDTO(Pet pet) {
        this.idPublico = pet.getUuid();
        if (pet.getTutor() != null) {
            this.idPublicoTutor = pet.getTutor().getUuid();
        }
        this.nome = pet.getNome();
        this.raca = pet.getRaca();
        this.sexo = pet.getSexo();
        this.especie = pet.getEspecie();
        this.porte = pet.getPorte();
        this.urlFoto = pet.getUrlFoto();
        this.descricao = pet.getDescricao();
        this.dataNascimento = pet.getDataNascimento();
        this.temZonaSeguranca = pet.getZonaSeguranca() != null;

        if (pet.getDispositivoIot() != null) {
            this.enderecoMac = pet.getDispositivoIot().getEnderecoMac();
            this.bateriaNivel = pet.getDispositivoIot().getBateriaNivel();
            this.dispositivoAtivo = pet.getDispositivoIot().isAtivo();
            if (pet.getDispositivoIot().getUltimaComunicacao() != null) {
                this.ultimaComunicacao = pet.getDispositivoIot().getUltimaComunicacao().toString();
            }
        }
    }
}
