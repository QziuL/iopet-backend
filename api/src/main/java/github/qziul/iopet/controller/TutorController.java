package github.qziul.iopet.controller;

import github.qziul.iopet.controller.dto.request.TutorRequestDTO;
import github.qziul.iopet.controller.dto.response.TutorResponseDTO;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.service.ITutorService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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

    /*
    @PostMapping
    public ResponseEntity<TutorResponseDTO> cadastrar(@RequestBody TutorRequestDTO requestDto) {
        if(this.tutorService.encontrarPorEmail(requestDto.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Tutor novoTutor = new Tutor();
        novoTutor.setNome(requestDto.nome());
        novoTutor.setEmail(requestDto.email());
        novoTutor.setSenha(requestDto.senha());
        novoTutor.setUrlFoto(requestDto.urlFoto());

        Tutor tutorSalvo = tutorService.cadastrar(novoTutor);
        TutorResponseDTO tutorResponseDTO = new TutorResponseDTO(tutorSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(tutorResponseDTO);
    }
    */

    @GetMapping("/{uuid}")
    public ResponseEntity<TutorResponseDTO> buscarPorUuid(@PathVariable UUID uuid) {
        Optional<Tutor> tutorEncontrado = tutorService.encontrarPorUuid(uuid);

        if (tutorEncontrado.isPresent()) {
            TutorResponseDTO responseDTO = new TutorResponseDTO(tutorEncontrado.get());
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> excluirConta(@PathVariable UUID uuid) {
        tutorService.excluirConta(uuid);
        return ResponseEntity.noContent().build();
    }
}
