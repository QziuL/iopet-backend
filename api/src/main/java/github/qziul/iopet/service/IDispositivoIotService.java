package github.qziul.iopet.service;

import github.qziul.iopet.controller.dto.response.DispositivoResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IDispositivoIotService {
    void vincularDispositivoAoPet(UUID idPublicoPet, String enderecoMac);
    List<DispositivoResponseDTO> listarDispositivosDoTutor(Long tutorId);
}
