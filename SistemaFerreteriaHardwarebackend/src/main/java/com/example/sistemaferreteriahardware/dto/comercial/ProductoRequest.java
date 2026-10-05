package com.example.sistemaferreteriahardware.dto.comercial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductoRequest {
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 120)
    private String nombre;

    @Size(max = 80)
    private String modelo;

    @Size(max = 500)
    private String descripcion;

    @NotNull(message = "El precio de compra es obligatorio")
    @PositiveOrZero(message = "El precio de compra no puede ser negativo")
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @PositiveOrZero(message = "El precio de venta no puede ser negativo")
    private BigDecimal precioVenta;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La marca es obligatoria")
    private Long marcaId;

    @NotNull(message = "El stock inicial es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stockInicial;

    @NotNull(message = "El stock mínimo es obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @Size(max = 120)
    private String ubicacion;

    @Size(max = 20)
    private String estado;
}
