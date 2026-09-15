package github.qziul.iopet.service.impl;

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

        DispositivoIot dispositivo = this.repository.findById(enderecoMac).orElseGet(
                () -> {
                    DispositivoIot novoDispositivo = new DispositivoIot();
                    novoDispositivo.setEnderecoMac(enderecoMac);
                    return this.repository.save(novoDispositivo);
                }
        );

        if(dispositivo.getPet() != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dispositivo já está vinculado a outro pet.");

        dispositivo.setPet(pet);
        dispositivo.setEnderecoMac(enderecoMac);
        dispositivo.setAtivo(true);
        this.repository.save(dispositivo);
    }
}
