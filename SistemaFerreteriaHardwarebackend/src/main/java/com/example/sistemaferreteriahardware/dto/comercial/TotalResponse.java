package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TotalResponse {
    private BigDecimal total;
    private long cantidad;
}
