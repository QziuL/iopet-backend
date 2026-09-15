package github.qziul.iopet.service.impl;

import github.qziul.iopet.controller.dto.request.PetRequestDTO;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.domain.repository.PetRepository;
 import github.qziul.iopet.service.IDispositivoIotService;
import github.qziul.iopet.service.IPetService;
import github.qziul.iopet.service.ITutorService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ImplPetService implements IPetService {
    private final PetRepository petRepository;
    private final ITutorService tutorService;
    private final IDispositivoIotService dispositivoService;

    public ImplPetService(PetRepository petRepository,
                          ITutorService tutorService,
                          IDispositivoIotService dispositivoService
    )
    {
        this.petRepository = petRepository;
        this.tutorService = tutorService;
        this.dispositivoService = dispositivoService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> listarPetsDoTutor(Long tutorId) {
        return this.petRepository.findByTutorId(tutorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> encontrarPorUuid(UUID uuid) {
        return this.petRepository.findByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> encontrarPorNome(String nome) {
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
    public void vincularDispositivoIot(UUID idPublicoPet, String enderecoMac) {
        this.dispositivoService.vincularDispositivoAoPet(idPublicoPet, enderecoMac);
    }
}
