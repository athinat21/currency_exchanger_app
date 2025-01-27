package com.currencyConverter.currency_converter.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExchangeRateResponse {
    private String fromCurrency;
    private String toCurrency;
    private double exchangeRate;

    public ExchangeRateResponse(String fromCurrency, String toCurrency, double exchangeRate) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
    }

}
