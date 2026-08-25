package github.qziul.iopet.service.impl;

import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.domain.repository.TutorRepository;
import github.qziul.iopet.service.ITutorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImplTutorService implements ITutorService {
    private final TutorRepository tutorRepository;

    public ImplTutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Override
    public List<Tutor> listar() {
        return List.of();
    }

    @Override
    public Optional<Tutor> encontrarPorUuid(String uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Tutor> encontrarPorEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Tutor cadastrar(Tutor tutor) {
        return null;
    }

    @Override
    public Tutor atualizar(Tutor tutor) {
        return null;
    }

    @Override
    public boolean deletar(Long id) {
        return false;
    }
}
