package github.qziul.iopet.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import github.qziul.iopet.domain.model.AlertaGeofencing;
import github.qziul.iopet.domain.model.HistoricoLocalizacao;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.service.impl.ImplNotificacaoService;
import github.qziul.iopet.utils.SpatialUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImplNotificacaoServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    private ImplNotificacaoService notificacaoService;

    @BeforeEach
    void setUp() {
        notificacaoService = new ImplNotificacaoService(Optional.of(firebaseMessaging));
    }

    @Test
    @DisplayName("Deve enviar notificação push via FCM com sucesso para o tópico do tutor")
    void deveEnviarNotificacaoPushComSucesso() throws Exception {
        UUID tutorUuid = UUID.randomUUID();
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        tutor.setUuid(tutorUuid);
        tutor.setNome("João");

        Pet pet = new Pet();
        pet.setId(10L);
        pet.setUuid(UUID.randomUUID());
        pet.setNome("Rex");
        pet.setUrlFoto("https://exemplo.com/foto.jpg");
        pet.setTutor(tutor);

        HistoricoLocalizacao historico = new HistoricoLocalizacao();
        historico.setPosicao(SpatialUtils.criarPonto(-23.55052, -46.633308));

        AlertaGeofencing alerta = new AlertaGeofencing();
        alerta.setId(100L);
        alerta.setPet(pet);
        alerta.setHistoricoLocalizacao(historico);
        alerta.setMensagem("Rex saiu da zona de segurança!");
        alerta.setData(LocalDateTime.now());

        when(firebaseMessaging.send(any(Message.class))).thenReturn("projects/iopet/messages/12345");

        assertDoesNotThrow(() -> notificacaoService.enviarNotificacaoPush(alerta));

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging, times(1)).send(messageCaptor.capture());
    }

    @Test
    @DisplayName("Não deve disparar exceção se FirebaseMessaging não estiver inicializado")
    void naoDeveDispararErroQuandoFirebaseNaoInicializado() {
        ImplNotificacaoService serviceSemFirebase = new ImplNotificacaoService(Optional.empty());

        AlertaGeofencing alerta = new AlertaGeofencing();
        assertDoesNotThrow(() -> serviceSemFirebase.enviarNotificacaoPush(alerta));
    }

    @Test
    @DisplayName("Deve abortar envio sem lançar erro quando alerta for nulo ou sem tutor")
    void deveAbortarQuandoDadosIncompletos() throws Exception {
        notificacaoService.enviarNotificacaoPush(null);
        verify(firebaseMessaging, never()).send(any(Message.class));

        AlertaGeofencing alertaSemPet = new AlertaGeofencing();
        notificacaoService.enviarNotificacaoPush(alertaSemPet);
        verify(firebaseMessaging, never()).send(any(Message.class));

        Pet petSemTutor = new Pet();
        alertaSemPet.setPet(petSemTutor);
        notificacaoService.enviarNotificacaoPush(alertaSemPet);
        verify(firebaseMessaging, never()).send(any(Message.class));
    }

    @Test
    @DisplayName("Deve enviar por token com sucesso")
    void deveEnviarPorTokenComSucesso() throws Exception {
        when(firebaseMessaging.send(any(Message.class))).thenReturn("msg-token-1");

        assertDoesNotThrow(() -> notificacaoService.enviarPorToken(
                "fcm-device-token-123",
                "Título",
                "Corpo da mensagem",
                Map.of("chave", "valor")
        ));

        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }

    @Test
    @DisplayName("Deve tratar FirebaseMessagingException sem propagar exceção")
    void deveTratarFirebaseMessagingException() throws Exception {
        FirebaseMessagingException fcmException = mock(FirebaseMessagingException.class);
        when(firebaseMessaging.send(any(Message.class))).thenThrow(fcmException);

        assertDoesNotThrow(() -> notificacaoService.enviarPorTopico(
                "topico-teste",
                "Título",
                "Corpo",
                Map.of("chave", "valor")
        ));

        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }
}
