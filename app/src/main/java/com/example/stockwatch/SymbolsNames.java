package com.example.stockwatch;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class SymbolsNames implements Runnable{
    private MainActivity mainActivity;
    private String keyWord;
    private boolean isConnected;
    private static final String DATA_URL = "https://financialmodelingprep.com/api/v3/available-traded/list?apikey=74e9af01059d6b85e472ea0b527096b1";

    public SymbolsNames(MainActivity mainActivity/*, String keyWord*/) {
        this.mainActivity = mainActivity;
        //this.keyWord = keyWord;
    }

    @Override
    public void run() {
        //connect to API and process json file here

        //mainActivity.runOnUiThread() -> mainActivity.
        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();
        //System.out.println("In here");

        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while((line=reader.readLine())!=null) {
                sb.append(line).append('\n');
            }

            HashMap<Integer, String> stockMap = new HashMap<>();
            stockMap = parseJson(sb.toString());

            HashMap<Integer, String> finalStockMap = stockMap;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.updateData(finalStockMap);
                }
            });




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private HashMap<Integer, String> parseJson(String s ) throws JSONException {
        String name;
        HashMap<Integer, String> stockMap = new HashMap<>();
        JSONArray jsonArray = new JSONArray(s);

        for(int i = 0; i<jsonArray.length(); i++) {
            JSONObject jStock = (JSONObject)jsonArray.get(i);
            String symbol = jStock.getString("symbol").toUpperCase();
            String stockName = jStock.getString("name").toUpperCase();
            String space = " - ";
            String concat = symbol.concat(space).concat(stockName);
            stockMap.put(i, concat);
        }

        return stockMap;
    }


}
