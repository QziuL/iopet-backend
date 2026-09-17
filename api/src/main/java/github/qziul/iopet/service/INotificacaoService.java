package github.qziul.iopet.service;

import github.qziul.iopet.domain.model.AlertaGeofencing;

import java.util.Map;

public interface INotificacaoService {
    void enviarNotificacaoPush(AlertaGeofencing alerta);
    void enviarPorTopico(String topico, String titulo, String corpo, Map<String, String> dados);
    void enviarPorToken(String token, String titulo, String corpo, Map<String, String> dados);
}
