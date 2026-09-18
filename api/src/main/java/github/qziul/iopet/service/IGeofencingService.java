package github.qziul.iopet.service;

import github.qziul.iopet.controller.dto.request.TelemetriaRequestDTO;
import github.qziul.iopet.domain.model.DispositivoIot;

public interface IGeofencingService {
    /**
     * Processa a coordenada recebida do dispositivo, persiste o histórico,
     * verifica os limites da zona de segurança e registra o alerta caso haja fuga.
     */
    void processarPosicao(DispositivoIot dispositivo, double latitude, double longitude);

    /**
     * Processa o pacote de telemetria recebido do dispositivo IoT via mensageria (RabbitMQ/MQTT),
     * atualiza os metadados do dispositivo (bateria, última comunicação), persiste o histórico
     * e avalia violação do perímetro de segurança.
     */
    void processarTelemetria(TelemetriaRequestDTO telemetria);

    /**
     * Avalia se um determinado ponto está dentro do polígono de segurança do Pet.
     */
    boolean estaDentroDaZonaDeSeguranca(DispositivoIot dispositivo, double latitude, double longitude);
}
