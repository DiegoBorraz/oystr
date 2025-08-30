package com.br.oystr.service;

import com.br.oystr.model.Machine;

public interface SiteSpecificBot {
    Machine fetch(String url);
    boolean supports(String url);
}