package com.currencyConverter.currency_converter.controller;

import com.currencyConverter.currency_converter.dto.ExchangeRateDTO;
import com.currencyConverter.currency_converter.dto.request.ConversionHistoryRequestDTO;
import com.currencyConverter.currency_converter.dto.request.CurrencyConversionRequestDTO;
import com.currencyConverter.currency_converter.dto.response.ConversionHistoryResponseDTO;
import com.currencyConverter.currency_converter.dto.response.CurrencyConversionResponseDTO;
import com.currencyConverter.currency_converter.dto.response.ExchangeRateResponse;
import com.currencyConverter.currency_converter.service.ExchangeRateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping
    public ExchangeRateResponse getExchangeRate(@RequestParam String fromCurrency,
                                                @RequestParam String toCurrency) {
        double rate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
        return new ExchangeRateResponse(fromCurrency, toCurrency, rate);
    }

    @PostMapping
    public String addExchangeRate(@RequestBody ExchangeRateDTO dto) {
        exchangeRateService.addExchangeRate(dto);
        return "Exchange rate added successfully.";
    }

    @PostMapping("/convert")
    public ResponseEntity<?> convertCurrency(@RequestBody CurrencyConversionRequestDTO request) {
        try {
            CurrencyConversionResponseDTO response = exchangeRateService.convertCurrency(
                    request.getSourceCurrency(),
                    request.getTargetCurrency(),
                    request.getAmount()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error converting currency: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public Page<ConversionHistoryResponseDTO> getConversionHistory(
            ConversionHistoryRequestDTO request, Pageable pageable) {
        return exchangeRateService.getConversionHistory(request, pageable);
    }
}