package github.qziul.iopet.service.impl;

import github.qziul.iopet.controller.dto.response.DispositivoResponseDTO;
import github.qziul.iopet.domain.model.DispositivoIot;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.repository.DispositivoIotRepository;
import github.qziul.iopet.domain.repository.PetRepository;
import github.qziul.iopet.service.IDispositivoIotService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ImplDispositivoIotService implements IDispositivoIotService {
    private final DispositivoIotRepository repository;
    private final PetRepository petRepository;

    public ImplDispositivoIotService(DispositivoIotRepository repository, PetRepository petRepository) {
        this.repository = repository;
        this.petRepository = petRepository;
    }

    @Override
    @Transactional
    public void vincularDispositivoAoPet(UUID idPublicoPet, String enderecoMac) {
        Pet pet = this.petRepository.findByUuid(idPublicoPet).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet nao encontrado.")
        );

        DispositivoIot dispositivo = this.repository.findById(enderecoMac)
                                                    .orElseGet(() -> this.cadastrar(enderecoMac));

        if (dispositivo.getPet() != null && !dispositivo.getPet().getId().equals(pet.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dispositivo já está vinculado a outro pet.");
        }

        // Se o pet já possuía outro dispositivo vinculado, desvincula o anterior
        if (pet.getDispositivoIot() != null && !pet.getDispositivoIot().getEnderecoMac().equalsIgnoreCase(enderecoMac)) {
            DispositivoIot dispAnterior = pet.getDispositivoIot();
            dispAnterior.setPet(null);
            this.repository.save(dispAnterior);
        }

        dispositivo.setPet(pet);
        dispositivo.setEnderecoMac(enderecoMac);
        dispositivo.setAtivo(true);
        pet.setDispositivoIot(dispositivo);
        this.repository.save(dispositivo);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<DispositivoResponseDTO> listarDispositivosDoTutor(Long tutorId) {
        java.util.List<DispositivoIot> dispositivos = this.repository.findByPetTutorId(tutorId);
        return dispositivos.stream()
                .map(DispositivoResponseDTO::new)
                .toList();
    }

    private DispositivoIot cadastrar(String enderecoMac) {
        DispositivoIot novoDispositivo = new DispositivoIot();
        novoDispositivo.setEnderecoMac(enderecoMac);
        return this.repository.save(novoDispositivo);
    }
}
