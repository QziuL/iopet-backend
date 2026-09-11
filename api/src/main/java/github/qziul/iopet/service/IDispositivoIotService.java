package github.qziul.iopet.service;

import github.qziul.iopet.controller.dto.request.DispositivoIotRequestDTO;
import github.qziul.iopet.domain.model.DispositivoIot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDispositivoIotService {
    //DispositivoIot cadastrar(DispositivoIotRequestDTO requestDTO);
    DispositivoIot buscarPorEnderecoMac(String enderecoMac);
    boolean vincularDispositivoAoPet(UUID idPublicoPet, String enderecoMac);
    List<DispositivoIot> consultarDispositivos();
}
