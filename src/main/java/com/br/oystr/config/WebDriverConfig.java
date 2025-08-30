package com.br.oystr.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.Duration;

@Configuration
public class WebDriverConfig {

    @Bean
    @Scope("prototype") // MUDANÇA CRÍTICA: scope prototype para cada injeção ser uma nova instância
    public WebDriver webDriver() {
        // Configura automaticamente o ChromeDriver
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Configurações para melhor performance e stealth
        options.addArguments(
                "--headless=new", // Modo headless moderno
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080",
                "--disable-extensions",
                "--disable-popup-blocking",
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        );

        // Opções para evitar detecção como bot
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);

        // Configurações de timeout
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(10))
                .pageLoadTimeout(Duration.ofSeconds(30))
                .scriptTimeout(Duration.ofSeconds(10));

        return driver;
    }
}