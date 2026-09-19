package github.qziul.iopet.service;

import github.qziul.iopet.controller.dto.request.AtualizarTutorRequestDTO;
import github.qziul.iopet.domain.model.Tutor;

import java.util.Optional;
import java.util.UUID;

public interface ITutorService {
    Optional<Tutor> encontrarPorUuid(UUID uuid);
    Optional<Tutor> encontrarPorEmail(String email);
    Tutor cadastrar(Tutor tutor);
    Tutor atualizar(UUID uuid, AtualizarTutorRequestDTO dto);
    void alterarSenha(UUID uuid, String senhaAtual, String novaSenha);
    void excluirConta(UUID uuid);
}
