package com.example.sistemaferreteriahardware.dto.comercial;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 80)
    private String apellido;

    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 8, max = 15, message = "El documento debe tener entre 8 y 15 caracteres")
    @Pattern(regexp = "^[0-9A-Za-z-]{8,15}$", message = "El documento no es válido")
    private String documento;

    @Size(max = 20)
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    @Size(max = 120)
    private String correo;
}
