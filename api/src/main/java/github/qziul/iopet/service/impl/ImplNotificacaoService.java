package github.qziul.iopet.service.impl;

import com.google.firebase.messaging.*;
import github.qziul.iopet.domain.model.AlertaGeofencing;
import github.qziul.iopet.domain.model.DispositivoIot;
import github.qziul.iopet.domain.model.HistoricoLocalizacao;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.service.INotificacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ImplNotificacaoService implements INotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(ImplNotificacaoService.class);
    private static final String CHANNEL_ID_ALERTA = "alerta_geofencing";

    private final FirebaseMessaging firebaseMessaging;

    public ImplNotificacaoService(Optional<FirebaseMessaging> firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging.orElse(null);
    }

    @Override
    public void enviarNotificacaoPush(AlertaGeofencing alerta) {
        if (alerta == null) {
            log.warn("Alerta recebido para notificação é nulo. Operação abortada.");
            return;
        }

        Pet pet = alerta.getPet();
        if (pet == null) {
            log.warn("Alerta id={} não possui pet associado. Notificação push cancelada.", alerta.getId());
            return;
        }

        Tutor tutor = pet.getTutor();
        if (tutor == null || tutor.getUuid() == null) {
            log.warn("Pet '{}' (id={}) não possui tutor associado ou UUID do tutor é nulo. Notificação push cancelada.",
                    pet.getNome(), pet.getId());
            return;
        }

        String topico = "tutor-" + tutor.getUuid();
        String nomePet = pet.getNome() != null ? pet.getNome() : "Seu pet";
        String titulo = "Alerta de Fuga: " + nomePet;
        String corpo = (alerta.getMensagem() != null && !alerta.getMensagem().isBlank())
                ? alerta.getMensagem()
                : String.format("Atenção! %s saiu da zona de segurança.", nomePet);

        Map<String, String> dados = new HashMap<>();
        if (alerta.getId() != null) {
            dados.put("alertaId", String.valueOf(alerta.getId()));
        }
        if (pet.getId() != null) {
            dados.put("petId", String.valueOf(pet.getId()));
        }
        if (pet.getUuid() != null) {
            dados.put("petUuid", pet.getUuid().toString());
        }
        dados.put("petNome", nomePet);
        dados.put("tipo", "ALERTA_GEOFENCING");

        if (alerta.getData() != null) {
            dados.put("dataDisparo", alerta.getData().toString());
        }

        HistoricoLocalizacao historico = alerta.getHistoricoLocalizacao();
        if (historico != null) {
            if (historico.getPosicao() != null) {
                dados.put("latitude", String.valueOf(historico.getPosicao().getY()));
                dados.put("longitude", String.valueOf(historico.getPosicao().getX()));
            } else {
                dados.put("latitude", String.valueOf(historico.getLatitude()));
                dados.put("longitude", String.valueOf(historico.getLongitude()));
            }
        }

        enviarPorTopico(topico, titulo, corpo, pet.getUrlFoto(), dados);
    }

    @Override
    public void enviarAlertaBateriaBaixa(DispositivoIot dispositivo, int nivelBateria) {
        if (dispositivo == null) {
            log.warn("Dispositivo nulo ao tentar enviar alerta de bateria baixa.");
            return;
        }

        Pet pet = dispositivo.getPet();
        if (pet == null) {
            log.warn("Dispositivo '{}' não possui pet vinculado. Alerta de bateria baixa cancelado.",
                    dispositivo.getEnderecoMac());
            return;
        }

        Tutor tutor = pet.getTutor();
        if (tutor == null || tutor.getUuid() == null) {
            log.warn("Pet '{}' (id={}) não possui tutor associado ou UUID do tutor é nulo. Alerta de bateria baixa cancelado.",
                    pet.getNome(), pet.getId());
            return;
        }

        String topico = "tutor-" + tutor.getUuid();
        String nomePet = pet.getNome() != null ? pet.getNome() : "Seu pet";
        String titulo = "⚠️ Bateria Fraca: " + nomePet;
        String corpo = String.format("A bateria da coleira de %s está em %d%%. Conecte-a ao carregador para manter o monitoramento.",
                nomePet, nivelBateria);

        Map<String, String> dados = new HashMap<>();
        dados.put("tipo", "ALERTA_BATERIA_BAIXA");
        dados.put("deviceId", dispositivo.getEnderecoMac());
        dados.put("bateriaNivel", String.valueOf(nivelBateria));
        if (pet.getId() != null) {
            dados.put("petId", String.valueOf(pet.getId()));
        }
        if (pet.getUuid() != null) {
            dados.put("petUuid", pet.getUuid().toString());
        }
        dados.put("petNome", nomePet);
        dados.put("dataDisparo", java.time.LocalDateTime.now().toString());

        enviarPorTopico(topico, titulo, corpo, pet.getUrlFoto(), dados);
    }

    @Override
    public void enviarPorTopico(String topico, String titulo, String corpo, Map<String, String> dados) {
        enviarPorTopico(topico, titulo, corpo, null, dados);
    }

    public void enviarPorTopico(String topico, String titulo, String corpo, String imagemUrl, Map<String, String> dados) {
        if (topico == null || topico.isBlank()) {
            log.warn("Tópico inválido para envio de notificação push.");
            return;
        }

        Notification notification = criarNotification(titulo, corpo, imagemUrl);
        AndroidConfig androidConfig = criarAndroidConfig();
        ApnsConfig apnsConfig = criarApnsConfig();

        Message.Builder messageBuilder = Message.builder()
                .setTopic(topico)
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig);

        adicionarDados(messageBuilder, dados);

        enviarMensagem(messageBuilder.build(), "tópico: " + topico);
    }

    @Override
    public void enviarPorToken(String token, String titulo, String corpo, Map<String, String> dados) {
        enviarPorToken(token, titulo, corpo, null, dados);
    }

    public void enviarPorToken(String token, String titulo, String corpo, String imagemUrl, Map<String, String> dados) {
        if (token == null || token.isBlank()) {
            log.warn("Token de dispositivo inválido para envio de notificação push.");
            return;
        }

        Notification notification = criarNotification(titulo, corpo, imagemUrl);
        AndroidConfig androidConfig = criarAndroidConfig();
        ApnsConfig apnsConfig = criarApnsConfig();

        Message.Builder messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig);

        adicionarDados(messageBuilder, dados);

        enviarMensagem(messageBuilder.build(), "token: " + token);
    }

    private void enviarMensagem(Message message, String destinatario) {
        if (this.firebaseMessaging == null) {
            log.warn("Firebase Messaging não inicializado (credenciais não fornecidas). Push para [{}] não enviado.", destinatario);
            return;
        }

        try {
            String messageId = this.firebaseMessaging.send(message);
            log.info("Notificação push FCM enviada com sucesso para [{}]. ID: {}", destinatario, messageId);
        } catch (FirebaseMessagingException e) {
            log.error("Falha ao enviar notificação push via FCM para [{}]. Código: {}, Erro: {}",
                    destinatario, e.getMessagingErrorCode(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar mensagem FCM para [{}]: {}", destinatario, e.getMessage(), e);
        }
    }

    private Notification criarNotification(String titulo, String corpo, String imagemUrl) {
        Notification.Builder builder = Notification.builder()
                .setTitle(titulo)
                .setBody(corpo);

        if (imagemUrl != null && !imagemUrl.isBlank()) {
            builder.setImage(imagemUrl);
        }

        return builder.build();
    }

    private AndroidConfig criarAndroidConfig() {
        return AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setChannelId(CHANNEL_ID_ALERTA)
                        .setDefaultSound(true)
                        .setDefaultVibrateTimings(true)
                        .build())
                .build();
    }

    private ApnsConfig criarApnsConfig() {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setSound("default")
                        .build())
                .build();
    }

    private void adicionarDados(Message.Builder builder, Map<String, String> dados) {
        if (dados != null && !dados.isEmpty()) {
            dados.forEach((chave, valor) -> {
                if (chave != null && valor != null) {
                    builder.putData(chave, valor);
                }
            });
        }
    }
}
