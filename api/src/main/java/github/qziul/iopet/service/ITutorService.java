package github.qziul.iopet.service;

import github.qziul.iopet.domain.model.Tutor;

import java.util.List;
import java.util.Optional;

public interface ITutorService {
    List<Tutor> listar();
    Optional<Tutor> encontrarPorUuid(String uuid);
    Optional<Tutor> encontrarPorEmail(String email);
    Tutor cadastrar(Tutor tutor); // To-Do TutorDTO
    Tutor atualizar(Tutor tutor);
    boolean deletar(Long id);
}
