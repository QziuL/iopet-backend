package github.qziul.iopet.service;

import github.qziul.iopet.controller.dto.request.PetRequestDTO;
import github.qziul.iopet.domain.model.Pet;

import java.util.*;

public interface IPetService {
    List<Pet> listarPetsDoTutor(Long tutorId);
    Optional<Pet> encontrarPorUuid(UUID idPublico);
    Optional<Pet> encontrarPorNome(String nome);
    Pet cadastrar(PetRequestDTO petDTO);
    Pet atualizar(Pet pet);
    void deletar(UUID idPublico);
    void vincularDispositivoIot(UUID idPublico, String enderecoMac);
}
