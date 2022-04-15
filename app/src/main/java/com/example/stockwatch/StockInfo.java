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

import javax.net.ssl.HttpsURLConnection;

public class StockInfo implements Runnable{
    private MainActivity mainActivity;
    private static final String DATA_URL = "https://cloud.iexapis.com/stable/stock/";
    private String symbol;
    private String keyWord;
    private static final String yourAPIKey = "/quote?token=pk_ebdd3dab826940ddac2db224b377cc6e";

    public StockInfo(MainActivity mainActivity, String keyWord) {
        this.mainActivity = mainActivity;
        this.keyWord = keyWord;
    }


    @Override
    public void run() {
        String finalBoss = DATA_URL.concat(keyWord.trim()).concat(yourAPIKey);
        Uri.Builder buildURL = Uri.parse(finalBoss).buildUpon();
        String urlToUse = buildURL.build().toString();

        StringBuilder sb = new StringBuilder();


        try{
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
            handleResults(sb.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void handleResults(String s) throws JSONException {
        final Stock stock = parseJSON(s);
    }

    private Stock parseJSON(String s) throws JSONException {
        JSONObject jObjMain = new JSONObject(s);
        double change = jObjMain.getDouble("change");
        double changePercent = jObjMain.getDouble("changePercent");
        double latestprice = jObjMain.getDouble("latestPrice");
        System.out.println(change + " " + " " + changePercent + " " + latestprice);
        Stock stock = new Stock("", "", latestprice, change, changePercent);


        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.updateStock(stock);
            }
        });

        return stock;





    }
}
