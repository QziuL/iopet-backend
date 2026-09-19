package github.qziul.iopet.controller;

import github.qziul.iopet.controller.dto.response.DispositivoResponseDTO;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.service.IDispositivoIotService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {

    private final IDispositivoIotService dispositivoService;

    public DispositivoController(IDispositivoIotService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @GetMapping
    public ResponseEntity<List<DispositivoResponseDTO>> listar(@AuthenticationPrincipal Tutor tutor) {
        List<DispositivoResponseDTO> dispositivos = this.dispositivoService.listarDispositivosDoTutor(tutor.getId());
        return ResponseEntity.ok(dispositivos);
    }
}
