package github.qziul.iopet.queue;

import github.qziul.iopet.controller.dto.request.TelemetriaRequestDTO;
import github.qziul.iopet.service.IGeofencingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetriaConsumerTest {

    @Mock
    private IGeofencingService geofencingService;

    @InjectMocks
    private TelemetriaConsumer telemetriaConsumer;

    private TelemetriaRequestDTO telemetriaValida;

    @BeforeEach
    void setUp() {
        telemetriaValida = new TelemetriaRequestDTO(
                "74:EC:B2:2A:14:FF",
                -25.542123,
                -46.534567,
                87
        );
    }

    @Test
    @DisplayName("Deve consumir mensagem de telemetria e repassar ao GeofencingService")
    void deveConsumirTelemetriaComSucesso() {
        assertDoesNotThrow(() -> telemetriaConsumer.consumirTelemetria(telemetriaValida));
        verify(geofencingService, times(1)).processarTelemetria(telemetriaValida);
    }

    @Test
    @DisplayName("Não deve propagar exceção caso o GeofencingService falhe no processamento")
    void naoDevePropagarExcecaoQuandoServicoFalhar() {
        doThrow(new RuntimeException("Erro de banco")).when(geofencingService).processarTelemetria(any());

        assertDoesNotThrow(() -> telemetriaConsumer.consumirTelemetria(telemetriaValida));
        verify(geofencingService, times(1)).processarTelemetria(telemetriaValida);
    }
}
