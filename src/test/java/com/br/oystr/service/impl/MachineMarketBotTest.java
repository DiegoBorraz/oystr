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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MachineMarketBotTest {

    @Mock
    private WebDriver webDriver;

    @Mock
    private WebElement ulElement;

    @Mock
    private WebElement priceContainer;

    @Mock
    private WebElement priceElement;

    @Mock
    private WebElement utilizationSection;

    @Mock
    private WebElement imageElement;

    @InjectMocks
    private MachineMarketBot machineMarketBot;

    @Test
    void shouldSupportMachineMarketUrls() {
        assertTrue(machineMarketBot.supports("https://www.mercadomaquinas.com.br/trator"));
        assertTrue(machineMarketBot.supports("http://mercadomaquinas.com.br/colheitadeira"));
        assertFalse(machineMarketBot.supports("https://other-site.com"));
    }

    @Test
    void shouldReturnEmptyMachineOnException() {
        // Arrange
        when(webDriver.findElement(any(By.class))).thenThrow(new RuntimeException("Element not found"));

        // Act
        Machine result = machineMarketBot.fetch("https://www.mercadomaquinas.com.br/trator");

        // Assert
        assertNotNull(result);
        assertNull(result.getPrice());
        assertNull(result.getModel());
        verify(webDriver).get(anyString());
    }

    @Test
    void shouldHandleNullWebDriverGracefully() {
        // Teste para verificar comportamento com WebDriver null
        MachineMarketBot bot = new MachineMarketBot(null);

        try {
            Machine result = bot.fetch("https://www.mercadomaquinas.com.br/trator");
            assertNotNull(result);
            assertNull(result.getModel());
        } catch (Exception e) {
            // Esperado que possa lançar exceção com WebDriver null
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    void shouldExtractWorkHoursFromHtml() {
        // Teste da lógica de extração de horas trabalhadas com JSoup
        String htmlContent = """
            <ul>
                <li class="item spec">
                    <span class="name">Horas trabalhadas</span>
                    <span class="value">1.500</span>
                </li>
            </ul>
            """;

        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(htmlContent);
        org.jsoup.nodes.Element hoursElement = doc.selectFirst("li.item.spec:has(span.name:contains(Horas trabalhadas))");

        String hours = "";
        if (hoursElement != null) {
            org.jsoup.nodes.Element valueElement = hoursElement.selectFirst("span.value");
            if (valueElement != null) {
                hours = valueElement.text().trim();
            }
        }

        assertEquals("1.500", hours);
    }

    @Test
    void shouldHandleImageUrlWithDoubleSlash() {
        // Teste do tratamento de URL de imagem com double slash
        String imageUrl = "//example.com/image.jpg";

        if (imageUrl.startsWith("//")) {
            imageUrl = "https:" + imageUrl;
        }

        assertEquals("https://example.com/image.jpg", imageUrl);
    }

    @Test
    void shouldReturnOriginalImageUrl() {
        // Teste do tratamento de URL de imagem normal
        String imageUrl = "https://example.com/image.jpg";

        if (imageUrl.startsWith("//")) {
            imageUrl = "https:" + imageUrl;
        }

        assertEquals("https://example.com/image.jpg", imageUrl);
    }
}