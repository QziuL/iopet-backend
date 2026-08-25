package github.qziul.iopet.service.impl;

import github.qziul.iopet.service.INotificacaoService;
import org.springframework.stereotype.Service;

@Service
public class ImplNotificacaoService implements INotificacaoService {
    @Override
    public void enviarNotificacaoPush(Long tutorId, String titulo, String mensagem) {
        // Implementação do SDK do Firebase para disparar a notificação
    }
}
