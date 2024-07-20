package com.ed.currencyexchange.models;

public class Currency {
    private Long id;
    private String name;
    private String code;
    private String sign;
    public Currency(Long id, String code, String name, String sign) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.sign = sign;
    }

    public String toString(){
        return String.format("{\n" +
                "\"id\": %d, \n" +
                "\"name\": \"%s\", \n" +
                "\"code\": \"%s\", \n" +
                "\"sign\": \"%s\" \n" +
                "}\n", id, code, name, sign);
    }
}
