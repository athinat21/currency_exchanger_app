package com.currencyConverter.currency_converter.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


public class ConversionHistoryResponseDTO {
    private String transactionId;
    private String sourceCurrency;
    private String targetCurrency;
    private double sourceAmount;
    private double convertedAmount;
    private LocalDateTime transactionDate;

    public ConversionHistoryResponseDTO(String transactionId, String sourceCurrency,
                                        String targetCurrency, double sourceAmount,
                                        double convertedAmount, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.sourceAmount = sourceAmount;
        this.convertedAmount = convertedAmount;
        this.transactionDate = transactionDate;
    }

    public ConversionHistoryResponseDTO() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
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

    public double getSourceAmount() {
        return sourceAmount;
    }

    public void setSourceAmount(double sourceAmount) {
        this.sourceAmount = sourceAmount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}