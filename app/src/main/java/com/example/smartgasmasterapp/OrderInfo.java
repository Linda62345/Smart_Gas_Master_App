package com.example.smartgasmasterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.MalformedURLException;
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

public class OrderInfo extends AppCompatActivity {
    private Button startScan;
    private String order_Id, currentDateTimeString;
    private TextView name, phone, address, ordertime, gas_Quan;
    public static int gas_quantity;
    public String Customer_Id;
    String[] quantity, type, weight, Orig_gas_Id;
    ArrayList<orderDetail> orderdetail;
    public ListView Lorderdetail;
    public OrderList orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        startScan = findViewById(R.id.confirmOrder);

        orderList = new OrderList();

        //先判斷是否是從前往掃描頁面來的 是order_Id從那裏來 不是就今日訂單
        Bundle bundle;
        if ((bundle = getIntent().getExtras()) != null) {
            order_Id = bundle.getString("orderID");
        } else {
            OrderList orderList = new OrderList();
            order_Id = orderList.static_order_id;
        }

        name = findViewById(R.id.changable_customer_name);
        phone = findViewById(R.id.changable_contactNumber);
        address = findViewById(R.id.changable_deliveryAddress);
        ordertime = findViewById(R.id.ordertime);
        gas_Quan = findViewById(R.id.changable_gasQTY);

        try {
            showData();
        } catch (Exception e) {
            Log.i("Info Exception", e.toString());
        }
        //error
        //Original_Gas();
        Lorderdetail = (ListView) findViewById(R.id.list_item);
        try {
            Orderdetail();
        } catch (Exception e) {
            Log.i("Orderdetail Exception", e.toString());
        }

        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveNewGas();
                } catch (Exception e) {
                    Log.i("saveNewGas Exception", e.toString());
                }
                finish();
            }
        });
    }

    public void showData() throws MalformedURLException {
        try {
            String Showurl = "http://54.199.33.241/test/Show_Order_Info.php";
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
            Log.i("result", "[" + result + "]");
            JSONObject responseJSON = new JSONObject(result);
            String Order_name, Order_phone, Order_Address, Order_GasId, Order_GasQuan;
            Order_name = responseJSON.getString("Order_Name");
            name.setText(Order_name);
            Order_phone = responseJSON.getString("Order_Phone");
            phone.setText(Order_phone);
            Order_Address = responseJSON.getString("Order_Address");
            address.setText(Order_Address);
            Customer_Id = responseJSON.getString("Customer_Id");
            Log.i("Customer_Id", Customer_Id);
            Order_GasQuan = responseJSON.getString("Gas_Quantity");
            gas_Quan.setText(Order_GasQuan);
            gas_quantity = Integer.parseInt(Order_GasQuan);
            String OT = responseJSON.getString("Order_Time");
            ordertime.setText(OT);
        } catch (Exception e) {
            Log.i("Order Info Exception", e.toString());
        }
    }

    public void Orderdetail() throws MalformedURLException {
        try {
            String URL = "http://54.199.33.241/test/Worker_OrderDetail.php";
            URL url = new URL(URL);
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
            Log.i("orderdetail", "[" + result + "]");
            try {
                JSONArray ja = new JSONArray(result);
                JSONObject jo = null;
                quantity = new String[ja.length()];
                type = new String[ja.length()];
                weight = new String[ja.length()];
                orderdetail = new ArrayList<>();

//                orderDetail od = new orderDetail("數量","類別","規格");
//                orderdetail.add(od);
                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);
                    quantity[i] = jo.getString("Order_Quantity");
//                    type[i] = jo.getString("Order_type");
                    String orderType = jo.getString("Order_type");
                    if (orderType.equals("tradition")) {
                        type[i] = "傳統桶";
                    } else if (orderType.equals("composite")) {
                        type[i] = "複合桶";
                    } else {
                        type[i] = "N/A"; // In case of any other value, just use the original value
                    }
                    weight[i] = jo.getString("Order_weight");
                    orderDetail od = new orderDetail(quantity[i], type[i], weight[i]);
                    orderdetail.add(od);
                }
                orderDetailAdapterList adapter = new orderDetailAdapterList(this, R.layout.adapter_view_layout, orderdetail);
                if (orderdetail.size() > 0) {
                    Log.i("order detail", String.valueOf(orderdetail.size()));
                    Lorderdetail.setAdapter(adapter);
                    //每個從這裡開始點選
                    Lorderdetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //adapter.getItem(position);
                            Intent intent = new Intent(OrderInfo.this, ScanOriginalQRCode.class);
                            startActivity(intent);
                        }
                    });
                }
            } catch (Exception e) {
                Log.i("Orderdetail JSON Exception", e.toString());
            }
        } catch (Exception e) {
            Log.i("OrderDetail", e.toString());
        }
    }

    public void saveNewGas() {
        try {
            String URL = "http://54.199.33.241/test/Save_NewGasID.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("save_new_order",response);
                    if (response.contains("failure")) {
                        Toast.makeText(getApplicationContext(), "無法完成", Toast.LENGTH_SHORT).show();
                    } else if (response.contains("success")) {
                        saveSensorWeight();
                    }
                    else{
                        Log.i("save_new_order",response);
                        Toast.makeText(getApplicationContext(), "請確認連線或是聯絡公司", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("orderinfo saveGasOrder",error.toString().trim());
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("id", order_Id);

                    TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateFormat.setTimeZone(timeZone);

                    currentDateTimeString = dateFormat.format(new Date());

                    // Log the string to the console
                    Log.i("Date", currentDateTimeString);
                    data.put("time",currentDateTimeString);

                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.i("Save Exception", e.toString());
        }
    }

    public void saveSensorWeight() {
        try {
            String URL = "http://54.199.33.241/test/Save_OriginalWeight.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("saveSensorWeight",response);
                    if (response.contains("failure")) {
                        Toast.makeText(getApplicationContext(), "無法完成", Toast.LENGTH_SHORT).show();
                    } else if (response.contains("success")) {
                        Intent intent = new Intent(OrderInfo.this, Homepage.class);
                        startActivity(intent);
                        startScan.setClickable(false);
                    }
                    else if(response.contains("smaller")){
                        Toast.makeText(getApplicationContext(), "請先按iot更新鈕", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.i("saveSensorWeight",response);
                        Toast.makeText(getApplicationContext(), "請確認連線或是聯絡公司", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Error saveSensorWeight",error.toString().trim());
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();

                    for(int i = 0 ; i<orderList.sensor_Id_Array.size() ; i++){
                        data.put("id", orderList.sensor_Id_Array.get(i));
                    }

                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.i("saveSensorWeight Exception", e.toString());
        }
    }

}