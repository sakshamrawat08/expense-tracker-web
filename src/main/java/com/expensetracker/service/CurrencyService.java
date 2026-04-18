package com.expensetracker.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CurrencyService {

    private static final Map<String, String> CURRENCY_SYMBOLS
            = new LinkedHashMap<>();

    static {
        CURRENCY_SYMBOLS.put("INR", "Rs.");
        CURRENCY_SYMBOLS.put("USD", "$");
        CURRENCY_SYMBOLS.put("EUR", "€");
        CURRENCY_SYMBOLS.put("GBP", "£");
        CURRENCY_SYMBOLS.put("JPY", "¥");
        CURRENCY_SYMBOLS.put("AUD", "A$");
        CURRENCY_SYMBOLS.put("CAD", "C$");
        CURRENCY_SYMBOLS.put("SGD", "S$");
        CURRENCY_SYMBOLS.put("AED", "AED");
    }

    private static final Map<String, Double> RATES
            = new LinkedHashMap<>();

    static {
        RATES.put("INR", 1.0);
        RATES.put("USD", 0.012);
        RATES.put("EUR", 0.011);
        RATES.put("GBP", 0.0094);
        RATES.put("JPY", 1.79);
        RATES.put("AUD", 0.018);
        RATES.put("CAD", 0.016);
        RATES.put("SGD", 0.016);
        RATES.put("AED", 0.044);
    }

    public double convert(double amountInINR, String toCurrency) {
        if (toCurrency == null || toCurrency.equals("INR")) {
            return amountInINR;
        }
        return amountInINR * RATES.getOrDefault(toCurrency, 1.0);
    }

    public String getSymbol(String currency) {
        return CURRENCY_SYMBOLS.getOrDefault(
                currency != null ? currency : "INR", "Rs.");
    }

    public Map<String, String> getAllCurrencies() {
        return CURRENCY_SYMBOLS;
    }
}