package com.br.oystr.service.impl;

import com.br.oystr.model.Machine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgrofyBotTest {

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private AgrofyBot agrofyBot;

    @Test
    void shouldSupportAgrofyUrls() {
        assertTrue(agrofyBot.supports("https://www.agrofy.com.br/trator"));
        assertTrue(agrofyBot.supports("http://agrofy.com.br/trator"));
        assertFalse(agrofyBot.supports("https://other-site.com"));
    }

    @Test
    void shouldReturnEmptyMachineOnException() {
        // Arrange
        when(webDriver.findElement(any(By.class))).thenThrow(new RuntimeException("Element not found"));

        // Act
        Machine result = agrofyBot.fetch("https://www.agrofy.com.br/trator");

        // Assert
        assertNotNull(result); // Máquina vazia mas não nula
        assertNull(result.getPrice()); // Campos devem ser null
        assertNull(result.getModel());
        verify(webDriver).get(anyString());
    }

    @Test
    void shouldHandleNullWebDriverGracefully() {
        // Teste para verificar comportamento com WebDriver null
        AgrofyBot botWithNullDriver = new AgrofyBot(null);
        Machine result = botWithNullDriver.fetch("https://www.agrofy.com.br/trator");

        assertNotNull(result);
        assertNull(result.getModel());
    }
}