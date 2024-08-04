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

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private PoolConnectionBuilder cb = new PoolConnectionBuilder();
    private CurrencyRepositories curRep = new CurrencyRepositories(cb);
    private ExchangeRepository er = new ExchangeRepository(cb, curRep);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")){
            doPatch(req, resp);
        }
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String currencyCode = requestURI.substring(requestURI.length() - 6, requestURI.length()).toUpperCase();
        if (!(UTILS.isValidCode(currencyCode.substring(0, 3))) || !(UTILS.isValidCode(currencyCode.substring(3, 6)))){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\n" +
                    "    \"message\": \"Код валюты не соответствует стандарту\"\n" +
                    "}");
        }
        ExchangeRate exchangeRate = er.getExchangeRate(currencyCode);
        Gson gson = new Gson();
        String json = gson.toJson(exchangeRate);
        UTILS.responseConstructor(resp, req, json);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String currencyCode = requestURI.substring(requestURI.length() - 6, requestURI.length()).toUpperCase();
        if (!(UTILS.isValidCode(currencyCode.substring(0, 3))) || !(UTILS.isValidCode(currencyCode.substring(3, 6)))){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\n" +
                    "    \"message\": \"Код валюты не соответствует стандарту\"\n" +
                    "}");
        }
        float rate = Float.parseFloat(UTILS.getRateForPatch(req));
        if (er.patchExchangeRate(currencyCode, rate)) {
            ExchangeRate exchangeRate = er.getExchangeRate(currencyCode);
            Gson gson = new Gson();
            String json = gson.toJson(exchangeRate);
            UTILS.responseConstructor(resp, req, json);
        }
        else{
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }


}
