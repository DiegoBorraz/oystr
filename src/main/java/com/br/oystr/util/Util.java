package com.br.oystr.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class Util {

    public static String extractSearchTerm(String path) {
        // Remover a barra inicial e a extensão .html
        String filename = path.substring(1); // Remove a primeira barra
        if (filename.endsWith(".html")) {
            filename = filename.substring(0, filename.length() - 5);
        }
        if (filename.endsWith("-oferta")) {
            filename = filename.substring(0, filename.length() - 7);
        }

        // Remover números e identificadores no final
        String[] parts = filename.split("-");
        StringBuilder searchTerm = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            // Parar quando encontrar números (como 7230j)
            if (parts[i].matches(".*\\d.*")) {
                break;
            }
            if (searchTerm.length() > 0) {
                searchTerm.append("-");
            }
            searchTerm.append(parts[i]);
        }

        return searchTerm.toString();
    }
    public static BigDecimal convertPriceToBigDecimal(String priceText) {
        try {
            // Remover R$, espaços, pontos e converter para BigDecimal
            String cleanPrice = priceText.replace("R$", "")
                    .replace("&nbsp;", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim();

            return new BigDecimal(cleanPrice);
        } catch (Exception e) {
            log.warn("Não foi possível converter o preço: {}", priceText);
            return null;
        }
    }
}
