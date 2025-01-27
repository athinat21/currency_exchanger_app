package com.currencyConverter.currency_converter.service;

import com.currencyConverter.currency_converter.dto.response.ExternalApiResponse;
import com.currencyConverter.currency_converter.exception.CurrencyNotFoundException;
import com.currencyConverter.currency_converter.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ExternalExchangeRateClient {

    @Value("${external.api.base-url}")
    private String baseUrl;

    @Value("${external.api.access-key}")
    private String accessKey;

    private final RestTemplate restTemplate;

    @Autowired
    public ExternalExchangeRateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getExchangeRate(String fromCurrency, String toCurrency) {
        String url = String.format("%s/latest?access_key=%s&symbols=%s,%s",
                baseUrl, accessKey, fromCurrency, toCurrency);

        try {
            ResponseEntity<ExternalApiResponse> response = restTemplate.getForEntity(url, ExternalApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ExternalApiResponse apiResponse = response.getBody();

                if (apiResponse.getRates() != null) {
                    Map<String, Double> rates = apiResponse.getRates();
                    if (rates.containsKey(fromCurrency) && rates.containsKey(toCurrency)) {
                        double fromRate = rates.get(fromCurrency);
                        double toRate = rates.get(toCurrency);
                        return toRate / fromRate;
                    } else {
                        throw new CurrencyNotFoundException("Exchange rate not found for: " + fromCurrency + " or " + toCurrency);
                    }
                } else {
                    throw new ExternalApiException("External API response is missing the rates field.");
                }
            } else {
                throw new ExternalApiException("Failed to fetch exchange rate. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new ExternalApiException("External API call failed: " + e.getMessage());
        }
    }
}