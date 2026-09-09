package github.qziul.iopet.service.impl;

import github.qziul.iopet.controller.dto.request.PetRequestDTO;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.domain.repository.PetRepository;
import github.qziul.iopet.service.IPetService;
import github.qziul.iopet.service.ITutorService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImplPetService implements IPetService {
    private final PetRepository petRepository;
    private final ITutorService tutorService;

    public ImplPetService(PetRepository petRepository, ITutorService tutorService) {
        this.petRepository = petRepository;
        this.tutorService = tutorService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> listar() {
        return this.petRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> encontrarPorUuid(UUID uuid) {
        return this.petRepository.findByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> listarPorNome(String nome) {
        return this.petRepository.findByNome(nome);
    }

    @Override
    @Transactional
    public Pet cadastrar(PetRequestDTO dto) {
        Tutor tutor = this.tutorService.encontrarPorUuid(dto.idPublicoTutor()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado.")
        );
        Pet novoPet = PetRequestDTO.toEntity(dto, tutor);
        return this.petRepository.save(novoPet);
    }

    @Override
    @Transactional
    public Pet atualizar(Pet pet) {
        return null;
    }

    @Override
    @Transactional
    public void deletar(UUID uuid) {
        this.petRepository.delete(this.petRepository.findByUuid(uuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado.")
        ));
    }

    @Override
    @Transactional
    public boolean vincularDispositivoIot(Long petId, String enderecoMac) {
        return false;
    }
}
