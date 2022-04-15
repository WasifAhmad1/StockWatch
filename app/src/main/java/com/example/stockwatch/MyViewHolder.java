package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView symbol;
    TextView price;
    TextView change;
    TextView name;
     MyViewHolder(@NonNull View view) {
        super(view);
        symbol = view.findViewById(R.id.symbol);
        price = view.findViewById(R.id.price);
        change = view.findViewById(R.id.change);
        name = view.findViewById(R.id.stockName);
    }
}
