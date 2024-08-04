package com.ed.currencyexchange.UTILS;

import com.ed.currencyexchange.models.Currency;
import com.ed.currencyexchange.models.ExchangeRate;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UTILS {

    public static void responseConstructor(HttpServletResponse resp, HttpServletRequest req, String json) throws IOException {
        PrintWriter writer = null;
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            writer = resp.getWriter();
            writer.println(json);
            resp.setStatus(HttpServletResponse.SC_OK);
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public static boolean isValidCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        if (code.length() != 3) {
            return false;
        }
        return true;
    }

    public static String getRateForPatch(HttpServletRequest req){
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String body = sb.toString();

        Map<String, String> params = parseParameters(body);
        return params.get("rate");
    }

    private static Map<String, String> parseParameters(String body) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
}
