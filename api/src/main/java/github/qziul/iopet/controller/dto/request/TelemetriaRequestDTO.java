package github.qziul.iopet.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelemetriaRequestDTO(
        @JsonProperty("device_id")
        @NotBlank(message = "O identificador do dispositivo (device_id) é obrigatório")
        @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", message = "Formato de endereço MAC inválido")
        String deviceId,

        @JsonProperty("latitude")
        @NotNull(message = "A latitude é obrigatória")
        Double latitude,

        @JsonProperty("longitude")
        @NotNull(message = "A longitude é obrigatória")
        Double longitude,

        @JsonProperty("battery")
        Integer battery
) {}
