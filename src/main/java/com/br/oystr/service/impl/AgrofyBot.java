package com.br.oystr.service.impl;


import com.br.oystr.model.Machine;
import com.br.oystr.service.BaseBot;
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
public class AgrofyBot extends BaseBot {
    private static final String MACHINE_CONTAINER = "ul[data-test=contentTable-list]";

    @Autowired
    public AgrofyBot(WebDriver webDriver) {
        super(webDriver);
    }


    @Override
    public Machine fetch(String url) {
        log.info("Acessando página da Agrofy: {}", url);
        Machine machine = new Machine();

        try {
            navigateToUrl(url);
            createWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(MACHINE_CONTAINER)));

            WebElement dataDiv = webDriver.findElement(By.cssSelector(MACHINE_CONTAINER));
            String htmlContent = dataDiv.getAttribute("outerHTML");

            machine = extractDetailElement(htmlContent);
            machine.setPrice(extractPrice());
            machine.setWorkingHours(extractWorkHours());
            machine.setImageUrl(extractImage());
            machine.setUrl(url);

        } catch (Exception e) {
            log.error("Erro ao processar página: {}", url, e);
        }
        return machine;
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
            Element ulElement = doc.select(MACHINE_CONTAINER).first();

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

    private String extractWorkHours(){
        String workingHours = "";
        try{
            WebElement workingHoursElement = webDriver.findElement(By.cssSelector("div#Descrição"));
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

    private String extractPrice() {
        String price = null;
        try {
            // Primeiro encontrar a div pai
            WebElement priceContainer = webDriver.findElement(
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

    private String extractImage() {
        String img = "";
        try {
            // Tentar encontrar a imagem usando múltiplos seletores
            WebElement imageElement = webDriver.findElement(
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