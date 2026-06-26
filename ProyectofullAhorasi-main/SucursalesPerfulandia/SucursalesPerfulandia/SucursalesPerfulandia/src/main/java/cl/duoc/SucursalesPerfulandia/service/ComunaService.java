package cl.duoc.SucursalesPerfulandia.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.duoc.SucursalesPerfulandia.dto.response.ComunaResponse;
import cl.duoc.SucursalesPerfulandia.model.Comuna;
import cl.duoc.SucursalesPerfulandia.repository.ComunaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComunaService {

    private final ComunaRepository comunaRepository;

    public List<ComunaResponse> listarComunas() {
        return comunaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    private ComunaResponse convertirAResponse(Comuna comuna) {
        ComunaResponse response = new ComunaResponse();

        response.setIdComuna(comuna.getIdComuna());
        response.setNombreComuna(comuna.getNombreComuna());
        response.setRegion(comuna.getRegion());

        return response;
    }

}
