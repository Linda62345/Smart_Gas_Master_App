
package com.example.smartgasmasterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Size;
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
import com.google.common.util.concurrent.ListenableFuture;

import android.Manifest;

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
import java.util.concurrent.ExecutionException;


public class ScanNewQRCode extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Button next,next_gas;
    public String order_Id, currentDateTimeString;
    public EditText input_newGasId;
    private TextView GAS_ID, Initial_Volume, GAS_Type;
    public ArrayList<String> New_Gas_Id_Array;
    public String Customer_Id,qrCode;
    public static String condition,S_condition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_new_qrcode);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ScanOriginalQRCode scanOriginalQRCode = new ScanOriginalQRCode();
        OrderInfo orderInfo = new OrderInfo();

        next = findViewById(R.id.confirm_NewScan_button);
        previewView = findViewById(R.id.newScanner);

        OrderList orderList = new OrderList();
        order_Id = orderList.static_order_id;

        input_newGasId = findViewById(R.id.mannuallyEnterGasCode);

        GAS_ID = findViewById(R.id.changeableNewID);
        Initial_Volume = findViewById(R.id.changeableNewVolume);
        GAS_Type = findViewById(R.id.GasTypeChoice);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Save Gas Order資料
                saveNewGas();
                //Save 換桶資料: Customer_Gas
                try {
                    saveCustomerGasId(scanOriginalQRCode.Gas_Id_Array,New_Gas_Id_Array);
                }
                catch (Exception e){
                    Log.i("Exchange Gas",e.toString());
                }
                // Finish the current activity to prevent the user from navigating back to it
                finish();
            }
        });

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

        input_newGasId.addTextChangedListener(textWatcher);

        // Call GasData() method once initially
        GasData();

        Remain_Gas remain_gas = new Remain_Gas();
        Customer_Id = remain_gas.Customer_Id;

        //scanner
        next.setVisibility(View.VISIBLE);

//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                saveIOT();
//                // enterNewIot.setText(""); // Clear the EditText
////                enterNewIot.setText(qrCode);
////                Toast.makeText(getApplicationContext(), qrCode, Toast.LENGTH_SHORT).show();
////                Log.i(ScanReceiptQRCode.class.getSimpleName(), "QR Code Found: " + qrCode);
//            }
//        });

        //ShowDataDetail();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        requestCamera();

    }
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                GasData();
            }
        }
        @Override
        public void afterTextChanged(Editable s) {
            //在這裡把gas detail的東西弄出來
//            if (input_newGasId.hasFocus()) {
//                // is only executed if the EditText was directly changed by the user
//                GasData();
//            }
        }};



    public void GasData(){
        try {
            String gas_Id1 = input_newGasId.getText().toString().trim();
            if (TextUtils.isEmpty(gas_Id1)) {
                // Clear the TextViews if the input is empty
                GAS_ID.setText("");
                Initial_Volume.setText("");
                GAS_Type.setText("");
            } else {
                String Showurl = "http://54.199.33.241/test/Show_Gas_Info.php";
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
                Log.i("Gas_ID", "[" + result + "]");
                JSONObject responseJSON = new JSONObject(result);
                if (responseJSON.getString("response").contains("failure")) {
                    Toast.makeText(this, "此瓦斯桶尚未註冊", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ScanNewQRCode.this,GasRegister.class);

                    startActivity(intent);
                } else {
                    String S_Gas_ID, S_initial_volume, S_Gas_Type;
                    S_Gas_ID = responseJSON.getString("GAS_Id");
                    GAS_ID.setText(S_Gas_ID);
                    S_initial_volume = responseJSON.getString("GAS_Volume");
                    Initial_Volume.setText(S_initial_volume);
                    S_Gas_Type = responseJSON.getString("GAS_Type");
                    //GAS_Type.setText(S_Gas_Type);
                    if ("0".equals(S_Gas_Type)) {
                        GAS_Type.setText("傳統鋼瓶");
                    } else {
                        GAS_Type.setText("複合材料");
                    }



                    if (input_newGasId.getText().toString() != null && input_newGasId.getText().toString() != "") {
                        New_Gas_Id_Array.add(input_newGasId.getText().toString());
                    }
                }
            }
            } catch(Exception e){
                Log.i("Gas_Data Exception", e.toString());
            }
    }

    public void saveNewGas(){
        try {
            String URL = "http://54.199.33.241/test/Save_NewGasID.php";
            String New_Gas_Id;
            New_Gas_Id = input_newGasId.getText().toString().trim();
            if (New_Gas_Id_Array.size()==0) {
                Toast.makeText(this, "以上不可為空白", Toast.LENGTH_SHORT).show();
            } else {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("success")) {
                            Log.i("saveNewGasId", "Successfully store New GasId.");
                            next.setClickable(false);
                        } else if (response.contains("failure")) {
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

    public interface ActionCallback {
        void onSuccess(String condition);
        void onError(String error);
    }

    public void action(String sql, final ActionCallback callback) {
        condition = ""; // Initialize condition

        String url = "http://54.199.33.241/test/Save_Array_GasID.php";
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.contains("success")) {
                        condition = "success";
                        callback.onSuccess(condition);
                        Log.i("action response", response.toString());
                    } else {
                        condition = "failure";
                        callback.onSuccess(condition);
                        Log.i("action response", response.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onError(error.toString().trim());
                    Log.i("gas_quantity error", error.toString().trim());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("sql", sql);
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.i("換桶customer gas error", e.toString());
            callback.onError(e.toString());
        }

        // No need to return condition
    }

    public void saveCustomerGasId(ArrayList<String> OriginalID, ArrayList<String> NewID) {
        S_condition = "failure";

        try {
            Log.i("NewID.size()", String.valueOf(NewID.size()));
            Log.i("OriginalID.size()", String.valueOf(OriginalID.size()));
            if (NewID.size() == OriginalID.size()) {
                for (int i = 0; i < NewID.size(); i++) {
                    String update_sql = "update customer_gas set customer_gas.Gas_Id = " + NewID.get(i) + " where customer_gas.Gas_Id = "
                            + OriginalID.get(i) + " and Customer_Id = " + Customer_Id;

                    final String finalNewId = NewID.get(i);
                    ActionCallback updateCallback = new ActionCallback() {
                        @Override
                        public void onSuccess(String condition) {
                            S_condition = condition;
                            Log.i("update action", S_condition);
                            Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                            startActivity(intent);

                            if (S_condition.contains("failure")) {
                                String insert_sql = "insert into customer_gas(`Gas_Id`, `Customer_Id`) values (" + finalNewId + "," + Customer_Id + ")";
                                action(insert_sql, new ActionCallback() {
                                    @Override
                                    public void onSuccess(String condition) {
                                        S_condition = condition;
                                        Log.i("insert action", S_condition);
                                        if(S_condition.contains("failure")){
                                            Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                                            startActivity(intent);
                                        }
                                        else{
                                            Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        // Handle error condition
                                        // You may want to update S_condition accordingly
                                        Log.i("insert action error", error);
                                        Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(String error) {
                            // Handle error condition
                            // You may want to update S_condition accordingly
                            Log.i("update action error", error);
                            Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                            startActivity(intent);
                        }
                    };

                    action(update_sql, updateCallback);

                    if (S_condition.contains("success")) {
                        S_condition = "success";
                        Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                        startActivity(intent);
                    }
                }
            } else if (NewID.size() > OriginalID.size()) {
                for (int i = 0; i < NewID.size(); i++) {
                    if (i < OriginalID.size()) {
                        String update_sql = "update customer_gas set customer_gas.Gas_Id = " + NewID.get(i) + " where customer_gas.Gas_Id = "
                                + OriginalID.get(i) + " and Customer_Id = " + Customer_Id;

                        final String finalNewId = NewID.get(i);
                        ActionCallback updateCallback = new ActionCallback() {
                            @Override
                            public void onSuccess(String condition) {
                                S_condition = condition;
                                Log.i("update action", S_condition);
                                Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                                startActivity(intent);

                                if (S_condition.contains("failure")) {
                                    String insert_sql = "insert into customer_gas(`Gas_Id`, `Customer_Id`) values (" + finalNewId + "," + Customer_Id + ")";
                                    action(insert_sql, new ActionCallback() {
                                        @Override
                                        public void onSuccess(String condition) {
                                            S_condition = condition;
                                            Log.i("insert action", S_condition);
                                            if(S_condition.contains("failure")){
                                                Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                                                startActivity(intent);
                                            }
                                            else{
                                                Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {
                                            // Handle error condition
                                            // You may want to update S_condition accordingly
                                            Log.i("insert action error", error);
                                            Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(String error) {
                                // Handle error condition
                                // You may want to update S_condition accordingly
                                Log.i("update action error", error);
                                Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                startActivity(intent);
                            }
                        };

                        action(update_sql, updateCallback);
                    } else {
                        String insert_sql = "insert into customer_gas(`Gas_Id`, `Customer_Id`) values (" + NewID.get(i) + "," + Customer_Id + ")";

                        ActionCallback insertCallback = new ActionCallback() {
                            @Override
                            public void onSuccess(String condition) {
                                S_condition = condition;
                                Log.i("insert action", S_condition);
                                if(condition.contains("failure")){
                                    Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onError(String error) {
                                // Handle error condition
                                // You may want to update S_condition accordingly
                                Log.i("insert action error", error);
                                Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                startActivity(intent);
                            }
                        };

                        action(insert_sql, insertCallback);
                    }
                }
            } else {
                for (int i = 0; i < OriginalID.size(); i++) {
                    if (i < NewID.size()) {
                        String update_sql = "update customer_gas set customer_gas.Gas_Id = " + NewID.get(i) + " where customer_gas.Gas_Id = "
                                + OriginalID.get(i) + " and Customer_Id = " + Customer_Id;

                        final String finalNewId = NewID.get(i);
                        ActionCallback updateCallback = new ActionCallback() {
                            @Override
                            public void onSuccess(String condition) {
                                S_condition = condition;
                                Log.i("update action", S_condition);
                                if(S_condition.contains("success")){
                                    Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                    startActivity(intent);
                                }

                                if (S_condition.contains("failure")) {
                                    String insert_sql = "insert into customer_gas(`Gas_Id`, `Customer_Id`) values (" + finalNewId + "," + Customer_Id + ")";
                                    action(insert_sql, new ActionCallback() {
                                        @Override
                                        public void onSuccess(String condition) {
                                            S_condition = condition;
                                            Log.i("insert action", S_condition);
                                            if(S_condition.contains("failure")){
                                                Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                                startActivity(intent);
                                            }
                                            else{
                                                Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {
                                            // Handle error condition
                                            // You may want to update S_condition accordingly
                                            Log.i("insert action error", error);
                                            Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(String error) {
                                // Handle error condition
                                // You may want to update S_condition accordingly
                                Log.i("update action error", error);
                                Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                startActivity(intent);
                            }
                        };

                        action(update_sql, updateCallback);
                    } else {
                        String delete_sql = "DELETE FROM `customer_gas` WHERE Gas_Id = " + OriginalID.get(i);

                        ActionCallback deleteCallback = new ActionCallback() {
                            @Override
                            public void onSuccess(String condition) {
                                S_condition = condition;
                                Log.i("delete action", S_condition);
                                if(S_condition.contains("failure")){
                                    Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onError(String error) {
                                // Handle error condition
                                // You may want to update S_condition accordingly
                                Log.i("delete action error", error);
                                Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                                startActivity(intent);
                            }
                        };

                        action(delete_sql, deleteCallback);
                    }
                }
            }
        } catch (Exception e) {
            Log.i("saveCustomerGasId ex", e.toString());
            Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
            startActivity(intent);
        }

    }


    //scanner
    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(ScanNewQRCode.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String _qrCode) {
                qrCode = _qrCode;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        input_newGasId.setText(qrCode);
                    }
                });
                Log.i(ScanReceiptQRCode.class.getSimpleName(), "QR Code Found: " + qrCode);
            }

            @Override
            public void qrCodeNotFound() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        order_ID_Text.setText(""); // Clear the EditText when QR code is not found
                    }
                });
            }
        }));
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }


}

