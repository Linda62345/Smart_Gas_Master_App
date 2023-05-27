package com.example.smartgasmasterapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class Remain_Gas extends AppCompatActivity {
    public String order_Id,Customer_Id,Company_id;
    public Button Scan_New_Gas,next;
    public TextView CompanyName;
    public int volume,total_volume;
    public EditText RemainInput;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remain_gas);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Scan_New_Gas = findViewById(R.id.ScanNewGas);
        CompanyName = findViewById(R.id.gasCompanyName);
        next = findViewById(R.id.next);
        RemainInput = findViewById(R.id.RemainGasInput);

        OrderList orderList = new OrderList();
        order_Id = orderList.static_order_id;

        Scan_New_Gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!RemainInput.getText().toString().trim().isEmpty()){
                    volume = Integer.parseInt(RemainInput.getText().toString().trim());
                    RemainInput.setText("");
                }
                total_volume += volume;
                Log.i("volume", String.valueOf(total_volume));
                SaveVolume();
            }
        });
        total_volume = 0;
        if(!RemainInput.getText().toString().trim().isEmpty()){
            volume = Integer.parseInt(RemainInput.getText().toString().trim());

        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!RemainInput.getText().toString().trim().isEmpty()){
                    volume = Integer.parseInt(RemainInput.getText().toString().trim());
                    RemainInput.setText("");
                }
                total_volume += volume;
                Log.i("volume", String.valueOf(total_volume));
            }
        });

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    CompanyName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
    public void CompanyName(){
        try{
            String Showurl = "http://10.0.2.2/SQL_Connect/Show_Company_Name.php";
            URL url = new URL(Showurl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(order_Id, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            Log.i("result", "["+result+"]");
            JSONObject responseJSON = new JSONObject(result);
            String Company_Name = responseJSON.getString("Company_Name");
            CompanyName.setText(Company_Name);
            Customer_Id = responseJSON.getString("Customer_Id");
            Log.i("Customer_Id",Customer_Id);
            Company_id = responseJSON.getString("COMPANY_Id");
        }
        catch(Exception e){
            Log.i("Company Name Exception",e.toString());
        }
    }
    public void SaveVolume(){
        //customer, id
        //totalVolume
        //update + or insert
        try {
            String URL = "http://10.0.2.2/SQL_Connect/Save_RemainGas.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        Intent intent = new Intent(Remain_Gas.this, ScanNewQRCode.class);
                        startActivity(intent);
                        Log.i("GasRemain", "Successfully store GasRemain.");
                        next.setClickable(false);
                    } else if (response.equals("failure")) {
                        Log.i("GasRemain", "Something went wrong!");
                    }
                }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("id", Customer_Id);
                    data.put("gas", String.valueOf(total_volume));
                    data.put("company",Company_id);
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
        catch (Exception e){
            Log.i("S Gas Remain Exception", e.toString());
        }
    }

}
