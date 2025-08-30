package com.br.oystr.service;

import com.br.oystr.model.Machine;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GenerateJSON {

    private final ObjectMapper objectMapper;

    // Injetar ObjectMapper via construtor
    public GenerateJSON(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void generateJsonFile(List<Machine> machines) {
        try {
            // Configurar o ObjectMapper para formato bonito
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Criar nome do arquivo com timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "machines_" + timestamp + ".json";

            // Criar diretório de output se não existir
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Caminho completo do arquivo
            File outputFile = new File(outputDir, fileName);

            // Escrever no arquivo
            objectMapper.writeValue(outputFile, machines);

            System.out.println("✅ Arquivo JSON gerado com sucesso: " + outputFile.getAbsolutePath());
            System.out.println("📊 Total de máquinas extraídas: " + machines.size());

        } catch (IOException e) {
            System.err.println("❌ Erro ao gerar arquivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método adicional para gerar com nome personalizado
    public void generateJsonFile(List<Machine> machines, String customFileName) {
        try {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, customFileName);
            objectMapper.writeValue(outputFile, machines);

            System.out.println("✅ Arquivo JSON gerado: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Erro ao gerar arquivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}