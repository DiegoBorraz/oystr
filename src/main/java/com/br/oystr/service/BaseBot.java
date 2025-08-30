package com.br.oystr.service;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Slf4j
public abstract class BaseBot implements SiteSpecificBot {
    protected final WebDriver webDriver;
    protected static final Duration TIMEOUT = Duration.ofSeconds(30);
    protected static final Duration POLLING_INTERVAL = Duration.ofMillis(500);

    protected BaseBot(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    protected WebDriverWait createWait() {
        return new WebDriverWait(webDriver, TIMEOUT);
    }


    protected void navigateToUrl(String url) {
        webDriver.get(url);
        log.info("Página carregada: {}", webDriver.getTitle());
    }

    protected String extractTextFromElement(By locator) {
        try {
            return webDriver.findElement(locator).getText().trim();
        } catch (Exception e) {
            log.warn("Elemento não encontrado: {}", locator);
            return "";
        }
    }
}
