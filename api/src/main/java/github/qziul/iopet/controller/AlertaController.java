package github.qziul.iopet.controller;

import github.qziul.iopet.controller.dto.response.AlertaResponseDTO;
import github.qziul.iopet.domain.model.AlertaGeofencing;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.domain.repository.AlertaGeofencingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaGeofencingRepository alertaRepository;

    public AlertaController(AlertaGeofencingRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @GetMapping
    public ResponseEntity<List<AlertaResponseDTO>> listarAlertas(@AuthenticationPrincipal Tutor tutor) {
        List<AlertaGeofencing> alertas = alertaRepository.findByPetTutorIdOrderByDataDesc(tutor.getId());
        List<AlertaResponseDTO> dtos = alertas.stream().map(a -> new AlertaResponseDTO(
                a.getId(),
                a.getPet() != null ? a.getPet().getUuid() : null,
                a.getPet() != null ? a.getPet().getNome() : "Pet",
                a.getPet() != null ? a.getPet().getUrlFoto() : null,
                determinarTipoAlerta(a.getMensagem()),
                "critical",
                "Alerta de Segurança",
                a.getMensagem(),
                a.getData() != null ? a.getData().toString() : "",
                a.isVisualizado()
        )).toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/visualizar")
    public ResponseEntity<Void> marcarComoVisualizado(@PathVariable Long id) {
        AlertaGeofencing alerta = alertaRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta não encontrado.")
        );
        alerta.setVisualizado(true);
        alertaRepository.save(alerta);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/visualizar-todos")
    public ResponseEntity<Void> marcarTodosComoVisualizados(@AuthenticationPrincipal Tutor tutor) {
        List<AlertaGeofencing> alertas = alertaRepository.findByPetTutorIdOrderByDataDesc(tutor.getId());
        for (AlertaGeofencing a : alertas) {
            a.setVisualizado(true);
        }
        alertaRepository.saveAll(alertas);
        return ResponseEntity.ok().build();
    }

    private String determinarTipoAlerta(String mensagem) {
        if (mensagem == null) return "geofence_exit";
        String msgLower = mensagem.toLowerCase();
        if (msgLower.contains("bateria")) return "battery_low";
        if (msgLower.contains("saiu") || msgLower.contains("fuga")) return "geofence_exit";
        if (msgLower.contains("voltou") || msgLower.contains("entrou")) return "geofence_enter";
        if (msgLower.contains("desconectado")) return "device_disconnected";
        if (msgLower.contains("reconectado")) return "device_reconnected";
        return "geofence_exit";
    }
}
