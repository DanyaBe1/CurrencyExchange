package com.ed.currencyexchange.servlets;

import com.ed.currencyexchange.dbconnection.PoolConnectionBuilder;
import com.ed.currencyexchange.repositories.CurrencyRepositories;
import com.ed.currencyexchange.models.Currency;
import com.ed.currencyexchange.UTILS.UTILS;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.util.ArrayList;
@WebServlet(value = "/currencies")
public class CurrencyServlet extends HttpServlet {
    PoolConnectionBuilder cb = new PoolConnectionBuilder();
    CurrencyRepositories curRep = new CurrencyRepositories(cb);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ArrayList<Currency> currencies = curRep.getAllCurrencies();
        if (currencies.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка. База данных недоступна.");
        }
        Gson gson = new Gson();
        String json = gson.toJson(currencies);
        UTILS.responseConstructor(resp, req, json);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        if ((UTILS.isValidCode(code) == false)){
            resp.sendError(HttpServletResponse.SC_CONFLICT, "{\n" +
                    "    \"message\": \"Код валюты не соответствует стандарту\"\n" +
                    "}");
            return;
        }
        if (curRep.addCurrency(code, name, sign)) {
            Currency currency = curRep.getCurrency(code);
            Gson gson = new Gson();
            String json = gson.toJson(currency);
            UTILS.responseConstructor(resp, req, json);
        }
        else {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Валюта с таким кодом уже существует");
        }
}

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }
}
