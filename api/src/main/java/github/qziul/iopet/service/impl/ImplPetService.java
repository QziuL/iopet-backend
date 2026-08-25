package github.qziul.iopet.service.impl;

import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.repository.PetRepository;
import github.qziul.iopet.service.IPetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImplPetService implements IPetService {
    private final PetRepository petRepository;

    public ImplPetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public List<Pet> listar() {
        return List.of();
    }

    @Override
    public Optional<Pet> encontrarPorUuid(String uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Pet> listarPorNome(String nome) {
        return Optional.empty();
    }

    @Override
    public Pet cadastrar(Pet pet, Long idTutor) {
        return null;
    }

    @Override
    public Pet atualizar(Pet pet) {
        return null;
    }

    @Override
    public boolean deletar(Long idPet) {
        return false;
    }

    @Override
    public boolean vincularDispositivoIot(Long petId, String enderecoMac) {
        return false;
    }
}
