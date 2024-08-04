package com.ed.currencyexchange.servlets;

import com.ed.currencyexchange.UTILS.UTILS;
import com.ed.currencyexchange.dbconnection.PoolConnectionBuilder;
import com.ed.currencyexchange.models.Currency;
import com.ed.currencyexchange.repositories.CurrencyRepositories;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/currency/*")
public class TargetCurrencyServlet extends HttpServlet {
    PoolConnectionBuilder cb = new PoolConnectionBuilder();
    final private CurrencyRepositories currencyRepositories = new CurrencyRepositories(cb);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String targetCurrency = req.getRequestURI().substring(req.getRequestURI().length() - 3, req.getRequestURI().length()).toUpperCase();
        Currency currency = currencyRepositories.getCurrency(targetCurrency);
        if (currency == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        Gson gson = new Gson();
        String json = gson.toJson(currency);
        UTILS.responseConstructor(resp, req, json);
    }
}
