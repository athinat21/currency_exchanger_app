package com.currencyConverter.currency_converter.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    private int statusCode;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(int statusCode, String error, String message, String path) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.path = path;
    }

}