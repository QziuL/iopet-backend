package github.qziul.iopet.queue;

import github.qziul.iopet.controller.dto.request.TelemetriaRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMQJsonConversionTest {

    @Test
    @DisplayName("Deve converter payload MQTT JSON cru em TelemetriaRequestDTO usando DefaultClassMapper")
    void deveConverterPayloadSemHeaderDeTipo() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setDefaultType(TelemetriaRequestDTO.class);
        converter.setClassMapper(classMapper);

        String json = "{\"device_id\":\"AA:BB:CC:11:22:33\",\"latitude\":-25.123456,\"longitude\":-49.123456,\"battery\":100}";
        MessageProperties properties = new MessageProperties();
        Message message = new Message(json.getBytes(StandardCharsets.UTF_8), properties);

        Object result = converter.fromMessage(message);

        assertNotNull(result);
        assertInstanceOf(TelemetriaRequestDTO.class, result);
        TelemetriaRequestDTO dto = (TelemetriaRequestDTO) result;
        assertEquals("AA:BB:CC:11:22:33", dto.deviceId());
        assertEquals(-25.123456, dto.latitude());
        assertEquals(-49.123456, dto.longitude());
        assertEquals(100, dto.battery());
    }
}
