package github.qziul.iopet.queue;

import github.qziul.iopet.controller.dto.request.TelemetriaRequestDTO;
import github.qziul.iopet.service.IGeofencingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TelemetriaConsumer {

    private static final Logger log = LoggerFactory.getLogger(TelemetriaConsumer.class);

    private final IGeofencingService geofencingService;

    public TelemetriaConsumer(IGeofencingService geofencingService) {
        this.geofencingService = geofencingService;
    }

    /**
     * Intercepta de forma assíncrona mensagens de telemetria publicadas pelo ESP32-C3 via MQTT/RabbitMQ.
     * O processamento ocorre em threads de worker dedicadas do container AMQP, sem bloquear
     * as threads HTTP do Tomcat que atendem as requisições do aplicativo mobile.
     *
     * @param telemetria DTO contendo device_id, latitude, longitude e battery já desserializados do JSON.
     */
    @RabbitListener(
            queues = "${rabbitmq.queue.telemetria:iopet.telemetria.queue}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consumirTelemetria(TelemetriaRequestDTO telemetria) {
        log.info("Mensagem de telemetria recebida da fila RabbitMQ para o dispositivo: {}", telemetria.deviceId());
        try {
            geofencingService.processarTelemetria(telemetria);
            log.debug("Telemetria do dispositivo {} processada com sucesso pelo GeofencingService.", telemetria.deviceId());
        } catch (Exception e) {
            log.error("Erro ao processar pacote de telemetria do dispositivo {}: {}",
                    telemetria.deviceId(), e.getMessage(), e);
        }
    }
}
