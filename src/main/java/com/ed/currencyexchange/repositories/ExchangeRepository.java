package com.ed.currencyexchange.repositories;

import com.ed.currencyexchange.dbconnection.PoolConnectionBuilder;
import com.ed.currencyexchange.models.Currency;
import com.ed.currencyexchange.models.Exchange;
import com.ed.currencyexchange.models.ExchangeRate;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRepository {
    private CurrencyRepositories curRep;

    private PoolConnectionBuilder cb;

    public ExchangeRepository(PoolConnectionBuilder cb, CurrencyRepositories curRep){
        this.cb = cb;
        this.curRep = curRep;
    }

    private Connection getConnection() throws SQLException {
        return cb.getConnection();
    }

    private ExchangeRate newExchangeRateModel(ResultSet rs) throws SQLException {
            if (rs.wasNull()){
                return ExchangeRate.nullRate();
            }else {
                Long id = rs.getLong("id");
                BigDecimal rate = BigDecimal.valueOf(rs.getDouble("rate"));
                Currency baseCur = curRep.getCurrency(rs.getLong("base_currency_id"));
                Currency targetCur = curRep.getCurrency(rs.getLong("target_currency_id"));
                return new ExchangeRate(id, baseCur, targetCur, rate);
            }

    }
    public ExchangeRate getExchangeRate(String code){
        String baseCurrencyCode = code.substring(0, 3);
        String targetCurrencyCode = code.substring(3, 6);
        final String query = "SELECT er.id, er.base_currency_id, er.target_currency_id, er.rate " +
                "FROM currencyexchange.exchange_rates er " +
                "JOIN currencyexchange.currencies base_cur ON er.base_currency_id = base_cur.id " +
                "JOIN currencyexchange.currencies target_cur ON er.target_currency_id = target_cur.id " +
                "WHERE base_cur.code = ? AND target_cur.code = ?";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, baseCurrencyCode);
            ps.setString(2, targetCurrencyCode);
            try (ResultSet res = ps.executeQuery()){
                if (res.next()) {
                    return newExchangeRateModel(res);
                }else{
                    return getReverseExchangeRate(targetCurrencyCode, baseCurrencyCode, connection);
                }
            }catch (SQLException e){
                e.printStackTrace();
                return null;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    private ExchangeRate getReverseExchangeRate(String baseCurrencyCode, String targetCurrencyCode, Connection connection){
        final String query = "SELECT er.id, er.base_currency_id, er.target_currency_id, er.rate " +
                "FROM currencyexchange.exchange_rates er " +
                "JOIN currencyexchange.currencies base_cur ON er.base_currency_id = base_cur.id " +
                "JOIN currencyexchange.currencies target_cur ON er.target_currency_id = target_cur.id " +
                "WHERE base_cur.code = ? AND target_cur.code = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, baseCurrencyCode);
            ps.setString(2, targetCurrencyCode);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return new ExchangeRate(res.getLong("id"), curRep.getCurrency(res.getLong("target_currency_id")),
                        curRep.getCurrency(res.getLong("base_currency_id")), (BigDecimal.valueOf(1 / res.getDouble("rate"))));
            }else {
                return ExchangeRate.nullRate();
            }
        }catch (SQLException e){
            e.printStackTrace();
            return ExchangeRate.nullRate();
        }
    }

    private ExchangeRate getCrossRate(String code){
        String baseCurrencyCode = code.substring(0, 3);
        String targetCurrencyCode = code.substring(3, 6);
        final String query = "SELECT er.rate " +
                "FROM currencyexchange.exchange_rates er " +
                "JOIN currencyexchange.currencies base_cur ON er.base_currency_id = base_cur.id " +
                "JOIN currencyexchange.currencies target_cur ON er.target_currency_id = target_cur.id " +
                "WHERE base_cur.code = 'USD' AND target_cur.code = ?";
        try {
            Connection connection = getConnection();
            PreparedStatement firstPS = connection.prepareStatement(query);
            PreparedStatement secondPS = connection.prepareStatement(query);
            firstPS.setString(1, baseCurrencyCode);
            secondPS.setString(1, targetCurrencyCode);
            try (ResultSet firstRes = firstPS.executeQuery();
                 ResultSet secondRes = secondPS.executeQuery()){
                boolean first = firstRes.next();
                boolean second = secondRes.next();
                return (first && second ? new ExchangeRate(0L, curRep.getCurrency(baseCurrencyCode), curRep.getCurrency(targetCurrencyCode), firstRes.getBigDecimal("rate").divide(secondRes.getBigDecimal("rate"))) : null);
            }
            catch (SQLException e){
                e.printStackTrace();
                return null;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return null;
        }

    }

    private boolean CheckBaseBeforeAddRate(String code, float rate) {
        String baseCurrencyCode = code.substring(0, 3);
        String targetCurrencyCode = code.substring(3, 6);
        final String checkQuery = "SELECT sign FROM currencyexchange.currencies WHERE code = ? OR code = ?";
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(checkQuery);
            ps.setString(1, baseCurrencyCode);
            ps.setString(2, targetCurrencyCode);
            try (ResultSet res = ps.executeQuery()){
                if (res.next()){
                    if (res.next()){
                        return true;
                    }
                }
            }
            catch (SQLException e){
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean AddExchangeRate(String baseCurrencyCode, String targetCurrencyCode, float rate){
        if (!CheckBaseBeforeAddRate(baseCurrencyCode+targetCurrencyCode, rate)){
            return false;
        }
        final String query = "INSERT INTO currencyexchange.exchange_rates (base_currency_id, target_currency_id, rate) VALUES (" +
                "(SELECT id from currencyexchange.currencies WHERE code = ?), " +
                "(SELECT id from currencyexchange.currencies WHERE code = ?), ?)";
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, baseCurrencyCode);
            ps.setString(2, targetCurrencyCode);
            ps.setFloat(3, rate);
            int res = ps.executeUpdate();
            if (res > 0){
                return true;
            }

        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public ArrayList<ExchangeRate> getAllExchangeRates(){
        ArrayList<ExchangeRate> list = new ArrayList<>();
        final String query = "SELECT * FROM currencyexchange.exchange_rates ORDER BY id";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.execute();

            ResultSet res = ps.getResultSet();
            while (res.next()) {
                list.add(newExchangeRateModel(res));
            }
            return list;
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    public boolean patchExchangeRate(String code, float rate){
        String baseCurrencyCode = code.substring(0, 3);
        String targetCurrencyCode = code.substring(3, 6);
        if (!CheckBaseBeforeAddRate(baseCurrencyCode+targetCurrencyCode, rate)){
            return false;
        }
        final String query = "UPDATE currencyexchange.exchange_rates SET rate = ? " +
                "WHERE base_currency_id = (SELECT id FROM currencyexchange.currencies WHERE code = ?) AND " +
                "target_currency_id = (SELECT id FROM currencyexchange.currencies WHERE code = ?)";
        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(query)){
            ps.setFloat(1, rate);
            ps.setString(2, baseCurrencyCode);
            ps.setString(3, targetCurrencyCode);
            int res = ps.executeUpdate();
            if (res > 0){
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

    public Exchange exchange (String from, String to, float amount){
        ExchangeRate exchangeRate = getExchangeRate(from+to);
        if (exchangeRate == null){
            return null;
        }
        BigDecimal convertedAmount = BigDecimal.valueOf(amount).multiply(exchangeRate.getRate());
        return new Exchange(curRep.getCurrency(from), curRep.getCurrency(to), exchangeRate.getRate(), BigDecimal.valueOf(amount), convertedAmount);
    }
}