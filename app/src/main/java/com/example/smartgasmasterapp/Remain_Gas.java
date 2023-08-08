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

import org.json.JSONArray;
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
    public String order_Id,Company_id,result;
    public static String Customer_Id;
    public Button Scan_New_Gas,next;
    public TextView CompanyName;
    public int volume,total_volume;
    public EditText RemainInput;
    String[] data;
    public static ArrayList<String> remainGas;
    public static ArrayList<Integer> remainGasVolumnList;
    public ListView listView;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remain_gas);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Scan_New_Gas = findViewById(R.id.ScanNewGas);
        CompanyName = findViewById(R.id.gasCompanyName);
        //next = findViewById(R.id.next);
        //RemainInput = findViewById(R.id.RemainGasInput);

        OrderList orderList = new OrderList();
        order_Id = orderList.static_order_id;
        remainGas = new ArrayList<String>();
        remainGasVolumnList = new ArrayList<Integer>();
        listView = findViewById(R.id.Listview);

        Scan_New_Gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(remainGasVolumnList.size()>0){
                    for(int i=0;i<remainGasVolumnList.size();i++){
                        total_volume += remainGasVolumnList.get(i);
                    }
                }
                Log.i("volume", String.valueOf(total_volume));
                SaveVolume();


                // Start the ScanNewQRCode activity here
                Intent intent = new Intent(Remain_Gas.this, ScanNewQRCode.class);
                startActivity(intent);
            }
        });

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            GetData("http://10.0.2.2/SQL_Connect/Show_Company_Name.php", order_Id);
                            JSONObject responseJSON = new JSONObject(result);
                            String Company_Name = responseJSON.getString("Company_Name");
                            CompanyName.setText(Company_Name);
                            Customer_Id = responseJSON.getString("Customer_Id");
                            Log.i("Customer_Id",Customer_Id);
                            Company_id = responseJSON.getString("COMPANY_Id");

                            //獲取Sensor weight的重量
                            result = "";
                            GetData("http://10.0.2.2/SQL_Connect/Show_IOT.php",Customer_Id);
                            Log.i("remain gas result", result);
                            if(result.contains("Warning")){
                                Toast.makeText(Remain_Gas.this, "此客戶尚未註冊IOT", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                JSONArray ja = new JSONArray(result);
                                JSONObject jo = null;

                                for (int i = 0; i < ja.length(); i++) {
                                    jo = ja.getJSONObject(i);
                                    String sensorWeight = jo.getString("SENSOR_Weight");
                                    Log.i("sensorweight",sensorWeight);
                                    if (sensorWeight != null) {
                                        remainGasVolumnList.add(Integer.parseInt(sensorWeight));
                                        remainGas.add("感應器" + jo.getString("SENSOR_Id") + ": " + sensorWeight + "公斤");
                                    } else {
                                        remainGasVolumnList.add(0); // Or any other default value
                                        remainGas.add("感應器" + jo.getString("SENSOR_Id") + ": N/A");
                                    }
                                }
                                Log.i("SENSOR_Weight size", String.valueOf(remainGas.size()));
//                        for (int i = 0; i < ja.length(); i++) {
//                            jo = ja.getJSONObject(i);
//                            String sensorWeight = jo.getString("SENSOR_Weight");
//                            Log.i("sensorweight",sensorWeight);
//                            if (sensorWeight != null) {
//                                remainGasVolumnList.add(Integer.parseInt(sensorWeight));
//                                remainGas.add("感應器" + jo.getString("SENSOR_Id") + ": " + sensorWeight + "公斤");
//
//                            } else {
//                                remainGasVolumnList.add(0); // Or any other default value
//                                remainGas.add("感應器" + jo.getString("SENSOR_Id") + ": N/A");
//                            }
//                        }

//                                //if suceed, change page
//                                Intent intent = new Intent(Remain_Gas.this,ScanNewQRCode.class);
//                                startActivity(intent);

                        Log.i("SENSOR_Weight size", String.valueOf(remainGas.size()));

                                if (remainGas.size() > 0) {
                                    RemainGasAdapterList adapterList = new RemainGasAdapterList(getApplicationContext(), R.layout.adapter_remain_gas, remainGas);
                                    listView.setAdapter(adapterList);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        thread.start();

    }
    public void GetData(String geturl,String id){
        try{
            URL url = new URL(geturl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            result = "";
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            Log.i("result", "["+result+"]");
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
                    if (response.contains("success")) {
                        Intent intent = new Intent(Remain_Gas.this, ScanNewQRCode.class);
                        startActivity(intent);
                        Log.i("GasRemain", "Successfully store GasRemain.");
                        //next.setClickable(false);
                    } else if (response.contains("failure")) {
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
