package com.ed.currencyexchange.repositories;

import com.ed.currencyexchange.dbconnection.PoolConnectionBuilder;
import com.ed.currencyexchange.models.Currency;

import java.sql.*;
import java.util.ArrayList;

public class CurrencyRepositories {

    private PoolConnectionBuilder cb;

    public CurrencyRepositories(PoolConnectionBuilder cb){
        this.cb = cb;
    }

    private Connection getConnection() throws SQLException {
        return cb.getConnection();
    }


    private Currency newCurrencyModel(ResultSet rs){
        try{
            return new Currency(rs.getLong("id"),
                    rs.getString("code"),
                    rs.getString("fullname"),
                    rs.getString("sign"));
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Currency> getAllCurrencies() {
        Statement statement = null;
        ArrayList<Currency> cur = new ArrayList<>();
        try {
            Connection connection = getConnection();
            statement = connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM currencyexchange.currencies");
            while(res.next()){
                cur.add(newCurrencyModel(res));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cur;
    }

    public Currency getCurrency(String value){
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM currencyexchange.currencies WHERE code = '" + value + "'");
            if (!res.wasNull()) {
                while (res.next()) {
                    return newCurrencyModel(res);
                }
                connection.close();
            }
            else {
                connection.close();
                return null;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public Currency getCurrency(Long value){
        final String query = "SELECT * FROM currencyexchange.currencies WHERE id = " + value;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            ResultSet res = statement.executeQuery();
            if (res.next()){
                return newCurrencyModel(res);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public boolean addCurrency(String code, String fullName, String sign){
        String query = "INSERT INTO currencyexchange.currencies (code, fullname, sign) VALUES (?, ?, ?)";
        if (existCheck(code) == false){
            return false;
        }
        if (!code.equals("") & !fullName.equals("") & !sign.equals("")) {
            try (Connection connection = getConnection()) {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, code);
                ps.setString(2, fullName);
                ps.setString(3, sign);
                ps.executeUpdate();
                ps.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }else {
            return false;
        }
    }

    private boolean existCheck(String code){
        final String query = "SELECT * FROM currencyexchange.currencies WHERE code = ?";
        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, code);
            ResultSet res = ps.executeQuery();
            if (!res.next()){
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
