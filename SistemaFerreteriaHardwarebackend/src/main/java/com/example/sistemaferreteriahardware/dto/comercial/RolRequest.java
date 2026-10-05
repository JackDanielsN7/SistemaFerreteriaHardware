package com.example.sistemaferreteriahardware.dto.comercial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolRequest {
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre del rol no puede superar 50 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no puede superar 200 caracteres")
    private String descripcion;
}
