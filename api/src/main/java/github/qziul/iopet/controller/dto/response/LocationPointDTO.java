package github.qziul.iopet.controller.dto.response;

public record LocationPointDTO(
        Double latitude,
        Double longitude,
        String timestamp,
        String address
) {}
