package com.currencyConverter.currency_converter.service;

import com.currencyConverter.currency_converter.dto.ExchangeRateDTO;
import com.currencyConverter.currency_converter.dto.request.ConversionHistoryRequestDTO;
import com.currencyConverter.currency_converter.dto.response.ConversionHistoryResponseDTO;
import com.currencyConverter.currency_converter.dto.response.CurrencyConversionResponseDTO;
import com.currencyConverter.currency_converter.entity.ExchangeRate;
import com.currencyConverter.currency_converter.entity.Transaction;
import com.currencyConverter.currency_converter.repository.ExchangeRateRepository;
import com.currencyConverter.currency_converter.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    private final ExternalExchangeRateClient externalExchangeRateClient;


    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, TransactionRepository transactionRepository, ExternalExchangeRateClient externalClient) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.transactionRepository = transactionRepository;
        this.externalExchangeRateClient = externalClient;
    }

    public void addExchangeRate(ExchangeRateDTO dto) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setFromCurrency(dto.getFromCurrency());
        exchangeRate.setToCurrency(dto.getToCurrency());
        exchangeRate.setRate(dto.getRate());
        exchangeRateRepository.save(exchangeRate);
    }

    public double getExchangeRate(String fromCurrency, String toCurrency) {
        return exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
                .map(ExchangeRate::getRate)
                .orElseGet(() -> {
                    double externalRate = getExternalExchangeRate(fromCurrency, toCurrency);
                    saveExternalExchangeRate(fromCurrency, toCurrency, externalRate); // Save the rate to the database
                    return externalRate;
                });
    }


    public Page<ConversionHistoryResponseDTO> getConversionHistory(ConversionHistoryRequestDTO request, Pageable pageable) {
        Page<Transaction> transactions;

        if (request.getTransactionId() != null) {
            transactions = transactionRepository.findByTransactionId(request.getTransactionId(), pageable);
        } else if (request.getStartDate() != null && request.getEndDate() != null) {
            LocalDateTime startDate = request.getStartDate().atStartOfDay();
            LocalDateTime endDate = request.getEndDate().atTime(23, 59, 59);
            transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate, pageable);
        } else {
            throw new IllegalArgumentException("Either transactionId or date range must be provided.");
        }

        return transactions.map(transaction -> {
            ConversionHistoryResponseDTO dto = new ConversionHistoryResponseDTO();
            dto.setTransactionId(transaction.getTransactionId());
            dto.setSourceCurrency(transaction.getSourceCurrency());
            dto.setTargetCurrency(transaction.getTargetCurrency());
            dto.setSourceAmount(transaction.getSourceAmount());
            dto.setConvertedAmount(transaction.getConvertedAmount());
            dto.setTransactionDate(transaction.getTransactionDate());
            return dto;
        });
    }

    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '-' + #toCurrency")
    public double getExternalExchangeRate(String fromCurrency, String toCurrency) {
        return externalExchangeRateClient.getExchangeRate(fromCurrency, toCurrency);
    }

    @Transactional
    public CurrencyConversionResponseDTO convertCurrency(String sourceCurrency, String targetCurrency, double amount) {
        double rate = getExternalExchangeRate(sourceCurrency, targetCurrency);
        double convertedAmount = amount * rate;

        Transaction transaction = new Transaction();
        transaction.setSourceCurrency(sourceCurrency);
        transaction.setTargetCurrency(targetCurrency);
        transaction.setSourceAmount(amount);
        transaction.setConvertedAmount(convertedAmount);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionId(UUID.randomUUID().toString());
        transactionRepository.save(transaction);

        return new CurrencyConversionResponseDTO(
                sourceCurrency,
                targetCurrency,
                amount,
                convertedAmount,
                transaction.getTransactionId()
        );
    }
    @Transactional
    public void saveExternalExchangeRate(String fromCurrency, String toCurrency, double rate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setFromCurrency(fromCurrency);
        exchangeRate.setToCurrency(toCurrency);
        exchangeRate.setRate(rate);
        exchangeRateRepository.save(exchangeRate);
    }
}