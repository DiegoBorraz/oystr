package com.br.oystr.config;


import jakarta.annotation.PreDestroy;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
public class WebDriverCleanup {

    private final WebDriver webDriver;

    public WebDriverCleanup(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @PreDestroy
    public void cleanup() {
        if (webDriver != null) {
            try {
                webDriver.quit();
                System.out.println("WebDriver fechado com sucesso");
            } catch (Exception e) {
                System.err.println("Erro ao fechar WebDriver: " + e.getMessage());
            }
        }
    }
}