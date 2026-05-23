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
@Service

public class SucursalService {
     private final SucursalRepository sucursalRepository;
    private final ComunaRepository comunaRepository;

    public  SucursalService(SucursalRepository sucursalRepository, ComunaRepository comunaRepository) {
        this.sucursalRepository = sucursalRepository;
        this.comunaRepository = comunaRepository;
    }

    public List<SucursalResponse> listarSucursales() {
        return sucursalRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<SucursalResponse> listarSucursalesPorComuna(Integer idComuna) {
        return sucursalRepository.findByComunaIdComuna(idComuna)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public SucursalResponse buscarSucursalPorId(Integer idSucursal) {
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + idSucursal));

        return convertirAResponse(sucursal);
    }

    public SucursalResponse crearSucursal(SucursalCreateRequest request) {
        Comuna comuna = comunaRepository.findById(request.getIdComuna())
                .orElseThrow(() -> new ResourceNotFoundException("Comuna no encontrada con ID: " + request.getIdComuna()));

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
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + idSucursal));

        Comuna comuna = comunaRepository.findById(request.getIdComuna())
                .orElseThrow(() -> new ResourceNotFoundException("Comuna no encontrada con ID: " + request.getIdComuna()));

        sucursal.setNombreSucursal(request.getNombreSucursal());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setHorarioAtencion(request.getHorarioAtencion());
        sucursal.setComuna(comuna);

        Sucursal sucursalActualizada = sucursalRepository.save(sucursal);

        return convertirAResponse(sucursalActualizada);
    }

    public void eliminarSucursal(Integer idSucursal) {
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + idSucursal));

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
