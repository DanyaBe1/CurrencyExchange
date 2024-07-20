package com.ed.currencyexchange.servlets;

import com.ed.currencyexchange.dbconnection.PoolConnectionBuilder;
import com.ed.currencyexchange.models.Exchange;
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

@WebServlet(value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private PoolConnectionBuilder cb = new PoolConnectionBuilder();
    private CurrencyRepositories curRep = new CurrencyRepositories(cb);
    private ExchangeRepository er = new ExchangeRepository(cb, curRep);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        float amount = Float.parseFloat((req.getParameter("amount")));
        Exchange exchange = er.exchange(from, to, amount);
        if (exchange == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            resp.setStatus(HttpServletResponse.SC_OK);
            Gson gson = new Gson();
            out.println(gson.toJson(exchange));
            out.flush();
        }
    }
}
