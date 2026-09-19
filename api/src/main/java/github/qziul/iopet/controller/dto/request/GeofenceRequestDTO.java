package github.qziul.iopet.controller.dto.request;

import java.util.List;

public record GeofenceRequestDTO(
        String name,
        List<GeoPointDTO> points,
        Boolean active,
        Double area
) {}
