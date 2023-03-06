package com.example.smartgasmasterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

public class OrderInfo extends AppCompatActivity {

    private Button startScan;
    private String order_Id;
    private TextView name,phone,address,gas_Id,gas_Quan;
    public static String Original_Gas_Id;
    public static int gas_quantity;
    public String Customer_Id;
    String line,result = "";
    String[] data,Orig_gas_Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        startScan = findViewById(R.id.confirmOrder);

        //先判斷是否是從前往掃描頁面來的 是order_Id從那裏來 不是就今日訂單
        Bundle bundle;
        if((bundle = getIntent().getExtras())!=null){
            order_Id = bundle.getString("orderID");
        }
        else{
            OrderList orderList = new OrderList();
            order_Id = orderList.static_order_id;
        }

        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //看有幾桶瓦斯 就換幾次
                Intent intent = new Intent(OrderInfo.this, ScanOriginalQRCode.class);
                startActivity(intent);
            }
        });

        name = findViewById(R.id.changable_customer_name);
        phone = findViewById(R.id.changable_contactNumber);
        address = findViewById(R.id.changable_deliveryAddress);
        gas_Id = findViewById(R.id.changable_gasID);
        gas_Quan = findViewById(R.id.changable_gasQTY);

        try {
            showData();
        } catch (Exception e) {
            Log.i("Info Exception",e.toString());
        }
        Original_Gas();

    }
    public void showData() throws MalformedURLException {
        try{
            String Showurl = "http://10.0.2.2/SQL_Connect/Show_Order_Info.php";
            URL url = new URL(Showurl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(order_Id), "UTF-8");
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
            String Order_name, Order_phone, Order_Address, Order_GasId, Order_GasQuan;
            Order_name = responseJSON.getString("Order_Name");
            name.setText(Order_name);
            Order_phone = responseJSON.getString("Order_Phone");
            phone.setText(Order_phone);
            Order_Address = responseJSON.getString("Order_Address");
            address.setText(Order_Address);
            Customer_Id = responseJSON.getString("Customer_Id");
            Log.i("Customer_Id",Customer_Id);
            /*if(responseJSON.getString("Gas_Id")==null){
                Order_GasId = responseJSON.getString("Gas_Id");
                //為了要傳給scanOriginalQRCode
                Original_Gas_Id = Order_GasId;
                gas_Id.setText("無");
            }
            else{
                gas_Id.setText(responseJSON.getString("Gas_Id"));
            }*/
            Order_GasQuan = responseJSON.getString("Gas_Quantity");
            gas_Quan.setText(Order_GasQuan);
            gas_quantity = Integer.parseInt(Order_GasQuan);
        } catch (Exception e) {
            Log.i("Order Info Exception", e.toString());
        }
    }
    /*public String getOriginal_Order_Id(){
        String O_gas_Id = Original_Gas_Id;
        return O_gas_Id;
    }*/

    public void Original_Gas(){
        try{
            String URL = "http://10.0.2.2/SQL_Connect/Customer_Original_Gas.php";
            URL url = new URL(URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(Customer_Id), "UTF-8");
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
            try{
                JSONArray ja = new JSONArray(result);
                JSONObject jo = null;

                Orig_gas_Id = new String[ja.length()];

                for(int i = 0; i<ja.length();i++){
                    jo = ja.getJSONObject(i);
                    Orig_gas_Id[i] = jo.getString("Gas_Id");
                    Log.i("Original_Gas",Orig_gas_Id[i]);
                }
            }catch(Exception e){
                Log.i("OriginalGas JSON Exception",e.toString());
            }
        } catch (Exception e) {
            Log.i("OriginalGas Exception", e.toString());
        }
    }

}