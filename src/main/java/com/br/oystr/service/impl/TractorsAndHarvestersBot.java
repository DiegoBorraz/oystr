package com.br.oystr.service.impl;

import com.br.oystr.model.Machine;
import com.br.oystr.service.BaseBot;
import com.br.oystr.service.SiteSpecificBot;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class TractorsAndHarvestersBot extends BaseBot {

    // Seletores CSS simplificados
    private static final String MACHINE_CONTAINER = "div.product-description.rte";
    private static final String ALL_COLUMNS = "div.col-lg-4.col-md-4.col-sm-12.col-12";


    @Autowired
    public TractorsAndHarvestersBot(WebDriver webDriver)  {
        super(webDriver);
    }

    @Override
    public Machine fetch(String url) {
        log.info("Acessando página da tratoresecolheitadeiras: {}", url);
        Machine machine = new Machine();

        try {
            navigateToUrl(url);
            createWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(MACHINE_CONTAINER)));

            WebElement detailsSection = findDetailsSection();
            if (detailsSection != null) {
                String htmlContent = detailsSection.getAttribute("outerHTML");
                machine = extractDetailElement(htmlContent);
            }

            machine.setCity(extractLocation());
            machine.setImageUrl(extractImageUrl());
            machine.setUrl(url);

        } catch (Exception e) {
            log.error("Erro ao processar página: {}", url, e);
        }
        return machine;
    }

    @Override
    public boolean supports(String url) {
        return url.contains("tratoresecolheitadeiras.com.br");
    }

    private WebElement findDetailsSection() {
        try {
            // Encontrar todas as colunas
            List<WebElement> columns = webDriver.findElements(By.cssSelector(ALL_COLUMNS));

            for (WebElement column : columns) {
                String html = column.getAttribute("outerHTML");
                // Verificar se é a seção de detalhes procurando pelo texto
                if (html.contains("Detalhes do Veículo") || html.contains("Detalhes")) {
                    return column;
                }
            }
        } catch (Exception e) {
            log.warn("Seção de detalhes não encontrada: {}", e.getMessage());
        }
        return null;
    }


    private Machine extractDetailElement(String htmlContent) {
        Machine machine = new Machine();

        try {
            Document doc = Jsoup.parse(htmlContent);
            // Selecionar todos os parágrafos dentro da seção
            Elements paragraphElements = doc.select("p");
            for (Element p : paragraphElements) {
                String text = p.text().trim();

                // Verificar se é um campo no formato "Label: Value"
                if (text.contains(":")) {
                    String[] parts = text.split(":", 2);
                    String label = parts[0].trim();
                    String value = parts.length > 1 ? parts[1].trim() : "";

                    // Remover tags strong se existirem
                    value = value.replaceAll("<strong>|</strong>", "").trim();

                    // Mapear os valores para o objeto Machine
                    mapValueToMachine(machine, label, value);
                }
            }
        } catch (Exception e) {
            log.error("Erro ao extrair dados da seção de detalhes: {}", e.getMessage());
        }

        return machine;
    }

    private String extractLocation() {
        try {
            WebElement descriptionElement = webDriver.findElement(By.cssSelector("div.product-single__description.rte"));
            String htmlContent = descriptionElement.getAttribute("outerHTML");

            Document doc = Jsoup.parse(htmlContent);

            // Procurar pelo padrão cidade/estado (ex: TOLEDO/PR)
            String regex = "[A-ZÀ-Ú]{2,}/[A-Z]{2}";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(htmlContent);

            if (matcher.find()) {
                String location = matcher.group();
                log.info("Localização encontrada: {}", location);
                return location;
            }

        } catch (Exception e) {
            log.warn("Localização não encontrada: {}", e.getMessage());
        }
        return "";
    }

    private String extractImageUrl() {
        try {
            // Esperar que o elemento zoomWindow esteja presente
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
            WebElement zoomWindow = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.zoomWindow")));

            String style = zoomWindow.getAttribute("style");
            log.debug("Style do zoomWindow: {}", style);

            // Extrair a URL usando regex para maior robustez
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "background-image:\\s*url\\([\"']?(https://d36qmzp7jiean8\\.cloudfront\\.net/[^\"']+)[\"']?\\)");

            java.util.regex.Matcher matcher = pattern.matcher(style);

            if (matcher.find()) {
                String imageUrl = matcher.group(1);
                log.info("Imagem principal encontrada: {}", imageUrl);
                return imageUrl;
            }

        } catch (Exception e) {
            log.warn("Não foi possível extrair imagem do zoomWindow: {}", e.getMessage());

            // Fallback: procurar qualquer imagem cloudfront
            try {
                WebElement image = webDriver.findElement(By.cssSelector("img[src*='cloudfront.net']"));
                return image.getAttribute("src");
            } catch (Exception ex) {
                log.warn("Imagem alternativa não encontrada");
            }
        }

        return "";
    }



    private void mapValueToMachine(Machine machine, String label, String value) {
        String normalizedLabel = label.toLowerCase();

        switch (normalizedLabel) {
            case "preço":
                machine.setPrice(value);
                break;
            case "tipo":
                machine.setContractType(value);
                break;
            case "marca":
                machine.setBrand(value);
                break;
            case "modelo":
                machine.setModel(value);
                break;
            case "ano de fabricação":
                try {
                    machine.setYear(Integer.parseInt(value.replaceAll("\\D", "")));
                } catch (NumberFormatException e) {
                    log.warn("Ano de fabricação inválido: {}", value);
                }
                break;
            case "cidade":
            case "cidade / estado":
                machine.setCity(value);
            default:
                log.debug("Label não mapeado: {} = {}", label, value);
        }
    }

    private void closeDriverSafely() {
        try {
            if (webDriver != null) {
                webDriver.quit();
            }
        } catch (Exception e) {
            log.warn("Erro ao fechar WebDriver: {}", e.getMessage());
        }
    }
}