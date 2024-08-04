package com.ed.currencyexchange.models;

import java.math.BigDecimal;

public class ExchangeRate {
    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;

    public ExchangeRate(Long id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getRate() {
        return rate;
    }

    public String toString(){
        return String.format("{ \n" +
                "\"id\": %d, \n" +
                "\"baseCurrency\": \"%s\", \n" +
                "\"targetCurrency\": \"%s\", \n" +
                "\"rate\": %f \n" +
                "}", id, baseCurrency, targetCurrency, rate);
    }
}
