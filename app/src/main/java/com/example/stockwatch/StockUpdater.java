package com.example.stockwatch;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class StockUpdater implements Runnable {

    private MainActivity mainActivity;
    private ArrayList<Stock> stockList;
    private static final String DATA_URL = "https://cloud.iexapis.com/stable/stock/";
    private static final String yourAPIKey = "/quote?token=pk_ebdd3dab826940ddac2db224b377cc6e";

    public StockUpdater(MainActivity mainActivity, ArrayList<Stock> stockList) {
        this.mainActivity = mainActivity;
        this.stockList = stockList;
    }

    @Override
    public void run() {


        ArrayList<Stock> stockListCopy = new ArrayList<>();

        for(Stock stock: stockList) {
            System.out.println("In Stock Updater " + stock.getSymbol() + " " + stock.getName() + " " + stock.getPercent());
            String finalBoss = DATA_URL.concat(stock.getSymbol().trim()).concat(yourAPIKey);
            Uri.Builder buildURL = Uri.parse(finalBoss).buildUpon();
            String urlToUse = buildURL.build().toString();

            StringBuilder sb = new StringBuilder();

            try {
                URL url = new URL(urlToUse);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jObjMain = new JSONObject(sb.toString());
                double change = jObjMain.getDouble("change");
                double changePercent = jObjMain.getDouble("changePercent");
                double latestprice = jObjMain.getDouble("latestPrice");
                Stock stock2 = new Stock(stock.getSymbol(), stock.getName(), latestprice, change, changePercent);
                stockListCopy.add(stock2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.updateStockData(stockListCopy);
            }
        });

    }

    public void handleResults(String s, String symbol, String name) throws JSONException {
        final Stock stock = parseJSON(s, symbol, name);
    }

    private Stock parseJSON(String s, String symbol, String name) throws JSONException {
        ArrayList<Stock> stockListCopy = new ArrayList<>();

        JSONObject jObjMain = new JSONObject(s);
        double change = jObjMain.getDouble("change");
        double changePercent = jObjMain.getDouble("changePercent");
        double latestprice = jObjMain.getDouble("latestPrice");
        Stock stock = new Stock(symbol, name, latestprice, change, changePercent);
        stockListCopy.add(stock);



        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.updateStockData(stockListCopy);
            }
        });

        return stock;





    }
}



