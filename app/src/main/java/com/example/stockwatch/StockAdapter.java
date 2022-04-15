package com.example.stockwatch;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder>{
    private final ArrayList<Stock> stockList;
    private final MainActivity mainActivity;

    public StockAdapter(ArrayList<Stock> stockList, MainActivity mainActivity) {
        this.stockList = stockList;
        this.mainActivity = mainActivity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_entry,
                parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("0.00");
        Stock stock = stockList.get(position);
        double percent = Double.parseDouble(stock.getPercent()) * 100;
        String newPercent = String.valueOf(percent);
        String arrow = "▲";
        holder.symbol.setText(stock.getSymbol());
        holder.name.setText(stock.getName());
        holder.price.setText(stock.getPrice() );
        holder.change.setText(arrow + " " +stock.getChange()+ " (" + df.format(percent)+ "%)");
        if(percent<0.0) {
            arrow = "▼";
            holder.change.setText(arrow + " " +stock.getChange()+ " (" + df.format(percent) + "%)");
            holder.symbol.setTextColor(Color.parseColor("#FF0000"));
            holder.name.setTextColor(Color.parseColor("#FF0000"));
            holder.price.setTextColor(Color.parseColor("#FF0000"));
            holder.change.setTextColor(Color.parseColor("#FF0000"));
        }


    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
