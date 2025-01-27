package com.currencyConverter.currency_converter;

import com.currencyConverter.currency_converter.dto.ExchangeRateDTO;
import com.currencyConverter.currency_converter.dto.request.ConversionHistoryRequestDTO;
import com.currencyConverter.currency_converter.dto.response.ConversionHistoryResponseDTO;
import com.currencyConverter.currency_converter.dto.response.CurrencyConversionResponseDTO;
import com.currencyConverter.currency_converter.entity.ExchangeRate;
import com.currencyConverter.currency_converter.entity.Transaction;
import com.currencyConverter.currency_converter.repository.ExchangeRateRepository;
import com.currencyConverter.currency_converter.repository.TransactionRepository;
import com.currencyConverter.currency_converter.service.ExchangeRateService;
import com.currencyConverter.currency_converter.service.ExternalExchangeRateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ExternalExchangeRateClient externalExchangeRateClient;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddExchangeRate() {
        ExchangeRateDTO dto = new ExchangeRateDTO();
        dto.setFromCurrency("USD");
        dto.setToCurrency("EUR");
        dto.setRate(0.85);

        exchangeRateService.addExchangeRate(dto);

        ArgumentCaptor<ExchangeRate> captor = ArgumentCaptor.forClass(ExchangeRate.class);
        verify(exchangeRateRepository).save(captor.capture());

        ExchangeRate savedExchangeRate = captor.getValue();
        assertEquals("USD", savedExchangeRate.getFromCurrency());
        assertEquals("EUR", savedExchangeRate.getToCurrency());
        assertEquals(0.85, savedExchangeRate.getRate());
    }

    @Test
    void testGetExchangeRate_FromDatabase() {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setFromCurrency("USD");
        exchangeRate.setToCurrency("EUR");
        exchangeRate.setRate(0.85);

        when(exchangeRateRepository.findByFromCurrencyAndToCurrency("USD", "EUR"))
                .thenReturn(Optional.of(exchangeRate));

        double rate = exchangeRateService.getExchangeRate("USD", "EUR");

        assertEquals(0.85, rate);
        verify(exchangeRateRepository, times(1)).findByFromCurrencyAndToCurrency("USD", "EUR");
        verifyNoInteractions(externalExchangeRateClient);
    }

    @Test
    void testGetExchangeRate_FromExternalApi() {
        when(exchangeRateRepository.findByFromCurrencyAndToCurrency("USD", "EUR"))
                .thenReturn(Optional.empty());
        when(externalExchangeRateClient.getExchangeRate("USD", "EUR"))
                .thenReturn(0.85);

        double rate = exchangeRateService.getExchangeRate("USD", "EUR");

        assertEquals(0.85, rate);
        verify(exchangeRateRepository).findByFromCurrencyAndToCurrency("USD", "EUR");
        verify(externalExchangeRateClient).getExchangeRate("USD", "EUR");
        verify(exchangeRateRepository).save(any(ExchangeRate.class));
    }

    @Test
    void testConvertCurrency() {
        when(externalExchangeRateClient.getExchangeRate("USD", "EUR"))
                .thenReturn(0.85);

        CurrencyConversionResponseDTO response = exchangeRateService.convertCurrency("USD", "EUR", 100);

        assertEquals("USD", response.getSourceCurrency());
        assertEquals("EUR", response.getTargetCurrency());
        assertEquals(100, response.getOriginalAmount());
        assertEquals(85, response.getConvertedAmount(), 0.01);

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testGetConversionHistory() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("1234");
        transaction.setSourceCurrency("USD");
        transaction.setTargetCurrency("EUR");
        transaction.setSourceAmount(100);
        transaction.setConvertedAmount(85);
        transaction.setTransactionDate(LocalDateTime.now());

        List<Transaction> transactions = Collections.singletonList(transaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions);

        when(transactionRepository.findByTransactionDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(transactionPage);

        ConversionHistoryRequestDTO requestDTO = new ConversionHistoryRequestDTO();
        requestDTO.setStartDate(LocalDateTime.now().toLocalDate());
        requestDTO.setEndDate(LocalDateTime.now().toLocalDate());

        Page<ConversionHistoryResponseDTO> history = exchangeRateService.getConversionHistory(requestDTO, Pageable.unpaged());

        assertEquals(1, history.getContent().size());
        ConversionHistoryResponseDTO dto = history.getContent().get(0);
        assertEquals("1234", dto.getTransactionId());
        assertEquals("USD", dto.getSourceCurrency());
        assertEquals("EUR", dto.getTargetCurrency());
        assertEquals(100, dto.getSourceAmount());
        assertEquals(85, dto.getConvertedAmount());
    }
}