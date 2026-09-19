package github.qziul.iopet.service.impl;

import github.qziul.iopet.controller.dto.request.AtualizarTutorRequestDTO;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.domain.repository.TutorRepository;
import github.qziul.iopet.service.ITutorService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ImplTutorService implements ITutorService {
    private final TutorRepository tutorRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ImplTutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Override
    public Optional<Tutor> encontrarPorUuid(UUID uuid) {
        return this.tutorRepository.findByUuid(uuid);
    }

    @Override
    public Optional<Tutor> encontrarPorEmail(String email) {
        return this.tutorRepository.findByEmail(email);
    }

    @Override
    public Tutor cadastrar(Tutor tutor) {
        tutor.setSenha(passwordEncoder.encode(tutor.getSenha()));
        return this.tutorRepository.save(tutor);
    }

    @Override
    public Tutor atualizar(UUID uuid, AtualizarTutorRequestDTO dto) {
        Tutor tutor = tutorRepository.findByUuid(uuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado.")
        );
        if (dto.nome() != null && !dto.nome().isBlank()) tutor.setNome(dto.nome());
        if (dto.telefone() != null && !dto.telefone().isBlank()) tutor.setTelefone(dto.telefone());
        if (dto.urlFoto() != null) tutor.setUrlFoto(dto.urlFoto());
        return tutorRepository.save(tutor);
    }

    @Override
    public void alterarSenha(UUID uuid, String senhaAtual, String novaSenha) {
        Tutor tutor = tutorRepository.findByUuid(uuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado.")
        );
        if (!passwordEncoder.matches(senhaAtual, tutor.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta.");
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha deve ter ao menos 6 caracteres.");
        }
        tutor.setSenha(passwordEncoder.encode(novaSenha));
        tutorRepository.save(tutor);
    }

    @Override
    public void excluirConta(UUID uuid) {
        Optional<Tutor> tutor = tutorRepository.findByUuid(uuid);
        tutor.ifPresent(tutorRepository::delete);
    }
}
