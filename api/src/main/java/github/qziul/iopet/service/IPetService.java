package github.qziul.iopet.service;

import github.qziul.iopet.controller.dto.request.PetRequestDTO;
import github.qziul.iopet.domain.model.Pet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPetService {
    List<Pet> listar();
    Optional<Pet> encontrarPorUuid(UUID uuid);
    Optional<Pet> listarPorNome(String nome);
    Pet cadastrar(PetRequestDTO petDTO);
    Pet atualizar(Pet pet);
    void deletar(UUID idPet);
    boolean vincularDispositivoIot(Long petId, String enderecoMac);
}
