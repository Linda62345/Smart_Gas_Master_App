
package com.example.smartgasmasterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.sql.*;


public class ScanNewQRCode extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private Button next,next_gas;
    public String order_Id, currentDateTimeString;
    public EditText input_newGasId;
    private TextView GAS_ID, Initial_Volume, GAS_Type;
    public ArrayList<String> New_Gas_Id_Array;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_new_qrcode);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ScanOriginalQRCode scanOriginalQRCode = new ScanOriginalQRCode();
        OrderInfo orderInfo = new OrderInfo();

        next = findViewById(R.id.confirm_NewScan_button);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    New_Gas_Id_Array.add(input_newGasId.getText().toString());
                    saveNewGas();
                    try {
                        saveGasId(scanOriginalQRCode.Gas_Id_Array,New_Gas_Id_Array);
                    }
                    catch (Exception e){
                        Log.i("Exchange Gas",e.toString());
                    }
                    Intent intent = new Intent(ScanNewQRCode.this, Homepage.class);
                    startActivity(intent);
                }
                catch (Exception e){
                    Log.i("Save New Gas",e.toString());
                }
            }
        });

        CodeScannerView scannerView = findViewById(R.id.newScanner);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanNewQRCode.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        OrderList orderList = new OrderList();
        order_Id = orderList.static_order_id;

        input_newGasId = findViewById(R.id.mannuallyEnterNewGasCode);
        input_newGasId.addTextChangedListener(textWatcher);

        GAS_ID = findViewById(R.id.changeableNewID);
        Initial_Volume = findViewById(R.id.changeableNewVolume);
        GAS_Type = findViewById(R.id.GasTypeChoice);

        New_Gas_Id_Array = new ArrayList<String>();
        next_gas = findViewById(R.id.next_gas);
        next_gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_newGasId.removeTextChangedListener(textWatcher);
                input_newGasId.setText("");
                input_newGasId.addTextChangedListener(textWatcher);
            }
        });
    }
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        @Override
        public void afterTextChanged(Editable s) {
            //在這裡把gas detail的東西弄出來
            if (input_newGasId.hasFocus()) {
                // is only executed if the EditText was directly changed by the user
                GasData();
            }
        }};

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void GasData(){
        try{
            String Showurl = "http://10.0.2.2/SQL_Connect/Show_Gas_Info.php";
            URL url = new URL(Showurl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String gas_Id = input_newGasId.getText().toString().trim();
            String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(gas_Id), "UTF-8");
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
            Log.i("Gas_ID", "["+result+"]");
            JSONObject responseJSON = new JSONObject(result);
            if(responseJSON.getString("response").equals("failure")){
                Toast.makeText(this, "此瓦斯桶尚未註冊", Toast.LENGTH_SHORT).show();
            }
            else{
                String S_Gas_ID, S_initial_volume, S_Gas_Type;
                S_Gas_ID = responseJSON.getString("GAS_Id");
                GAS_ID.setText(S_Gas_ID);
                S_initial_volume = responseJSON.getString("GAS_Volume");
                Initial_Volume.setText(S_initial_volume);
                S_Gas_Type = responseJSON.getString("GAS_Type");
                GAS_Type.setText(S_Gas_Type);
                New_Gas_Id_Array.add(input_newGasId.getText().toString());
            }
        } catch (Exception e) {
            Log.i("Gas_Data Exception", e.toString());
        }
    }

    public void saveNewGas(){
        try {
            String URL = "http://10.0.2.2/SQL_Connect/Save_NewGasID.php";
            String New_Gas_Id;
            New_Gas_Id = input_newGasId.getText().toString().trim();
            if (New_Gas_Id_Array.size()==0) {
                Toast.makeText(this, "以上不可為空白", Toast.LENGTH_SHORT).show();
            } else {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            Log.i("saveNewGasId", "Successfully store New GasId.");
                            next.setClickable(false);
                        } else if (response.equals("failure")) {
                            Log.i("saveNewGasId", "Something went wrong!");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("Order_Id", String.valueOf(order_Id));
                        //因為這裡所以最後一個input不能為空
                        String gas_Id = input_newGasId.getText().toString().trim();
                        data.put("id", gas_Id);
                        // Create a DateFormat object and set the timezone to Taiwan
                        TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dateFormat.setTimeZone(timeZone);

                        // Format the current date and time as a string in the correct format
                        currentDateTimeString = dateFormat.format(new Date());

                        // Log the string to the console
                        Log.i("Date", currentDateTimeString);
                        data.put("time",currentDateTimeString);

                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        }
        catch (Exception e){
            Log.i("Save Exception", e.toString());
        }
    }
    public void saveGasId(ArrayList<String> OriginalID,ArrayList<String> NewID){
        //String orederID, String OriginalID,String newID,String GasQuan
        OrderInfo orderInfo = new OrderInfo();
        int i=0;
        for(i = 0; i<NewID.size(); i++){
            try {
                int y=i;
                String URL="http://10.0.2.2/SQL_Connect/Save_Array_NewGasID.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            Log.i("Gas Quantity",response.toString());

                        } else if (response.equals("failure")) {
                            Log.i("Gas quantity false",response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                        Log.i("gas_quantity error",error.toString().trim());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("id",order_Id);
                        if(OriginalID.size()>0&&y<OriginalID.size()){
                            if(OriginalID.get(y)!=null&&OriginalID.get(y)!=""){
                                data.put("OriginalID", OriginalID.get(y));
                            }
                            else{
                                data.put("OriginalID", String.valueOf(0));
                            }
                        }
                        else{
                            data.put("OriginalID", String.valueOf(0));
                        }
                        data.put("NewID", NewID.get(y));
                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
            catch (Exception e){
                Log.i("Save Exception", e.toString());
            }
        }
    }


}

