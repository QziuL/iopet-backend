package github.qziul.iopet.service;

import github.qziul.iopet.controller.dto.request.GeofenceRequestDTO;
import github.qziul.iopet.controller.dto.request.PetRequestDTO;
import github.qziul.iopet.controller.dto.response.GeofenceResponseDTO;
import github.qziul.iopet.controller.dto.response.LocationPointDTO;
import github.qziul.iopet.controller.dto.response.TrackingResponseDTO;
import github.qziul.iopet.domain.model.Pet;
import github.qziul.iopet.domain.model.Tutor;

import java.util.*;

public interface IPetService {
    List<Pet> listarPetsDoTutor(Long tutorId);
    Optional<Pet> encontrarPorUuid(UUID idPublico);
    Optional<Pet> encontrarPorNome(String nome);
    Pet cadastrar(PetRequestDTO petDTO);
    Pet cadastrar(PetRequestDTO petDTO, Tutor tutor);
    Pet atualizar(Pet pet);
    void deletar(UUID idPublico);
    void vincularDispositivoIot(UUID idPublico, String enderecoMac);
    GeofenceResponseDTO obterGeofence(UUID petUuid);
    GeofenceResponseDTO salvarGeofence(UUID petUuid, GeofenceRequestDTO dto);
    TrackingResponseDTO obterTracking(UUID petUuid);
    List<LocationPointDTO> obterHistorico(UUID petUuid);
}
