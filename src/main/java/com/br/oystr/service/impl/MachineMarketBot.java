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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class MachineMarketBot extends BaseBot {

    @Autowired
    public MachineMarketBot(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public Machine fetch(String url) {
        log.info("Acessando página da Agrofy: {}", url);
        Machine machine = new Machine();
        WebDriver driver = this.webDriver;

        try{
            // 1. Acessar a URL
            driver.get(url);
            log.info("Página carregada: {}", driver.getTitle());
            // 2. Aguardar o carregamento dos elementos
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
            wait.pollingEvery(POLLING_INTERVAL);
            // 3. Extrair dados da ul
            WebElement ulElement = webDriver.findElement(By.cssSelector("ul.items"));
            String htmlContent = ulElement.getAttribute("outerHTML");
            machine = extractMachineFromUl(htmlContent);
            machine.setPrice(extractPrice(driver));
            machine.setWorkingHours(extractWorkHours(driver));
            machine.setImageUrl(extractImage(driver));
            machine.setUrl(url);
        } catch (Exception e) {
            log.error("Erro ao processar página: {}", url, e);
            return machine;
        }

        return machine;
    }

    @Override
    public boolean supports(String url) {
        return url.contains("mercadomaquinas.com.br");
    }

    private String extractPrice(WebDriver driver) {
        String price = null;
        try {
            // Primeiro encontrar a div pai
            WebElement priceContainer = webDriver.findElement(By.cssSelector("div.price"));
            String htmlContent = priceContainer.getAttribute("outerHTML");

            // Dentro da div pai, encontrar o span do preço
            WebElement priceElement = priceContainer.findElement(
                    By.cssSelector("span.value"));
            price = priceElement.getText();
            log.info("Preço encontrado: {}", price);

        }  catch (Exception e) {
            log.error("Erro ao extrair preço: {}", e.getMessage());
        }
        return price;
    }
    private String extractWorkHours(WebDriver driver) {
        try {
            // Procurar pela seção de Utilização
            WebElement utilizationSection = driver.findElement(By.xpath("//h5[contains(text(), 'Utilização')]/following-sibling::ul"));
            String htmlContent = utilizationSection.getAttribute("outerHTML");

            // Usar JSoup para parsear
            Document doc = Jsoup.parse(htmlContent);
            Element hoursElement = doc.selectFirst("li.item.spec:has(span.name:contains(Horas trabalhadas))");

            if (hoursElement != null) {
                Element valueElement = hoursElement.selectFirst("span.value");
                if (valueElement != null) {
                    String hours = valueElement.text().trim();
                    log.info("Horas trabalhadas encontradas: {}", hours);
                    return hours;
                }
            }

        }catch (Exception e) {
            log.warn("Erro ao extrair horas trabalhadas: {}", e.getMessage());
        }
        return "";
    }

    private String extractImage(WebDriver driver) {
        try {
            // Método 1: Encontrar a imagem dentro do div#ad-main-photo
            WebElement imageElement = driver.findElement(
                    By.cssSelector("div#ad-main-photo.photo img")
            );

            String imageUrl = imageElement.getAttribute("src");

            // Completar a URL se necessário (adicionar http: se não tiver)
            if (imageUrl.startsWith("//")) {
                imageUrl = "https:" + imageUrl;
            }

            log.info("URL da imagem encontrada: {}", imageUrl);
            return imageUrl;

        }  catch (Exception e) {
            log.warn("Erro ao extrair imagem: {}", e.getMessage());
            return "";
        }
    }

    private Machine extractMachineFromUl(String htmlContent) {
        Machine machine = new Machine();

        try {
            Document doc = Jsoup.parse(htmlContent);
            Elements listItems = doc.select("li.item");

            for (Element li : listItems) {
                // Extrair o span com o label (item-name)
                Element labelElement = li.selectFirst("span.item-name");
                Element valueElement = li.selectFirst("span.item-value");
                Element labelElement2 = li.selectFirst("span.label");

                if (labelElement != null) {
                    // Caso normal: item-name + item-value
                    String labelText = labelElement.text().replaceAll("[^a-zA-ZÀ-ÿ ]", "").trim().toLowerCase();
                    String value = "";

                    if (valueElement != null) {
                        value = valueElement.text().trim();
                    } else {
                        // Se não tem item-value, pode ser um link
                        Element linkElement = li.selectFirst("a");
                        if (linkElement != null) {
                            value = linkElement.text().trim();
                        }
                    }

                    mapValueToMachine(machine, labelText, value);

                } else if (labelElement2 != null) {
                    // Caso dos spans com apenas label (como "Venda")
                    String labelText = labelElement2.text().trim().toLowerCase();
                    mapValueToMachine(machine, labelText, labelText);
                }
            }

        } catch (Exception e) {
            log.error("Erro ao extrair dados da UL: {}", e.getMessage());
        }

        return machine;
    }



    private void mapValueToMachine(Machine machine, String label, String value) {
        if (value == null || value.isEmpty()) return;

        switch (label) {
            case "fabricante":
                machine.setBrand(value);
                break;
            case "modelo":
                machine.setModel(value);
                break;
            case "ano":
                try {
                    machine.setYear(Integer.parseInt(value.replaceAll("\\D", "")));
                } catch (NumberFormatException e) {
                    log.warn("Ano inválido: {}", value);
                }
                break;
            case "localização":
                machine.setCity(value);
                break;
            case "venda":
                machine.setContractType("Venda");
                break;
            default:
                log.debug("Label não mapeado: {} = {}", label, value);
        }
    }
}
