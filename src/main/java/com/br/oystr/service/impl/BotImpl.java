package com.br.oystr.service.impl;

import com.br.oystr.model.Machine;
import com.br.oystr.service.Bot;
import com.br.oystr.service.SiteSpecificBot;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Primary
public class BotImpl implements Bot {
    private final WebDriver webDriver;
    private final List<SiteSpecificBot> siteBots; // Todos os bots são injetados aqui

    public BotImpl(WebDriver webDriver, List<SiteSpecificBot> siteBots) {
        this.webDriver = webDriver;
        this.siteBots = siteBots;
    }

    @Override
    public Machine fetch(String url) {
        for (SiteSpecificBot bot : siteBots) {
            if (bot.supports(url)) {
                return bot.fetch(url);
            }
        }
        return new Machine(); // Retorna vazio se nenhum bot suportar
    }



}