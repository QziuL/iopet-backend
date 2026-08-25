package github.qziul.iopet.service.impl;

import github.qziul.iopet.domain.model.AlertaGeofecing;
import github.qziul.iopet.domain.repository.AlertaGeofecingRepository;
import github.qziul.iopet.service.IGeofencingService;
import org.springframework.stereotype.Service;

@Service
public class ImplGeofecingService implements IGeofencingService {
    private final AlertaGeofecingRepository alertaGeofecingRepository;

    public ImplGeofecingService(AlertaGeofecingRepository alertaGeofecingRepository) {
        this.alertaGeofecingRepository = alertaGeofecingRepository;
    }

    @Override
    public boolean verificarLimitesGeofecing(Long petId, double latitude, double longitude) {
        return false;
    }

    @Override
    public AlertaGeofecing registrarAlertaFuga(Long petId) {
        return null;
    }
}
