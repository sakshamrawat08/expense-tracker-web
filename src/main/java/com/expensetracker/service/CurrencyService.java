package com.expensetracker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {

    private static final Map<String, String> CURRENCY_SYMBOLS = new HashMap<>();

    static {
        CURRENCY_SYMBOLS.put("INR", "₹");
        CURRENCY_SYMBOLS.put("USD", "$");
        CURRENCY_SYMBOLS.put("EUR", "€");
        CURRENCY_SYMBOLS.put("GBP", "£");
        CURRENCY_SYMBOLS.put("JPY", "¥");
        CURRENCY_SYMBOLS.put("AUD", "A$");
        CURRENCY_SYMBOLS.put("CAD", "C$");
        CURRENCY_SYMBOLS.put("SGD", "S$");
        CURRENCY_SYMBOLS.put("AED", "AED");
    }

    // Static fallback rates (in case API is down)
    private static final Map<String, Double> FALLBACK_RATES = new HashMap<>();

    static {
        FALLBACK_RATES.put("INR", 1.0);
        FALLBACK_RATES.put("USD", 0.012);
        FALLBACK_RATES.put("EUR", 0.011);
        FALLBACK_RATES.put("GBP", 0.0094);
        FALLBACK_RATES.put("JPY", 1.79);
        FALLBACK_RATES.put("AUD", 0.018);
        FALLBACK_RATES.put("CAD", 0.016);
        FALLBACK_RATES.put("SGD", 0.016);
        FALLBACK_RATES.put("AED", 0.044);
    }

    public double convert(double amountInINR, String toCurrency) {
        if (toCurrency == null || toCurrency.equals("INR")) {
            return amountInINR;
        }
        Double rate = FALLBACK_RATES.getOrDefault(toCurrency, 1.0);
        return amountInINR * rate;
    }

    public String getSymbol(String currency) {
        return CURRENCY_SYMBOLS.getOrDefault(
                currency != null ? currency : "INR", "Rs.");
    }

    public Map<String, String> getAllCurrencies() {
        return CURRENCY_SYMBOLS;
    }
}