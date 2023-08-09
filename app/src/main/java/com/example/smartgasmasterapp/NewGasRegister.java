package com.example.smartgasmasterapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//從origin 來的去remain gas
//從new 回去原來的頁面
public class NewGasRegister extends AppCompatActivity {
    // Declare public elements
    public TextView manuallyEnterCode;
    public Button nextGas;
    public TextView gasEmptyWeight;
    public TextView newScannerString;
    public TextView newText;
    public TextView changeableNewID;
    public TextView textView5;
    public TextView changeableNewVolume;
    public Button confirmNewScanButton,skip;

    // Other public variables
    public String order_Id;
    public String currentDateTimeString;

    //private variables
    private PreviewView newScanner;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private EditText mannuallyEnterGasCode,inputGasEmptyWeight;
    private String qrCode,gasId,gasWeight;
    private String URL = "http://54.199.33.241/test/NewRegisterGas.php";
    private ArrayList<String> new_Gas_Id_Array, empty_Weight_Array;
    public Remain_Gas remain_Gas;
    public String sensor_Id;
    //把gas_id 跟 gas + emptyweight 寫進去 iot table

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find views by ID
        newScanner = findViewById(R.id.oldScanner);
        manuallyEnterCode = findViewById(R.id.manuallyEnterCode);
        mannuallyEnterGasCode = findViewById(R.id.mannuallyEnterGasCode);
        gasEmptyWeight = findViewById(R.id.gas_empty_weight);
        inputGasEmptyWeight = findViewById(R.id.input_gas_empty_weight);
        newText = findViewById(R.id.oldText);
        changeableNewID = findViewById(R.id.changeableOldID);
        textView5 = findViewById(R.id.textView5);
        skip = findViewById(R.id.skipRegister);

        //inputs
        changeableNewVolume = findViewById(R.id.changeableOldVolume);
        confirmNewScanButton = findViewById(R.id.confirm_OldScan_button);

        //取得唯一的sensor Id
        remain_Gas = new Remain_Gas();
        sensor_Id = remain_Gas.finalSensorId.get(0);


        //Gas ID
        mannuallyEnterGasCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeableNewID.setText(mannuallyEnterGasCode.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });


        //Gas Empty Weight
        inputGasEmptyWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeableNewVolume.setText(inputGasEmptyWeight.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        //ShowDataDetail();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        requestCamera();
        confirmNewScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mannuallyEnterGasCode.getText().toString().length()==15) {
                    gasId = mannuallyEnterGasCode.getText().toString();
                    if (!inputGasEmptyWeight.getText().toString().trim().equals("")) {
                        gasWeight = inputGasEmptyWeight.getText().toString();
                        Log.i("empty_weight", gasWeight);
                        saveGas(URL);
                    } else {
                        Toast.makeText(getApplicationContext(), "請輸入瓦斯空桶重", Toast.LENGTH_LONG).show();
                    }
                }
                else if(mannuallyEnterGasCode.getText().toString()==null){
                    Toast.makeText(getApplicationContext(), "請輸入瓦斯桶編號", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "請輸入正確十五碼瓦斯桶編號", Toast.LENGTH_LONG).show();
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewGasRegister.this, RegisterWeightOnly.class);
                startActivity(intent);
            }
        });
    }


    public void saveGas(String URL){
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("gas register response", response);
                    if (response.contains("success")) {
                        Toast.makeText(getApplicationContext(), "瓦斯桶新增成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(NewGasRegister.this, ecchangeSucced.class);
                        startActivity(intent);
                    } else if (response.contains("Duplicate")) {
                        Toast.makeText(getApplicationContext(), "新增失敗，此瓦斯桶已在資料庫中", Toast.LENGTH_LONG).show();
                    } else if (response.contains("failure")) {
                        Toast.makeText(getApplicationContext(), "新增失敗", Toast.LENGTH_LONG).show();
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
                    data.put("gasId", gasId);
                    data.put("gasWeightEmpty", gasWeight);
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

    //scanner
    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(NewGasRegister.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
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
        newScanner.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(newScanner.createSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String _qrCode) {
                qrCode = _qrCode;
                if (qrCode.length() == 15) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mannuallyEnterGasCode.setText(qrCode);
                        }
                    });
                    Log.i(NewGasRegister.class.getSimpleName(), "QR Code Found: " + qrCode);
                }
                else {
                    Log.i(NewGasRegister.class.getSimpleName(), "QR Code ID Length Incorrect");
                }
            }

            @Override
            public void qrCodeNotFound() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(ScanReceiptQRCode.class.getSimpleName(), "QR Not Found");
//                        order_ID_Text.setText(""); // Clear the EditText when QR code is not found
                    }
                });
            }
        }));
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
    }
}
