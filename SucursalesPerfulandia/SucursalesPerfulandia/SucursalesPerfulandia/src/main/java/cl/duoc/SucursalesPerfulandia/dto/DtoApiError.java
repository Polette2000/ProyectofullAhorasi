package cl.duoc.SucursalesPerfulandia.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Esta clase se usa como respuesta cuando ocurre un error en la API.
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DtoApiError {

    private LocalDate timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String claseException;
}
