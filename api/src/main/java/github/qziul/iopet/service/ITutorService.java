package github.qziul.iopet.service;

import github.qziul.iopet.domain.model.Tutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITutorService {
    //List<Tutor> listar();
    Optional<Tutor> encontrarPorUuid(UUID uuid);
    Optional<Tutor> encontrarPorEmail(String email);
    Tutor cadastrar(Tutor tutor); // To-Do TutorDTO
    Tutor atualizar(Tutor tutor);
    void excluirConta(UUID uuid);
}
