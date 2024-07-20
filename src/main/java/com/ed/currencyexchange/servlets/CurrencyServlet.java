package com.ed.currencyexchange.servlets;

import com.ed.currencyexchange.dbconnection.PoolConnectionBuilder;
import com.ed.currencyexchange.repositories.CurrencyRepositories;
import com.ed.currencyexchange.models.Currency;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.io.PrintWriter;
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
        PrintWriter writer = null;
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            Gson gson = new Gson();
            String json = gson.toJson(currencies);
            writer = resp.getWriter();
            writer.println(json);
            resp.setStatus(HttpServletResponse.SC_OK);
            writer.flush();
        } finally {
            writer.close();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        Gson gson = new Gson();
        if (curRep.addCurrency(code, name, sign)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            Currency currency = curRep.getCurrency(code);
            String json = gson.toJson(currency);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.print(json);
            writer.flush();
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
