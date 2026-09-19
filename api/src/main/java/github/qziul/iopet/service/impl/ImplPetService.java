package github.qziul.iopet.service.impl;

import github.qziul.iopet.controller.dto.request.GeofenceRequestDTO;
import github.qziul.iopet.controller.dto.request.GeoPointDTO;
import github.qziul.iopet.controller.dto.request.PetRequestDTO;
import github.qziul.iopet.controller.dto.response.GeofenceResponseDTO;
import github.qziul.iopet.controller.dto.response.LocationPointDTO;
import github.qziul.iopet.controller.dto.response.TrackingResponseDTO;
import github.qziul.iopet.domain.model.DispositivoIot;
import github.qziul.iopet.domain.model.HistoricoLocalizacao;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.model.Tutor;
import github.qziul.iopet.domain.repository.DispositivoIotRepository;
import github.qziul.iopet.domain.repository.HistoricoLocalizacaoRepository;
import github.qziul.iopet.domain.repository.PetRepository;
import github.qziul.iopet.service.IDispositivoIotService;
import github.qziul.iopet.service.IPetService;
import github.qziul.iopet.service.ITutorService;
import github.qziul.iopet.utils.SpatialUtils;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ImplPetService implements IPetService {
    private final PetRepository petRepository;
    private final ITutorService tutorService;
    private final IDispositivoIotService dispositivoService;
    private final HistoricoLocalizacaoRepository historicoRepository;
    private final DispositivoIotRepository dispositivoIotRepository;

    public ImplPetService(PetRepository petRepository,
                          ITutorService tutorService,
                          IDispositivoIotService dispositivoService,
                          HistoricoLocalizacaoRepository historicoRepository,
                          DispositivoIotRepository dispositivoIotRepository) {
        this.petRepository = petRepository;
        this.tutorService = tutorService;
        this.dispositivoService = dispositivoService;
        this.historicoRepository = historicoRepository;
        this.dispositivoIotRepository = dispositivoIotRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> listarPetsDoTutor(Long tutorId) {
        return this.petRepository.findByTutorId(tutorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> encontrarPorUuid(UUID uuid) {
        return this.petRepository.findByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> encontrarPorNome(String nome) {
        return this.petRepository.findByNome(nome);
    }

    @Override
    @Transactional
    public Pet cadastrar(PetRequestDTO dto) {
        Tutor tutor = null;
        if (dto.idPublicoTutor() != null) {
            tutor = this.tutorService.encontrarPorUuid(dto.idPublicoTutor()).orElse(null);
        }
        if (tutor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado.");
        }
        Pet novoPet = PetRequestDTO.toEntity(dto, tutor);
        return this.petRepository.save(novoPet);
    }

    @Override
    @Transactional
    public Pet cadastrar(PetRequestDTO dto, Tutor tutor) {
        if (tutor == null && dto.idPublicoTutor() != null) {
            tutor = this.tutorService.encontrarPorUuid(dto.idPublicoTutor()).orElse(null);
        }
        if (tutor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutor autenticado obrigatório para cadastro.");
        }
        Pet novoPet = PetRequestDTO.toEntity(dto, tutor);
        return this.petRepository.save(novoPet);
    }

    @Override
    @Transactional
    public Pet atualizar(Pet pet) {
        Pet petExistente = this.petRepository.findByUuid(pet.getUuid()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado.")
        );
        if (pet.getNome() != null && !pet.getNome().isBlank()) petExistente.setNome(pet.getNome());
        if (pet.getRaca() != null) petExistente.setRaca(pet.getRaca());
        if (pet.getSexo() != null) petExistente.setSexo(pet.getSexo());
        if (pet.getEspecie() != null) petExistente.setEspecie(pet.getEspecie());
        if (pet.getPorte() != null) petExistente.setPorte(pet.getPorte());
        if (pet.getUrlFoto() != null) petExistente.setUrlFoto(pet.getUrlFoto());
        if (pet.getDataNascimento() != null) petExistente.setDataNascimento(pet.getDataNascimento());
        if (pet.getDescricao() != null) petExistente.setDescricao(pet.getDescricao());
        return this.petRepository.save(petExistente);
    }

    @Override
    @Transactional
    public void deletar(UUID uuid) {
        Pet pet = this.petRepository.findByUuid(uuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado.")
        );
        if (pet.getDispositivoIot() != null) {
            DispositivoIot disp = pet.getDispositivoIot();
            disp.setPet(null);
            disp.setAtivo(false);
            this.dispositivoIotRepository.save(disp);
            pet.setDispositivoIot(null);
        }
        this.petRepository.delete(pet);
    }

    @Override
    @Transactional
    public void vincularDispositivoIot(UUID idPublicoPet, String enderecoMac) {
        this.dispositivoService.vincularDispositivoAoPet(idPublicoPet, enderecoMac);
    }

    @Override
    @Transactional(readOnly = true)
    public GeofenceResponseDTO obterGeofence(UUID petUuid) {
        Pet pet = this.petRepository.findByUuid(petUuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado.")
        );
        if (pet.getZonaSeguranca() == null) {
            return new GeofenceResponseDTO("geo-" + pet.getUuid(), pet.getUuid(), "Área Segura", List.of(), 0.0, false);
        }
        List<GeoPointDTO> pontos = SpatialUtils.extrairPontos(pet.getZonaSeguranca());
        double areaCalculada = calcularAreaAproximada(pontos);
        return new GeofenceResponseDTO("geo-" + pet.getUuid(), pet.getUuid(), "Área Segura", pontos, areaCalculada, true);
    }

    @Override
    @Transactional
    public GeofenceResponseDTO salvarGeofence(UUID petUuid, GeofenceRequestDTO dto) {
        Pet pet = this.petRepository.findByUuid(petUuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado.")
        );

        if (Boolean.FALSE.equals(dto.active())) {
            pet.setZonaSeguranca(null);
        } else if (dto.points() != null && dto.points().size() >= 3) {
            Polygon poligono = SpatialUtils.criarPoligono(dto.points());
            pet.setZonaSeguranca(poligono);
        }
        this.petRepository.save(pet);
        return obterGeofence(petUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackingResponseDTO obterTracking(UUID petUuid) {
        Pet pet = this.petRepository.findByUuid(petUuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado.")
        );

        DispositivoIot dispositivo = pet.getDispositivoIot();
        if (dispositivo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não vinculado a este pet.");
        }

        Optional<HistoricoLocalizacao> ultimoHistorico = historicoRepository.findTop1ByDispositivoIotOrderByDataDesc(dispositivo);
        LocationPointDTO locationPoint;
        if (ultimoHistorico.isPresent()) {
            HistoricoLocalizacao h = ultimoHistorico.get();
            locationPoint = new LocationPointDTO(
                    (double) h.getLatitude(),
                    (double) h.getLongitude(),
                    h.getData().toString(),
                    "Posição atual do dispositivo"
            );
        } else {
            locationPoint = new LocationPointDTO(-25.514, -48.522, LocalDateTime.now().toString(), "Aguardando sinal GPS");
        }

        boolean online = dispositivo.isAtivo() && dispositivo.getUltimaComunicacao() != null &&
                dispositivo.getUltimaComunicacao().isAfter(LocalDateTime.now().minusMinutes(30));

        return new TrackingResponseDTO(
                pet.getUuid(),
                dispositivo.getEnderecoMac(),
                locationPoint,
                dispositivo.getBateriaNivel(),
                online ? "online" : "offline",
                12,
                dispositivo.getUltimaComunicacao() != null ? dispositivo.getUltimaComunicacao().toString() : LocalDateTime.now().toString()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationPointDTO> obterHistorico(UUID petUuid) {
        Pet pet = this.petRepository.findByUuid(petUuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado.")
        );
        DispositivoIot dispositivo = pet.getDispositivoIot();
        if (dispositivo == null) return List.of();

        List<HistoricoLocalizacao> historicos = historicoRepository.findByDispositivoIotOrderByDataDesc(dispositivo, PageRequest.of(0, 50));
        return historicos.stream()
                .map(h -> new LocationPointDTO(
                        (double) h.getLatitude(),
                        (double) h.getLongitude(),
                        h.getData().toString(),
                        null
                ))
                .toList();
    }

    private double calcularAreaAproximada(List<GeoPointDTO> points) {
        if (points == null || points.size() < 3) return 0.0;
        double a = 0.0;
        int n = points.size();
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            a += points.get(i).longitude() * points.get(j).latitude();
            a -= points.get(j).longitude() * points.get(i).latitude();
        }
        return Math.round(Math.abs(a * 111320.0 * 111320.0) / 2.0);
    }
}
