package com.example.stockwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView recyclerView;
    private ArrayList<Stock> stockList = new ArrayList<>();
    private StockAdapter stockAdapter;
    private String keyWord;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String finalKeyWord;
    private String company;
    private boolean isConnected;
    private String stockURL = "https://www.marketwatch.com/investing/stock/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "on create", Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.recycler);
        stockAdapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById((R.id.swiper));
        swipeRefreshLayout.setOnRefreshListener(this::doRefresh);


        //perform a network check when we first start the app
        isConnected = doNetCheck(isConnected);
        if (isConnected == false) {
            Builder builder = new Builder(this);
            builder.setTitle("You Are Not Connected to the Internet");
            builder.setMessage("Please connect if you want to use this app");
            builder.show();

        }


        try {
            stockList.addAll(loadStocks());
            //run the update stocks thread to get the latest data for stocks
            StockUpdater stockUpdater = new StockUpdater(this, stockList);
            new Thread(stockUpdater).start();
            //stockList.clear();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        /*for (int i = 0; i<5; i++) {
            Stock stock = new Stock("AAA", "Alcoholics", 35.40, 0.25, 1.24);
            stockList.add(stock);
        } */

        //generate code to load json file into json array?


    }

    public void updateStockData (ArrayList <Stock> listCopy) {

        //Toast.makeText(this, "in updateStocks", Toast.LENGTH_SHORT).show();

        stockList.clear();
        stockList.addAll(listCopy);
        //saveProduct();
    }

    private void doRefresh() {
        stockAdapter.notifyItemRangeChanged(0, stockList.size());
        StockUpdater stockUpdater = new StockUpdater(this, stockList);
        new Thread(stockUpdater).start();
        swipeRefreshLayout.setRefreshing(false);


    }

    @Override
    protected void onPause() {
        saveProduct();
        super.onPause();
    }

    private void saveProduct() {
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput("stocks.json", Context.MODE_PRIVATE);

            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(stockList);
            System.out.println(" In save product" + printWriter.toString());
            printWriter.close();
            fos.close();


            Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private ArrayList <Stock> loadStocks() throws IOException, JSONException {
        ArrayList<Stock> stockList2 = new ArrayList<>();



            InputStream is = getApplicationContext().openFileInput("stocks.json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());

        for(int i = 0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String symbol = jsonObject.getString("symbol");
            String name = jsonObject.getString("name");
            String percent = jsonObject.getString("percent");
            String price = jsonObject.getString("price");
            String change = jsonObject.getString("change");
            Stock stock = new Stock(symbol, name, Double.parseDouble(price), Double.parseDouble(change),
                    Double.parseDouble(percent));
            stockList2.add(stock);
        }

        return stockList2;



    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stockList.get(pos);
        String symbol = s.getSymbol();
        stockURL = stockURL + symbol;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(stockURL));
        stockURL = "https://www.marketwatch.com/investing/stock/";

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stocks_menu, menu);
        return true;
    }




    private boolean doNetCheck(boolean connect) {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected==true) {
            return true;

        } else{
            Toast.makeText(this, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        isConnected = doNetCheck(isConnected);

        if(id==R.id.add) {
            if(isConnected==true) {
                LayoutInflater inflater = LayoutInflater.from(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.stocks_dialog, null);
                builder.setView(view);
                TextView t1 = view.findViewById(R.id.stockSelect);
                TextView t2 = view.findViewById(R.id.enterStock);
                t1.setText("Stock Selection");
                t2.setText("Please enter a stock symbol");
                SymbolsNames symbolsNames = new SymbolsNames(this);
                EditText et1 = view.findViewById(R.id.textS);


                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //EditText et1 = view.findViewById(R.id.textS);
                        Toast.makeText(MainActivity.this, "You clicked yes", Toast.LENGTH_SHORT).show();
                        //put the code here to start the thread, not in onCreate
                        keyWord = String.valueOf(et1.getText());
                        new Thread(symbolsNames).start();


                    }
                });

                builder.setNegativeButton("NO WAY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "You selected no", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


            }
            else{
                Toast.makeText(this, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);

    }




    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock stock = stockList.get(pos);


        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Delete stock");
        builder2.setMessage("Do you want to delete the stock?");

        builder2.setPositiveButton("Yes", (dialog, id) -> {
            stockList.remove(stock);
            saveProduct();

            StockAdapter stockAdapter = new StockAdapter(stockList, this);
            recyclerView.setAdapter(stockAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        });

        builder2.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder2.show();



        return true;
    }

    public void updateData (HashMap map) {
        final CharSequence[] sArray = new CharSequence[map.size()];
        ArrayList<String> list = new ArrayList<>();
        int count = 0;
        for(int i = 0; i<sArray.length; i++) {
            String keyNew = keyWord.toUpperCase();
            sArray[i] = (CharSequence) map.get(i);
            if(sArray[i].toString().contains(keyNew)) {
                list.add(sArray[i].toString());
            }

        }

        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        System.out.println("The length of this list is" + cs.length);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(cs.length==0) {
            builder.setTitle("No Data Found: " + keyWord.toUpperCase());
            builder.setMessage("No data for stock symbol/name");
            AlertDialog dialog = builder.create();
            dialog.show();

        }


        //we will just parse the symbol and the name and add the entries to a listing
        //for now. Then we will figure out a way to search for specific words (needs to be a
        //sequence of characters)
        else {
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setTitle("Make a selection");

            String item = cs[which].toString();
            String [] split = item.split("-");
            runThread(split);
            finalKeyWord = split[0];
            company = split[1];
            Stock stock = new Stock(split[0], split[1], 0.0, 0.0, 0.0);
            /*stockList.add(stock);
            stockAdapter.notifyItemRangeChanged(0, stockList.size()); */
            }
        });

        builder.setNegativeButton("NO WAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You selected no", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show(); }

    }

    public void runThread (String [] symbolName) {
        //System.out.println("The boss is " + symbol);
        isConnected = doNetCheck(isConnected);
        if(isConnected==true) {
            StockInfo stockInfo = new StockInfo(this, symbolName[0]);
            new Thread(stockInfo).start();
        }

            else{

            Toast.makeText(this, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
        }

    }

    public void updateStock (Stock stock) {
        stock = new Stock(finalKeyWord, company, Double.parseDouble(stock.getPrice()),
                Double.parseDouble(stock.getChange()), Double.parseDouble(stock.getPercent()));
        //add if statements for situations when the values are null
        boolean check = checkStock(stock);
        if(check==true){
            stockList.add(stock);
        }

        else if(check!=true) {
            Toast.makeText(this, "This stock is already here!", Toast.LENGTH_SHORT).show();

        }

        stockAdapter.notifyItemRangeChanged(0, stockList.size());


    }

    public boolean checkStock (Stock stock) {
        for (Stock stock2 : stockList) {
            if (stock2.getSymbol().equals(stock.getSymbol())){
                return false;
            }
        }
        return true;
    }




}