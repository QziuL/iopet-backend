package github.qziul.iopet.service;

public interface INotificacaoService {
    void enviarNotificacaoPush(Long tutorId, String titulo, String mensagem);
}
