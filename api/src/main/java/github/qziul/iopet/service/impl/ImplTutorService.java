package github.qziul.iopet.service.impl;

import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.domain.repository.TutorRepository;
import github.qziul.iopet.service.ITutorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImplTutorService implements ITutorService {
    private final TutorRepository tutorRepository;

    public ImplTutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

//    @Override
//    public List<Tutor> listar() {
//        return List.of();
//    }

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
        return this.tutorRepository.save(tutor);
    }

    @Override
    public Tutor atualizar(Tutor tutor) {
        return null;
    }

    @Override
    public void excluirConta(UUID uuid) {
        Optional<Tutor> tutor = tutorRepository.findByUuid(uuid);
        tutor.ifPresent(tutorRepository::delete);
    }
}
