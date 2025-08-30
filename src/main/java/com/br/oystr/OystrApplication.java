package com.br.oystr;

import com.br.oystr.model.Machine;
import com.br.oystr.service.GenerateJSON;
import com.br.oystr.service.impl.BotImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class OystrApplication implements CommandLineRunner {

    private final BotImpl bot;
    private final GenerateJSON generateJSON;

    public OystrApplication(BotImpl bot, GenerateJSON generateJSON) {
        this.bot = bot;
        this.generateJSON = generateJSON;
    }

    public static void main(String[] args) {
        SpringApplication.run(OystrApplication.class, args);
    }

    @Override
    public void run(String... args) {
        String[] urls = new String[]{
                "https://www.agrofy.com.br/trator-john-deere-7230j-205340.html",
                "https://www.agrofy.com.br/trator-case-ih-puma-215-207085.html",
                "https://www.tratoresecolheitadeiras.com.br/veiculo/uberlandia/mg/plataforma-colheitadeira/gts/flexer-xs-45/2023/45-pes/draper/triamaq-tratores/1028839",
                "https://www.tratoresecolheitadeiras.com.br/veiculo/ibiruba/rs/trator/case/puma-185/2023/tracao-4x4/cabine-cabinado/maisner-tratores/1149323",
                "https://www.mercadomaquinas.com.br/anuncio/225351-retro-escavadeira-caterpillar-416e-2014-londrina-pr",
                "https://www.mercadomaquinas.com.br/anuncio/236623-mini-escavadeira-bobcat-e27z-2019-sete-lagoas-mg"
        };

        List<Machine> machineList = new ArrayList<>();

        try {
            for (String url : urls) {
                System.out.println("Processando URL: " + url);
                Machine machine = bot.fetch(url);
                machineList.add(machine);
                System.out.println("Máquina extraída: " + machine.getModel());
            }

            // Gerar arquivo JSON usando a classe de serviço
            generateJSON.generateJsonFile(machineList);

        } catch (Exception e) {
            System.err.println("Erro durante a execução: " + e.getMessage());
            e.printStackTrace();
        }
    }
}