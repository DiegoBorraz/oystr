package com.br.oystr.service;

import com.br.oystr.model.Machine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Primary
public class BotOrchestrator {
    private final List<SiteSpecificBot> siteBots; // Todos os bots são injetados aqui

    public BotOrchestrator(List<SiteSpecificBot> siteBots) {
        this.siteBots = siteBots;
    }

    public Machine fetch(String url) {
        for (SiteSpecificBot bot : siteBots) {
            if (bot.supports(url)) {
                return bot.fetch(url);
            }
        }
        return new Machine();
    }



}