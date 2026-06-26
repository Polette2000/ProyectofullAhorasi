package cl.duoc.SucursalesPerfulandia.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.duoc.SucursalesPerfulandia.dto.request.SucursalCreateRequest;
import cl.duoc.SucursalesPerfulandia.dto.request.SucursalUpdateRequest;
import cl.duoc.SucursalesPerfulandia.dto.response.SucursalResponse;
import cl.duoc.SucursalesPerfulandia.exception.ResourceNotFoundException;
import cl.duoc.SucursalesPerfulandia.model.Comuna;
import cl.duoc.SucursalesPerfulandia.model.Sucursal;
import cl.duoc.SucursalesPerfulandia.repository.ComunaRepository;
import cl.duoc.SucursalesPerfulandia.repository.SucursalRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class SucursalService {
     private final SucursalRepository sucursalRepository;
    private final ComunaRepository comunaRepository;

    public  SucursalService(SucursalRepository sucursalRepository, ComunaRepository comunaRepository) {
        this.sucursalRepository = sucursalRepository;
        this.comunaRepository = comunaRepository;
    }

    public List<SucursalResponse> listarSucursales() {
        log.info("Listando todas las sucursales");
        return sucursalRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<SucursalResponse> listarSucursalesPorComuna(Integer idComuna) {
        log.info("Listando sucursales de la comuna ID: {}", idComuna);
        return sucursalRepository.findByComunaIdComuna(idComuna)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public SucursalResponse buscarSucursalPorId(Integer idSucursal) {
        log.info("Buscando sucursal ID: {}", idSucursal);
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> {
                    log.warn("Sucursal no encontrada ID: {}", idSucursal);
                    return new ResourceNotFoundException("Sucursal no encontrada con ID: " + idSucursal);
                });

        return convertirAResponse(sucursal);
    }

    public SucursalResponse crearSucursal(SucursalCreateRequest request) {
        log.info("Creando sucursal '{}' en comuna ID: {}", request.getNombreSucursal(), request.getIdComuna());
        Comuna comuna = comunaRepository.findById(request.getIdComuna())
                .orElseThrow(() -> {
                    log.warn("Comuna no encontrada ID: {}", request.getIdComuna());
                    return new ResourceNotFoundException("Comuna no encontrada con ID: " + request.getIdComuna());
                });

        Sucursal sucursal = new Sucursal();
        sucursal.setNombreSucursal(request.getNombreSucursal());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setHorarioAtencion(request.getHorarioAtencion());
        sucursal.setComuna(comuna);

        Sucursal sucursalGuardada = sucursalRepository.save(sucursal);

        return convertirAResponse(sucursalGuardada);
    }

    public SucursalResponse actualizarSucursal(Integer idSucursal, SucursalUpdateRequest request) {
        log.info("Actualizando sucursal ID: {}", idSucursal);
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> {
                    log.warn("Sucursal no encontrada ID: {}", idSucursal);
                    return new ResourceNotFoundException("Sucursal no encontrada con ID: " + idSucursal);
                });

        Comuna comuna = comunaRepository.findById(request.getIdComuna())
                .orElseThrow(() -> {
                    log.warn("Comuna no encontrada ID: {}", request.getIdComuna());
                    return new ResourceNotFoundException("Comuna no encontrada con ID: " + request.getIdComuna());
                });

        sucursal.setNombreSucursal(request.getNombreSucursal());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setHorarioAtencion(request.getHorarioAtencion());
        sucursal.setComuna(comuna);

        Sucursal sucursalActualizada = sucursalRepository.save(sucursal);

        return convertirAResponse(sucursalActualizada);
    }

    public void eliminarSucursal(Integer idSucursal) {
        log.info("Eliminando sucursal ID: {}", idSucursal);
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> {
                    log.warn("Sucursal no encontrada ID: {}", idSucursal);
                    return new ResourceNotFoundException("Sucursal no encontrada con ID: " + idSucursal);
                });

        sucursalRepository.delete(sucursal);
    }

    private SucursalResponse convertirAResponse(Sucursal sucursal) {
        SucursalResponse dto = new SucursalResponse();

        dto.setIdSucursal(sucursal.getIdSucursal());
        dto.setNombreSucursal(sucursal.getNombreSucursal());
        dto.setDireccion(sucursal.getDireccion());
        dto.setTelefono(sucursal.getTelefono());
        dto.setHorarioAtencion(sucursal.getHorarioAtencion());

        dto.setIdComuna(sucursal.getComuna().getIdComuna());
        dto.setNombreComuna(sucursal.getComuna().getNombreComuna());
        dto.setRegion(sucursal.getComuna().getRegion());

        return dto;
    }

}
