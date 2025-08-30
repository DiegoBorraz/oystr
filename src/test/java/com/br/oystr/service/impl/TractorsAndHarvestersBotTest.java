package com.br.oystr.service.impl;


import com.br.oystr.model.Machine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TractorsAndHarvestersBotTest {

    @Mock
    private WebDriver webDriver;

    @Mock
    private WebElement productDescriptionElement;

    @Mock
    private WebElement detailsElement;

    @Mock
    private WebElement descriptionElement;

    @Mock
    private WebElement zoomWindowElement;

    @Mock
    private WebElement imageElement;

    @Mock
    private WebElement columnElement;

    @InjectMocks
    private TractorsAndHarvestersBot tractorsAndHarvestersBot;

    @Test
    void shouldSupportTractorsAndHarvestersUrls() {
        assertTrue(tractorsAndHarvestersBot.supports("https://www.tratoresecolheitadeiras.com.br/trator"));
        assertTrue(tractorsAndHarvestersBot.supports("http://tratoresecolheitadeiras.com.br/colheitadeira"));
        assertFalse(tractorsAndHarvestersBot.supports("https://other-site.com"));
    }

    @Test
    void shouldReturnEmptyMachineOnException() {
        // Arrange
        when(webDriver.findElement(any(By.class))).thenThrow(new RuntimeException("Element not found"));

        // Act
        Machine result = tractorsAndHarvestersBot.fetch("https://www.tratoresecolheitadeiras.com.br/trator");

        // Assert
        assertNotNull(result);
        assertNull(result.getPrice());
        assertNull(result.getModel());
        verify(webDriver).get(anyString());
    }

    @Test
    void shouldHandleNullWebDriverGracefully() {
        // Teste para verificar comportamento com WebDriver null
        TractorsAndHarvestersBot botWithNullDriver = new TractorsAndHarvestersBot(null);
        Machine result = botWithNullDriver.fetch("https://www.tratoresecolheitadeiras.com.br/trator");

        assertNotNull(result);
        assertNull(result.getModel());
        // Não deve lançar exceção
    }

    @Test
    void shouldFindDetailsSectionSuccessfully() {
        // Arrange
        String htmlWithDetails = "<div>Detalhes do Veículo</div>";
        List<WebElement> columns = Arrays.asList(columnElement);

        when(webDriver.findElements(By.cssSelector("div.col-lg-4.col-md-4.col-sm-12.col-12")))
                .thenReturn(columns);
        when(columnElement.getAttribute("outerHTML")).thenReturn(htmlWithDetails);

        // Act - Usando reflection para testar método privado
        WebElement result = invokePrivateFindDetailsSection();

        // Assert
        assertNotNull(result);
        assertEquals(columnElement, result);
    }

    @Test
    void shouldReturnNullWhenDetailsSectionNotFound() {
        // Arrange
        String htmlWithoutDetails = "<div>Outra seção</div>";
        List<WebElement> columns = Arrays.asList(columnElement);

        when(webDriver.findElements(By.cssSelector("div.col-lg-4.col-md-4.col-sm-12.col-12")))
                .thenReturn(columns);
        when(columnElement.getAttribute("outerHTML")).thenReturn(htmlWithoutDetails);

        // Act
        WebElement result = invokePrivateFindDetailsSection();

        // Assert
        assertNull(result);
    }

    @Test
    void shouldExtractDetailElementSuccessfully() {
        // Arrange
        String htmlContent = """
            <div>
                <p><strong>Marca:</strong> New Holland</p>
                <p><strong>Modelo:</strong> T8.390</p>
                <p><strong>Ano de fabricação:</strong> 2021</p>
                <p><strong>Preço:</strong> R$ 450.000,00</p>
                <p><strong>Tipo:</strong> Venda</p>
            </div>
            """;

        // Act - Usando reflection para testar método privado
        Machine result = invokePrivateExtractDetailElement(htmlContent);

        // Assert
        assertNotNull(result);
        assertEquals("New Holland", result.getBrand());
        assertEquals("T8.390", result.getModel());
        assertEquals(2021, result.getYear());
        assertEquals("R$ 450.000,00", result.getPrice());
        assertEquals("Venda", result.getContractType());
    }

    @Test
    void shouldExtractLocationSuccessfully() {
        // Arrange
        String htmlContent = "<div>Localizado em TOLEDO/PR, excelente estado</div>";

        when(webDriver.findElement(By.cssSelector("div.product-single__description.rte")))
                .thenReturn(descriptionElement);
        when(descriptionElement.getAttribute("outerHTML")).thenReturn(htmlContent);

        // Act - Usando reflection para testar método privado
        String location = invokePrivateExtractLocation();

        // Assert
        assertEquals("TOLEDO/PR", location);
    }

    @Test
    void shouldReturnEmptyStringWhenLocationNotFound() {
        // Arrange
        String htmlContent = "<div>Sem informação de localização</div>";

        when(webDriver.findElement(By.cssSelector("div.product-single__description.rte")))
                .thenReturn(descriptionElement);
        when(descriptionElement.getAttribute("outerHTML")).thenReturn(htmlContent);

        // Act
        String location = invokePrivateExtractLocation();

        // Assert
        assertEquals("", location);
    }

    @Test
    void shouldExtractImageUrlFromZoomWindow() {
        // Arrange
        String styleContent = "background-image: url('https://d36qmzp7jiean8.cloudfront.net/image.jpg')";

        when(webDriver.findElement(By.cssSelector("div.zoomWindow"))).thenReturn(zoomWindowElement);
        when(zoomWindowElement.getAttribute("style")).thenReturn(styleContent);

        // Act - Usando reflection para testar método privado
        String imageUrl = invokePrivateExtractImageUrl();

        // Assert
        assertEquals("https://d36qmzp7jiean8.cloudfront.net/image.jpg", imageUrl);
    }

    @Test
    void shouldExtractImageUrlFallback() {
        // Arrange - Primeira tentativa falha, fallback funciona
        when(webDriver.findElement(By.cssSelector("div.zoomWindow")))
                .thenThrow(new RuntimeException("Zoom window not found"));
        when(webDriver.findElement(By.cssSelector("img[src*='cloudfront.net']")))
                .thenReturn(imageElement);
        when(imageElement.getAttribute("src")).thenReturn("https://cloudfront.net/fallback.jpg");

        // Act
        String imageUrl = invokePrivateExtractImageUrl();

        // Assert
        assertEquals("https://cloudfront.net/fallback.jpg", imageUrl);
    }

    @Test
    void shouldReturnEmptyStringWhenImageExtractionFails() {
        // Arrange - Ambas as tentativas falham
        when(webDriver.findElement(By.cssSelector("div.zoomWindow")))
                .thenThrow(new RuntimeException("Zoom window not found"));
        when(webDriver.findElement(By.cssSelector("img[src*='cloudfront.net']")))
                .thenThrow(new RuntimeException("Image not found"));

        // Act
        String imageUrl = invokePrivateExtractImageUrl();

        // Assert
        assertEquals("", imageUrl);
    }

    @Test
    void shouldMapValuesToMachineCorrectly() {
        // Arrange
        Machine machine = new Machine();

        // Act - Usando reflection para testar método privado
        invokePrivateMapValueToMachine(machine, "Marca", "John Deere");
        invokePrivateMapValueToMachine(machine, "Modelo", "7310");
        invokePrivateMapValueToMachine(machine, "Ano de fabricação", "2020");
        invokePrivateMapValueToMachine(machine, "Preço", "R$ 250.000,00");
        invokePrivateMapValueToMachine(machine, "Tipo", "Venda");
        invokePrivateMapValueToMachine(machine, "Cidade", "Sorriso/MT");

        // Assert
        assertEquals("John Deere", machine.getBrand());
        assertEquals("7310", machine.getModel());
        assertEquals(2020, machine.getYear());
        assertEquals("R$ 250.000,00", machine.getPrice());
        assertEquals("Venda", machine.getContractType());
        assertEquals("Sorriso/MT", machine.getCity());
    }

    @Test
    void shouldHandleInvalidYear() {
        // Arrange
        Machine machine = new Machine();

        // Act
        invokePrivateMapValueToMachine(machine, "Ano de fabricação", "inválido");

        // Assert
        assertNull(machine.getYear());
    }

    @Test
    void shouldHandleUnmappedLabel() {
        // Arrange
        Machine machine = new Machine();

        // Act - Label não mapeado
        invokePrivateMapValueToMachine(machine, "Label Desconhecido", "Valor");

        // Assert - Não deve alterar a máquina
        assertNull(machine.getBrand());
        assertNull(machine.getModel());
    }

    // Métodos auxiliares para acessar métodos privados via reflection
    private WebElement invokePrivateFindDetailsSection() {
        try {
            var method = TractorsAndHarvestersBot.class.getDeclaredMethod("findDetailsSection");
            method.setAccessible(true);
            return (WebElement) method.invoke(tractorsAndHarvestersBot);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke private method", e);
        }
    }

    private Machine invokePrivateExtractDetailElement(String htmlContent) {
        try {
            var method = TractorsAndHarvestersBot.class.getDeclaredMethod("extractDetailElement", String.class);
            method.setAccessible(true);
            return (Machine) method.invoke(tractorsAndHarvestersBot, htmlContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke private method", e);
        }
    }

    private String invokePrivateExtractLocation() {
        try {
            var method = TractorsAndHarvestersBot.class.getDeclaredMethod("extractLocation");
            method.setAccessible(true);
            return (String) method.invoke(tractorsAndHarvestersBot);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke private method", e);
        }
    }

    private String invokePrivateExtractImageUrl() {
        try {
            var method = TractorsAndHarvestersBot.class.getDeclaredMethod("extractImageUrl");
            method.setAccessible(true);
            return (String) method.invoke(tractorsAndHarvestersBot);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke private method", e);
        }
    }

    private void invokePrivateMapValueToMachine(Machine machine, String label, String value) {
        try {
            var method = TractorsAndHarvestersBot.class.getDeclaredMethod("mapValueToMachine",
                    Machine.class, String.class, String.class);
            method.setAccessible(true);
            method.invoke(tractorsAndHarvestersBot, machine, label, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke private method", e);
        }
    }
}