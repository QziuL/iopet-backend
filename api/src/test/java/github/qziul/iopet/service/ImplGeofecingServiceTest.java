package github.qziul.iopet.service;

import github.qziul.iopet.controller.dto.request.TelemetriaRequestDTO;
import github.qziul.iopet.domain.model.AlertaGeofencing;
import github.qziul.iopet.domain.model.DispositivoIot;
import github.qziul.iopet.domain.model.HistoricoLocalizacao;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.repository.AlertaGeofencingRepository;
import github.qziul.iopet.domain.repository.DispositivoIotRepository;
import github.qziul.iopet.domain.repository.HistoricoLocalizacaoRepository;
import github.qziul.iopet.service.impl.ImplGeofecingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImplGeofecingServiceTest {

    @Mock
    private HistoricoLocalizacaoRepository historicoRepository;

    @Mock
    private AlertaGeofencingRepository alertaRepository;

    @Mock
    private DispositivoIotRepository dispositivoRepository;

    @Mock
    private INotificacaoService notificacaoService;

    @InjectMocks
    private ImplGeofecingService geofencingService;

    private GeometryFactory geometryFactory;
    private Polygon zonaSegura;

    @BeforeEach
    void setUp() {
        geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        // Polígono quadrado cobrindo longitude 0 a 10 e latitude 0 a 10
        Coordinate[] coordenadas = new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(10, 0),
                new Coordinate(10, 10),
                new Coordinate(0, 10),
                new Coordinate(0, 0)
        };
        zonaSegura = geometryFactory.createPolygon(coordenadas);
    }

    @Test
    @DisplayName("Deve processar telemetria de dispositivo existente, atualizar bateria e registrar histórico")
    void deveProcessarTelemetriaDispositivoExistente() {
        String mac = "74:EC:B2:2A:14:FF";
        DispositivoIot dispositivo = new DispositivoIot();
        dispositivo.setEnderecoMac(mac);
        dispositivo.setBateriaNivel(50);

        Pet pet = new Pet();
        pet.setId(1L);
        pet.setNome("Rex");
        pet.setZonaSeguranca(zonaSegura);
        dispositivo.setPet(pet);

        when(dispositivoRepository.findById(mac)).thenReturn(Optional.of(dispositivo));
        when(dispositivoRepository.save(any(DispositivoIot.class))).thenReturn(dispositivo);

        // Ponto dentro da zona segura (lat=5, lon=5)
        TelemetriaRequestDTO telemetria = new TelemetriaRequestDTO(mac, 5.0, 5.0, 87);

        geofencingService.processarTelemetria(telemetria);

        assertEquals(87, dispositivo.getBateriaNivel());
        assertNotNull(dispositivo.getUltimaComunicacao());

        verify(historicoRepository, times(1)).save(any(HistoricoLocalizacao.class));
        verify(alertaRepository, never()).save(any(AlertaGeofencing.class));
        verify(notificacaoService, never()).enviarNotificacaoPush(any());
    }

    @Test
    @DisplayName("Deve disparar alerta e enviar notificação push quando pet estiver fora da zona de segurança")
    void deveDispararAlertaQuandoHouverFuga() {
        String mac = "74:EC:B2:2A:14:FF";
        DispositivoIot dispositivo = new DispositivoIot();
        dispositivo.setEnderecoMac(mac);

        Pet pet = new Pet();
        pet.setId(2L);
        pet.setNome("Thor");
        pet.setZonaSeguranca(zonaSegura);
        dispositivo.setPet(pet);

        when(dispositivoRepository.findById(mac)).thenReturn(Optional.of(dispositivo));
        when(dispositivoRepository.save(any(DispositivoIot.class))).thenReturn(dispositivo);
        when(alertaRepository.save(any(AlertaGeofencing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ponto fora da zona segura (lat=20, lon=20)
        TelemetriaRequestDTO telemetria = new TelemetriaRequestDTO(mac, 20.0, 20.0, 80);

        geofencingService.processarTelemetria(telemetria);

        verify(historicoRepository, times(1)).save(any(HistoricoLocalizacao.class));

        ArgumentCaptor<AlertaGeofencing> alertaCaptor = ArgumentCaptor.forClass(AlertaGeofencing.class);
        verify(alertaRepository, times(1)).save(alertaCaptor.capture());
        AlertaGeofencing alertaSalvo = alertaCaptor.getValue();

        assertEquals(pet, alertaSalvo.getPet());
        assertTrue(alertaSalvo.getMensagem().contains("Thor"));
        assertFalse(alertaSalvo.isVisualizado());

        verify(notificacaoService, times(1)).enviarNotificacaoPush(alertaSalvo);
    }

    @Test
    @DisplayName("Deve auto-cadastrar dispositivo caso ele ainda não exista na base")
    void deveCadastrarDispositivoAutomaticamente() {
        String mac = "AA:BB:CC:DD:EE:FF";
        when(dispositivoRepository.findById(mac)).thenReturn(Optional.empty());
        when(dispositivoRepository.save(any(DispositivoIot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TelemetriaRequestDTO telemetria = new TelemetriaRequestDTO(mac, 1.0, 1.0, 95);

        geofencingService.processarTelemetria(telemetria);

        verify(dispositivoRepository, atLeast(1)).save(any(DispositivoIot.class));
        verify(historicoRepository, times(1)).save(any(HistoricoLocalizacao.class));
    }

    @Test
    @DisplayName("Deve ignorar pacote de telemetria nulo ou sem device_id")
    void deveIgnorarTelemetriaInvalida() {
        geofencingService.processarTelemetria(null);
        geofencingService.processarTelemetria(new TelemetriaRequestDTO(null, 10.0, 10.0, 100));

        verify(dispositivoRepository, never()).findById(any());
        verify(historicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve disparar alerta de bateria quando nível for 20% ou menos vindo de um nível superior")
    void deveDispararAlertaBateriaQuandoNivelForMenorOuIgualA20() {
        String mac = "74:EC:B2:2A:14:FF";
        DispositivoIot dispositivo = new DispositivoIot();
        dispositivo.setEnderecoMac(mac);
        dispositivo.setBateriaNivel(50); // Bateria anterior > 20

        when(dispositivoRepository.findById(mac)).thenReturn(Optional.of(dispositivo));
        when(dispositivoRepository.save(any(DispositivoIot.class))).thenReturn(dispositivo);

        TelemetriaRequestDTO telemetria = new TelemetriaRequestDTO(mac, 5.0, 5.0, 18);

        geofencingService.processarTelemetria(telemetria);

        verify(notificacaoService, times(1)).enviarAlertaBateriaBaixa(dispositivo, 18);
    }

    @Test
    @DisplayName("Não deve disparar alerta de bateria repetido se a bateria já estava em 20% ou menos")
    void naoDeveDispararAlertaBateriaRepetidoSeJaEstavaBaixa() {
        String mac = "74:EC:B2:2A:14:FF";
        DispositivoIot dispositivo = new DispositivoIot();
        dispositivo.setEnderecoMac(mac);
        dispositivo.setBateriaNivel(18); // Bateria anterior já era <= 20

        when(dispositivoRepository.findById(mac)).thenReturn(Optional.of(dispositivo));
        when(dispositivoRepository.save(any(DispositivoIot.class))).thenReturn(dispositivo);

        TelemetriaRequestDTO telemetria = new TelemetriaRequestDTO(mac, 5.0, 5.0, 17);

        geofencingService.processarTelemetria(telemetria);

        verify(notificacaoService, never()).enviarAlertaBateriaBaixa(any(), anyInt());
    }
}

