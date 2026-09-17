package github.qziul.iopet.service.impl;

import github.qziul.iopet.domain.model.AlertaGeofencing;
import github.qziul.iopet.domain.model.DispositivoIot;
import github.qziul.iopet.domain.model.HistoricoLocalizacao;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.repository.AlertaGeofencingRepository;
import github.qziul.iopet.domain.repository.HistoricoLocalizacaoRepository;
import github.qziul.iopet.service.IGeofencingService;
import github.qziul.iopet.service.INotificacaoService;
import github.qziul.iopet.utils.SpatialUtils;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ImplGeofecingService implements IGeofencingService {
    private final HistoricoLocalizacaoRepository historicoRepository;
    private final AlertaGeofencingRepository alertaRepository;
    private final INotificacaoService notificacaoService;

    public ImplGeofecingService(AlertaGeofencingRepository alertaGeofencingRepository,
                                HistoricoLocalizacaoRepository historicoLocalizacaoRepository,
                                INotificacaoService notificacaoService) {
        this.alertaRepository = alertaGeofencingRepository;
        this.historicoRepository = historicoLocalizacaoRepository;
        this.notificacaoService = notificacaoService;
    }

    @Override
    public void processarPosicao(DispositivoIot dispositivo, double latitude, double longitude) {
        Point posicao = SpatialUtils.criarPonto(latitude, longitude);

        HistoricoLocalizacao novoHistorico = new  HistoricoLocalizacao();
        novoHistorico.setPosicao(posicao);
        novoHistorico.setDispositivoIot(dispositivo);
        historicoRepository.save(novoHistorico);

        Pet pet = dispositivo.getPet();

        if(pet != null && pet.getZonaSeguranca() != null) {
            boolean estaDentro = posicao.coveredBy(pet.getZonaSeguranca());

            if (!estaDentro) {
                notificacaoService.enviarNotificacaoPush(registrarAlertaFuga(pet, novoHistorico));
            }
        }
    }

    @Override
    public boolean estaDentroDaZonaDeSeguranca(DispositivoIot dispositivo, double latitude, double longitude) {
        Pet pet = dispositivo.getPet();
        if (Objects.isNull(pet)|| Objects.isNull(pet.getZonaSeguranca()))
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
