package github.qziul.iopet.service.impl;

import github.qziul.iopet.domain.model.AlertaGeofencing;
import github.qziul.iopet.domain.repository.AlertaGeofencingRepository;
import github.qziul.iopet.service.IGeofencingService;
import org.springframework.stereotype.Service;

@Service
public class ImplGeofecingService implements IGeofencingService {
    private final AlertaGeofencingRepository alertaGeofencingRepository;

    public ImplGeofecingService(AlertaGeofencingRepository alertaGeofencingRepository) {
        this.alertaGeofencingRepository = alertaGeofencingRepository;
    }

    @Override
    public boolean verificarLimitesGeofecing(Long petId, double latitude, double longitude) {
        return false;
    }

    @Override
    public AlertaGeofencing registrarAlertaFuga(Long petId) {
        return null;
    }
}
