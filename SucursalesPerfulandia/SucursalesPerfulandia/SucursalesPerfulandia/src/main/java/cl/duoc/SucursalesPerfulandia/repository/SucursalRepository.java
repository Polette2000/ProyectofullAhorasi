package cl.duoc.SucursalesPerfulandia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.SucursalesPerfulandia.model.Sucursal;

public interface SucursalRepository  extends JpaRepository<Sucursal, Integer> {

    List<Sucursal> findByComunaIdComuna(Integer idComuna);
    

}
