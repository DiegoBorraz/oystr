package com.br.oystr;


import org.openqa.selenium.WebDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestDriverApplication implements CommandLineRunner {

    private final WebDriver webDriver;

    public TestDriverApplication(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public static void main(String[] args) {
        SpringApplication.run(TestDriverApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            webDriver.get("https://www.google.com");
            System.out.println("Título da página: " + webDriver.getTitle());
            System.out.println("URL atual: " + webDriver.getCurrentUrl());
            System.out.println("WebDriver configurado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro no WebDriver: " + e.getMessage());
        }
    }
}