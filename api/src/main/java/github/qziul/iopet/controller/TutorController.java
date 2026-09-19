package github.qziul.iopet.controller;

import github.qziul.iopet.controller.dto.request.AlterarSenhaRequestDTO;
import github.qziul.iopet.controller.dto.request.AtualizarTutorRequestDTO;
import github.qziul.iopet.controller.dto.response.TutorResponseDTO;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.service.ITutorService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {
    private final ITutorService tutorService;

    public TutorController(ITutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping("/me")
    public ResponseEntity<TutorResponseDTO> getMe(@AuthenticationPrincipal Tutor tutor) {
        return ResponseEntity.ok(new TutorResponseDTO(tutor));
    }

    @PutMapping("/me")
    public ResponseEntity<TutorResponseDTO> atualizarPerfil(@AuthenticationPrincipal Tutor tutor,
                                                            @RequestBody AtualizarTutorRequestDTO dto) {
        Tutor atualizado = this.tutorService.atualizar(tutor.getUuid(), dto);
        return ResponseEntity.ok(new TutorResponseDTO(atualizado));
    }

    @PutMapping("/senha")
    public ResponseEntity<Void> alterarSenha(@AuthenticationPrincipal Tutor tutor,
                                             @RequestBody AlterarSenhaRequestDTO dto) {
        this.tutorService.alterarSenha(tutor.getUuid(), dto.senhaAtual(), dto.novaSenha());
        return ResponseEntity.ok().build();
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
