package github.qziul.iopet.service;

import github.qziul.iopet.domain.model.AlertaGeofencing;

public interface IGeofencingService {
    boolean verificarLimitesGeofecing(Long petId, double latitude, double longitude);
    AlertaGeofencing registrarAlertaFuga(Long petId);
}
