package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String mensaje;
    private LocalDateTime timestamp;
    private Map<String, String> campos;

    public static ErrorResponse of(int status, String error, String mensaje) {
        ErrorResponse respuesta = new ErrorResponse();
        respuesta.setStatus(status);
        respuesta.setError(error);
        respuesta.setMensaje(mensaje);
        respuesta.setTimestamp(LocalDateTime.now());
        return respuesta;
    }
}
