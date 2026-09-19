package github.qziul.iopet.controller.dto.response;

import java.util.UUID;

public record TrackingResponseDTO(
        UUID petId,
        String deviceId,
        LocationPointDTO currentLocation,
        Integer battery,
        String signalStatus,
        Integer precision,
        String lastUpdated
) {}
