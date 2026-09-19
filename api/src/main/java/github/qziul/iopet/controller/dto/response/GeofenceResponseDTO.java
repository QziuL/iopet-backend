package github.qziul.iopet.controller.dto.response;

import github.qziul.iopet.controller.dto.request.GeoPointDTO;
import java.util.List;
import java.util.UUID;

public record GeofenceResponseDTO(
        String id,
        UUID petId,
        String name,
        List<GeoPointDTO> points,
        Double area,
        Boolean active
) {}
