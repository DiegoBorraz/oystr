package com.br.oystr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Machine {
    private String model;
    private String contractType;
    private String brand;
    private Integer year;
    private String city;
    private String price;
    private String imageUrl;
    private String workingHours = "Não informado.";
    private String url;
}