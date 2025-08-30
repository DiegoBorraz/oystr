package com.br.oystr.service;

import com.br.oystr.model.Machine;

public interface Bot {
    Machine fetch(String url);
}