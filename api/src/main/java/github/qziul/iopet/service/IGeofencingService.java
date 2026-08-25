package github.qziul.iopet.service;

import github.qziul.iopet.domain.model.AlertaGeofecing;

public interface IGeofencingService {
    boolean verificarLimitesGeofecing(Long petId, double latitude, double longitude);
    AlertaGeofecing registrarAlertaFuga(Long petId);
}
