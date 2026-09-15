package github.qziul.iopet.controller;

import github.qziul.iopet.controller.dto.request.TutorRequestDTO;
import github.qziul.iopet.controller.dto.response.TutorResponseDTO;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.service.ITutorService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {
    private final ITutorService tutorService;

    public TutorController(ITutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<TutorResponseDTO> buscarPorEmail(@PathVariable String email) {
        return this.tutorService.encontrarPorEmail(email)
                .map(TutorResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<TutorResponseDTO> buscarPorUuid(@PathVariable UUID uuid) {
        return this.tutorService.encontrarPorUuid(uuid)
                .map(TutorResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> excluirConta(@PathVariable UUID uuid) {
        tutorService.excluirConta(uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
