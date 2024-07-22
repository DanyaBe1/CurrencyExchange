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
        ExchangeRate exchangeRate = er.getExchangeRate(currencyCode);
        try (PrintWriter writer = resp.getWriter()){
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            Gson gson = new Gson();
            String json = gson.toJson(exchangeRate);
            writer.println(json);
            writer.flush();
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UTILS utils = new UTILS();
        String requestURI = req.getRequestURI();
        String currencyCode = requestURI.substring(requestURI.length() - 6, requestURI.length()).toUpperCase();

        float rate = Float.parseFloat(utils.getRateForPatch(req));
        if (er.patchExchangeRate(currencyCode, rate)) {
            ExchangeRate exchangeRate = er.getExchangeRate(currencyCode);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            Gson gson = new Gson();
            String json = gson.toJson(exchangeRate);
            resp.getWriter().println(json);
            resp.flushBuffer();
        }
        else{
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }


}
