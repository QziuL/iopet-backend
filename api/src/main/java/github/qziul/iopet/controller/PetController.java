package github.qziul.iopet.controller;

import github.qziul.iopet.controller.dto.request.PetRequestDTO;
import github.qziul.iopet.controller.dto.request.VincularDispositivoRequestDTO;
import github.qziul.iopet.controller.dto.response.PetResponseDTO;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.service.IPetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pets")
public class PetController {
    private final IPetService petService;

    public  PetController(IPetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<PetResponseDTO> cadastrar(@RequestBody PetRequestDTO petRequestDTO) {
        Pet petSalvo = this.petService.cadastrar(petRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PetResponseDTO(petSalvo));
    }

    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> listar(@AuthenticationPrincipal Tutor tutor) {
        List<Pet> pets = this.petService.listarPetsDoTutor(tutor.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(pets.stream()
                        .map(PetResponseDTO::new)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<PetResponseDTO> buscarPorUuid(@PathVariable UUID uuid) {
        return this.petService.encontrarPorUuid(uuid)
                .map(PetResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{nome}")
    public ResponseEntity<PetResponseDTO> buscarPorNome(@PathVariable String nome) {
        return this.petService.encontrarPorNome(nome)
                .map(PetResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<PetResponseDTO> atualizar(@RequestBody PetRequestDTO petRequestDTO,
                                                    @AuthenticationPrincipal Tutor tutor) {
        Pet petAtualizado = this.petService.atualizar(PetRequestDTO.toEntity(petRequestDTO, tutor));
        return ResponseEntity.status(HttpStatus.OK).body(new PetResponseDTO(petAtualizado));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deletar(@RequestParam("uuid") UUID uuid) {
        this.petService.deletar(uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/vincular-dispositivo")
    public ResponseEntity<Void> vincularDispositivoAoPet(@RequestBody VincularDispositivoRequestDTO dto) {
        this.petService.vincularDispositivoIot(dto.idPublicoPet(), dto.enderecoMac());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
