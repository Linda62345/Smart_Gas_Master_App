
package com.example.smartgasmasterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;


public class ScanNewQRCode extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Button next,next_gas;
    public String order_Id, currentDateTimeString;
    public EditText input_newGasId;
    private TextView GAS_ID, Initial_Volume, GAS_Type;
    public ArrayList<String> New_Gas_Id_Array;
    private DecoratedBarcodeView barcodeView;
    public String Customer_Id,qrCode,gas_Id1,Gas_Weight_Empty,sensor_Id;
    public static String condition,S_condition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_new_qrcode);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ScanOriginalQRCode scanOriginalQRCode = new ScanOriginalQRCode();
        OrderInfo orderInfo = new OrderInfo();

        next = findViewById(R.id.confirm_NewScan_button);
        barcodeView = findViewById(R.id.newScanner);
        barcodeView.decodeContinuous(callback);

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
                //saveNewGas();
                // Finish the current activity to prevent the user from navigating back to it
                finish();
                if(input_newGasId.getText().toString().trim().equals("")){
                    Intent intent = new Intent(ScanNewQRCode.this,NewGasRegister.class);
                    startActivity(intent);
                }
                else{
                    GasData();
                }
            }
        });

        New_Gas_Id_Array = new ArrayList<String>();

        Remain_Gas remain_gas = new Remain_Gas();
        Customer_Id = remain_gas.Customer_Id;
        sensor_Id = remain_gas.finalSensorId.get(0);
        //scanner
        next.setVisibility(View.VISIBLE);

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
            gas_Id1 = input_newGasId.getText().toString().trim();
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
                    Intent intent = new Intent(ScanNewQRCode.this,NewGasRegister.class);
                    startActivity(intent);

                } else {
                    String S_Gas_ID, S_initial_volume, S_Gas_Type;
                    S_Gas_ID = responseJSON.getString("GAS_Id");
                    GAS_ID.setText(S_Gas_ID);
                    S_initial_volume = responseJSON.getString("GAS_Volume");
                    Initial_Volume.setText(S_initial_volume);
                    S_Gas_Type = responseJSON.getString("GAS_Type");
                    GAS_Type.setText(S_Gas_Type);
                    Gas_Weight_Empty = responseJSON.getString("GAS_Weight_Empty");
                    Log.i("gas_weight",Gas_Weight_Empty);
                    // 更新iot資訊: 空桶重6
                    saveIot();
                    if (input_newGasId.getText().toString() != null && input_newGasId.getText().toString() != "") {
                        New_Gas_Id_Array.add(input_newGasId.getText().toString());
                    }
                }
            }
        } catch(Exception e){
            Log.i("Gas_Data Exception", e.toString());
        }
    }

    public void saveIot(){
        try {
            String URL = "http://54.199.33.241/test/scanNewGas_iot.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("gas register response", response);
                    if (response.contains("success")) {
                        Intent intent = new Intent(ScanNewQRCode.this, ecchangeSucced.class);
                        startActivity(intent);
                    }  else {
                        Intent intent = new Intent(ScanNewQRCode.this, ExchangeScanFailed.class);
                        startActivity(intent);
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
                    data.put("gasId", gas_Id1);
                    data.put("gasWeightEmpty", Gas_Weight_Empty);
                    data.put("sensorId",sensor_Id);

                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.i("save 瓦斯桶 Exception", e.toString());
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
               // bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {


            String qrCodeText = result.getText();
            if (qrCodeText != null && qrCodeText.length() == 10) {
                input_newGasId.setText(qrCodeText);
                Log.i("Scanned QR Code", qrCodeText);
            } else {
                Log.i("Scanned QR Code length invallid ", qrCodeText);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // Handle possible result points
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    private void startQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a QR code");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }



}

