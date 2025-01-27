package com.currencyConverter.currency_converter.dto.response;

import lombok.Getter;
import lombok.Setter;

public class CurrencyConversionResponseDTO {
    private String sourceCurrency;
    private String targetCurrency;
    private double originalAmount;
    private double convertedAmount;
    private String transactionId;

    public CurrencyConversionResponseDTO(String sourceCurrency, String targetCurrency, double originalAmount, double convertedAmount, String transactionId) {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.originalAmount = originalAmount;
        this.convertedAmount = convertedAmount;
        this.transactionId = transactionId;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(String sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}