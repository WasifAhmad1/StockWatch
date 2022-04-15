package com.example.stockwatch;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class Stock implements Serializable
{
    private final String symbol;
    private final String name;
    private final double price;
    private final double change;
    private final double percent;


    public Stock(String symbol, String name, double price, double change, double percent) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change = change;
        this.percent = percent;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getPercent() {
        String s = String.valueOf(percent);
        return s;
    }

    public String getPrice() {
        String s = String.valueOf(price);
        return s;
    }

    public String getChange() {
        String s = String.valueOf(change);
        return s;
    }

    public String toString() {

        try {
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("symbol").value(getSymbol());
            jsonWriter.name("name").value(getName());
            jsonWriter.name("percent").value(getPercent());
            jsonWriter.name("price").value(getPrice());
            jsonWriter.name("change").value(getChange());
            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();



    } catch (
    IOException e) {
    e.printStackTrace();
}

        return "";
    }
}
