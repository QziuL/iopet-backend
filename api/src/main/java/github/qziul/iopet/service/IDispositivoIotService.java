package github.qziul.iopet.service;

import java.util.UUID;

public interface IDispositivoIotService {
    void vincularDispositivoAoPet(UUID idPublicoPet, String enderecoMac);
}
