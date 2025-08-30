package com.br.oystr.service.impl;


import com.br.oystr.model.Machine;
import com.br.oystr.service.SiteSpecificBot;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class AgrofyBot implements SiteSpecificBot {
    private final WebDriver webDriver;

    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final Duration POLLING_INTERVAL = Duration.ofMillis(500);

    @Autowired
    public AgrofyBot(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public Machine fetch(String url) {
        log.info("Acessando página da Agrofy: {}", url);
        Machine machine = new Machine();
        WebDriver driver = this.webDriver;

        try {
            // 1. Acessar a URL
            driver.get(url);
            log.info("Página carregada: {}", driver.getTitle());
            // 2. Aguardar o carregamento dos elementos
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
            wait.pollingEvery(POLLING_INTERVAL);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul[data-test=contentTable-list]")));
            // 3. ACESSAR DIRETAMENTE O LINK DO PRIMEIRO RESULTADO
            WebElement dataDiv = driver.findElement( By.cssSelector("ul[data-test=contentTable-list]"));
            String htmlContent = dataDiv.getAttribute("outerHTML");
            machine = extractDetailElement(htmlContent);
            machine.setPrice(extractPrice(driver));
            machine.setWorkingHours(extractWorkHours(driver));
            machine.setImageUrl(extractImage(driver));
            machine.setUrl(url);
            return machine;

        } catch (Exception e) {
            log.error("Erro ao processar página: {}", url, e);
            return machine;
        }
    }

    @Override
    public boolean supports(String url) {
        return url.contains("agrofy.com.br");
    }


    private Machine extractDetailElement(String htmlContent) {
        Machine machine = new Machine();

        try {
            // Parsear o HTML com JSoup
            Document doc = Jsoup.parse(htmlContent);
            Element ulElement = doc.select("ul[data-test=contentTable-list]").first();

            if (ulElement != null) {
                Elements liElements = ulElement.select("li.sc-knuQbY.hKUCOd");

                for (Element li : liElements) {
                    String label = li.select("span.sc-dNsVcS.gkuVrC").text();
                    String value = li.select("ul.sc-kMribo.jOTMiU > li.sc-bdOgaJ.fNDfoa").text();

                    // Mapear os valores para o objeto Machine
                    mapValueToMachine(machine, label, value);
                }
            }
        } catch (Exception e) {
            log.error("Erro ao extrair dados da div Agrofy: {}", e.getMessage());
        }

        return machine;
    }

    private String extractWorkHours(WebDriver driver){
        String workingHours = "";
        try{
            WebElement workingHoursElement = driver.findElement(By.cssSelector("div#Descrição"));
            String workingHoursHtml = workingHoursElement.getAttribute("outerHTML");

            // Parsear o HTML com JSoup
            Document hoursDoc = Jsoup.parse(workingHoursHtml);
            Elements paragraphs = hoursDoc.select("p");

            for (Element p : paragraphs) {
                String text = p.text();
                if (text.toUpperCase().contains("HORAS")) {
                    workingHours = text;
                    log.info("Horas de trabalho encontradas: {}", text);
                    break;
                }
            }
        }catch (NoSuchElementException e) {
            log.warn("Elemento de horas de trabalho não encontrado com seletor: {}", "div#Descrição");
        }
        return workingHours;
    }

    private String extractPrice(WebDriver driver) {
        String price = null;
        try {
            // Primeiro encontrar a div pai
            WebElement priceContainer = driver.findElement(
                    By.cssSelector("div.sc-fPXMVe.bdNJjR"));

            // Dentro da div pai, encontrar o span do preço
            WebElement priceElement = priceContainer.findElement(
                    By.cssSelector("span[data-test='prices-totalPrice']"));

            price = priceElement.getText();
            log.info("Preço encontrado: {}", price);

        }  catch (Exception e) {
            log.error("Erro ao extrair preço: {}", e.getMessage());
        }
        return price;
    }

    private String extractImage(WebDriver driver) {
        String img = "";
        try {
            // Tentar encontrar a imagem usando múltiplos seletores
            WebElement imageElement = driver.findElement(
                    By.cssSelector("div.sc-dAlyuH.qAvVH img, div.slide img, img[fetchpriority=high]"));

            String imageUrl = imageElement.getAttribute("src");
            img =imageUrl;
            log.info("Imagem encontrada: {}", imageUrl);

        } catch (NoSuchElementException e) {
            log.warn("Imagem não encontrada com seletores primários");

        }
        return img;
    }

    private void mapValueToMachine(Machine machine, String label, String value) {
        switch (label.toLowerCase()) {
            case "tipo de operação":
                machine.setContractType(value);
                break;
            case "marca":
                machine.setBrand(value);
                break;
            case "modelo":
            case "outro modelo":
                machine.setModel(value);
                break;
            case "ano de fabricação":
                try {
                    machine.setYear(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    log.warn("Ano inválido: {}", value);
                }
                break;
            case "cidade / estado":
                machine.setCity(value);
                break;
        }
    }


}