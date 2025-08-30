package com.br.oystr;

import com.br.oystr.model.Machine;
import com.br.oystr.service.impl.BotImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class OystrApplication implements CommandLineRunner {

    private final BotImpl bot;

    public OystrApplication(BotImpl bot) {
        this.bot = bot;
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
        for (String url : urls) {
            machineList.add(bot.fetch(url));
        }
        System.out.println("Extracted machine: " + machineList);
    }
}