package github.qziul.iopet.service.impl;

import github.qziul.iopet.controller.dto.request.TelemetriaRequestDTO;
import github.qziul.iopet.domain.model.AlertaGeofencing;
import github.qziul.iopet.domain.model.DispositivoIot;
import github.qziul.iopet.domain.model.HistoricoLocalizacao;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.repository.AlertaGeofencingRepository;
import github.qziul.iopet.domain.repository.DispositivoIotRepository;
import github.qziul.iopet.domain.repository.HistoricoLocalizacaoRepository;
import github.qziul.iopet.service.IGeofencingService;
import github.qziul.iopet.service.INotificacaoService;
import github.qziul.iopet.utils.SpatialUtils;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ImplGeofecingService implements IGeofencingService {

    private static final Logger log = LoggerFactory.getLogger(ImplGeofecingService.class);

    private final HistoricoLocalizacaoRepository historicoRepository;
    private final AlertaGeofencingRepository alertaRepository;
    private final DispositivoIotRepository dispositivoRepository;
    private final INotificacaoService notificacaoService;

    public ImplGeofecingService(AlertaGeofencingRepository alertaGeofencingRepository,
                                HistoricoLocalizacaoRepository historicoLocalizacaoRepository,
                                DispositivoIotRepository dispositivoRepository,
                                INotificacaoService notificacaoService) {
        this.alertaRepository = alertaGeofencingRepository;
        this.historicoRepository = historicoLocalizacaoRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.notificacaoService = notificacaoService;
    }

    @Override
    @Transactional
    public void processarTelemetria(TelemetriaRequestDTO telemetria) {
        if (telemetria == null || telemetria.deviceId() == null) {
            log.warn("Telemetria descartada: payload nulo ou sem device_id.");
            return;
        }

        String mac = telemetria.deviceId();

        boolean isNovoDispositivo = false;
        var optDispositivo = dispositivoRepository.findById(mac);
        DispositivoIot dispositivo;
        if (optDispositivo.isPresent()) {
            dispositivo = optDispositivo.get();
        } else {
            log.info("Dispositivo IoT '{}' não cadastrado previamente. Registrando automaticamente.", mac);
            isNovoDispositivo = true;
            DispositivoIot novo = new DispositivoIot();
            novo.setEnderecoMac(mac);
            novo.setAtivo(true);
            novo.setUltimaComunicacao(LocalDateTime.now());
            novo.setBateriaNivel(telemetria.battery() != null ? telemetria.battery() : 100);
            dispositivo = dispositivoRepository.save(novo);
        }

        int nivelAnterior = dispositivo.getBateriaNivel();
        dispositivo.setUltimaComunicacao(LocalDateTime.now());

        if (telemetria.battery() != null) {
            int novoNivel = telemetria.battery();
            dispositivo.setBateriaNivel(novoNivel);

            // Regra de negócio: alerta de bateria fraca em 20% ou menos
            // Dispara na transição de >20% para <=20% (evitando spam a cada ping) ou no primeiro cadastro
            if (novoNivel <= 20 && (isNovoDispositivo || nivelAnterior > 20)) {
                log.warn("Bateria fraca detectada para o dispositivo {} (nível: {}%). Disparando notificação.", mac, novoNivel);
                notificacaoService.enviarAlertaBateriaBaixa(dispositivo, novoNivel);
            }
        }
        dispositivoRepository.save(dispositivo);

        processarPosicao(dispositivo, telemetria.latitude(), telemetria.longitude());
    }

    @Override
    @Transactional
    public void processarPosicao(DispositivoIot dispositivo, double latitude, double longitude) {
        Point posicao = SpatialUtils.criarPonto(latitude, longitude);

        HistoricoLocalizacao novoHistorico = new HistoricoLocalizacao();
        novoHistorico.setPosicao(posicao);
        novoHistorico.setLatitude((float) latitude);
        novoHistorico.setLongitude((float) longitude);
        novoHistorico.setDispositivoIot(dispositivo);
        historicoRepository.save(novoHistorico);

        log.debug("Posição registrada para dispositivo {}: lat={}, lon={}",
                dispositivo.getEnderecoMac(), latitude, longitude);

        Pet pet = dispositivo.getPet();

        if (pet != null && pet.getZonaSeguranca() != null) {
            boolean estaDentro = posicao.coveredBy(pet.getZonaSeguranca());

            if (!estaDentro) {
                log.warn("Violação de perímetro detectada para o Pet '{}' (ID: {}). Registrando alerta e notificando tutor.",
                        pet.getNome(), pet.getId());
                AlertaGeofencing alerta = registrarAlertaFuga(pet, novoHistorico);
                notificacaoService.enviarNotificacaoPush(alerta);
            }
        }
    }

    @Override
    public boolean estaDentroDaZonaDeSeguranca(DispositivoIot dispositivo, double latitude, double longitude) {
        Pet pet = dispositivo.getPet();
        if (Objects.isNull(pet) || Objects.isNull(pet.getZonaSeguranca()))
            return true;
        Point pontoAtual = SpatialUtils.criarPonto(latitude, longitude);
        return pontoAtual.coveredBy(pet.getZonaSeguranca());
    }

    private AlertaGeofencing registrarAlertaFuga(Pet pet, HistoricoLocalizacao novoHistorico) {
        AlertaGeofencing alerta = new AlertaGeofencing();
        alerta.setPet(pet);
        alerta.setHistoricoLocalizacao(novoHistorico);
        alerta.setData(LocalDateTime.now());
        alerta.setMensagem(String.format("Possível fuga detectada! %s saiu da área segura.", pet.getNome()));
        alerta.setVisualizado(false);

        return this.alertaRepository.save(alerta);
    }
}
