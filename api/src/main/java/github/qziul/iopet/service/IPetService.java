package github.qziul.iopet.service;

import github.qziul.iopet.domain.model.Pet;

import java.util.List;
import java.util.Optional;

public interface IPetService {
    List<Pet> listar();
    Optional<Pet> encontrarPorUuid(String uuid);
    Optional<Pet> listarPorNome(String nome);
    Pet cadastrar(Pet pet, Long idTutor); // To-Do PetDTO
    Pet atualizar(Pet pet);
    boolean deletar(Long idPet);
    boolean vincularDispositivoIot(Long petId, String enderecoMac);
}
