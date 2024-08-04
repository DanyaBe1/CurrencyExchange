package com.ed.currencyexchange.servlets;

import com.ed.currencyexchange.UTILS.UTILS;
import com.ed.currencyexchange.dbconnection.PoolConnectionBuilder;
import com.ed.currencyexchange.models.ExchangeRate;
import com.ed.currencyexchange.repositories.CurrencyRepositories;
import com.ed.currencyexchange.repositories.ExchangeRepository;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    PoolConnectionBuilder cb = new PoolConnectionBuilder();
    CurrencyRepositories curRep = new CurrencyRepositories(cb);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ExchangeRepository er = new ExchangeRepository(cb, curRep);
        ArrayList<ExchangeRate> exchangeRates = er.getAllExchangeRates();
        if (exchangeRates.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(exchangeRates);
        UTILS.responseConstructor(resp, req, json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = (req.getParameter("baseCurrencyCode")).toUpperCase();
        String targetCurrencyCode = (req.getParameter("targetCurrencyCode")).toUpperCase();
        if (baseCurrencyCode.isEmpty() || targetCurrencyCode.isEmpty() || baseCurrencyCode.equals(targetCurrencyCode)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }
        float rate = Float.parseFloat(req.getParameter("rate"));
        ExchangeRepository er = new ExchangeRepository(cb, curRep);
        if (er.AddExchangeRate(baseCurrencyCode, targetCurrencyCode, rate)){
            resp.setStatus(HttpServletResponse.SC_CREATED);
            ExchangeRate exchangeRate = er.getExchangeRate(baseCurrencyCode+targetCurrencyCode);
            Gson gson = new Gson();
            String json = gson.toJson(exchangeRate);
            UTILS.responseConstructor(resp, req, json);
        }
        else {
            resp.sendError(HttpServletResponse.SC_CONFLICT);
        }
    }
}
