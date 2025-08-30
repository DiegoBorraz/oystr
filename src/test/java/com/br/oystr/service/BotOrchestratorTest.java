package com.br.oystr.service;

import com.br.oystr.model.Machine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BotOrchestratorTest {

    @Mock
    private SiteSpecificBot agrofyBot;

    @Mock
    private SiteSpecificBot tractorsBot;

    @Mock
    private SiteSpecificBot marketBot;

    private BotOrchestrator botOrchestrator;

    @BeforeEach
    void setUp() {
        // ✅ SOLUÇÃO: Cria a lista manualmente e passa para o construtor
        List<SiteSpecificBot> bots = Arrays.asList(agrofyBot, tractorsBot, marketBot);
        botOrchestrator = new BotOrchestrator(bots);
    }

    @Test
    void shouldReturnMachineWhenAgrofyBotSupportsUrl() {
        // Arrange
        String agrofyUrl = "https://www.agrofy.com.br/trator-john-deere-7225j-ano-2013-205830.html";
        Machine expectedMachine = new Machine();
        expectedMachine.setModel("John Deere");

        when(agrofyBot.supports(agrofyUrl)).thenReturn(true);
        when(agrofyBot.fetch(agrofyUrl)).thenReturn(expectedMachine);

        // Act
        Machine result = botOrchestrator.fetch(agrofyUrl);

        // Assert
        assertNotNull(result);
        assertEquals("John Deere", result.getModel());
        verify(agrofyBot).supports(agrofyUrl);
        verify(agrofyBot).fetch(agrofyUrl);
    }

    @Test
    void shouldReturnEmptyMachineWhenNoBotSupportsUrl() {
        // Arrange
        String unknownUrl = "https://unknown-site.com";

        when(agrofyBot.supports(unknownUrl)).thenReturn(false);
        when(tractorsBot.supports(unknownUrl)).thenReturn(false);
        when(marketBot.supports(unknownUrl)).thenReturn(false);

        // Act
        Machine result = botOrchestrator.fetch(unknownUrl);

        // Assert
        assertNotNull(result);
        assertNull(result.getModel()); // Máquina vazia
    }

    @Test
    void shouldTryAllBotsUntilFindSupportedOne() {
        // Arrange
        String tractorsUrl = "https://tratoresecolheitadeiras.com.br/veiculo";
        Machine expectedMachine = new Machine();
        expectedMachine.setBrand("Case");

        when(agrofyBot.supports(tractorsUrl)).thenReturn(false);
        when(tractorsBot.supports(tractorsUrl)).thenReturn(true);
        when(tractorsBot.fetch(tractorsUrl)).thenReturn(expectedMachine);

        // Act
        Machine result = botOrchestrator.fetch(tractorsUrl);

        // Assert
        assertNotNull(result);
        assertEquals("Case", result.getBrand());
        verify(agrofyBot).supports(tractorsUrl);
        verify(tractorsBot).supports(tractorsUrl);
        verify(marketBot, never()).supports(anyString());
    }
}