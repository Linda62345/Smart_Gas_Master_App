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
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.Result;

import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.BarcodeCallback;
import java.util.concurrent.ExecutionException;

public class ScanOriginalQRCode extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Button next, next_gas;
    public String Original_Order_Id, GAS_ID;
    private TextView gas_Id;
    private String qrCode;
    private EditText input_Id;
    public int gas_quantity;
    private DecoratedBarcodeView barcodeView;
    public static ArrayList<String> Gas_Id_Array = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_original_qrcode);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //next_gas = findViewById(R.id.next_gas);
        next = findViewById(R.id.confirm_originalScan_button);

        OrderInfo orderInfo = new OrderInfo();
        gas_quantity = orderInfo.gas_quantity;
        Gas_Id_Array = new ArrayList<String>();

        gas_Id = findViewById(R.id.changableOriginalID);
        input_Id = findViewById(R.id.mannuallyEnterGasCode);
        barcodeView = findViewById(R.id.originalScanner);
        barcodeView.decodeContinuous(callback);

        /*next_gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_Id.getText().toString() != null && input_Id.getText().toString() != "") {
                    Gas_Id_Array.add(input_Id.getText().toString());
                }
                input_Id.setText("");
            }
        });*/


        //scanner
        next.setVisibility(View.VISIBLE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_Id.getText().toString() != null && !input_Id.getText().toString().trim().equals("")) {
                    Gas_Id_Array.add(input_Id.getText().toString());
                    sure();
                }
                else{
                    Intent intent = new Intent(ScanOriginalQRCode.this, GasRegister.class);
                    startActivity(intent);
                }
            }
        });


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

        input_Id.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                gas_Id.setText(input_Id.getText().toString());
                Log.i("sensor_id", String.valueOf(gas_Id));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        //ShowDataDetail();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // bindCameraPreview(cameraProvider);
            } catch (Exception e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
        requestCamera();

    }

    private BarcodeCallback callback = new BarcodeCallback() {
        public void barcodeResult(BarcodeResult result) {
            String qrCodeText = result.getText();
            if (qrCodeText != null) {
                input_Id.setText(qrCodeText);
                Log.i("Scanned QR Code", qrCodeText);
            } else {
                Log.i("QR Code invallid ", qrCodeText);
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

    public void sure() {
        try {
            String Showurl = "http://54.199.33.241/test/Show_Gas_Info.php";
            URL url = new URL(Showurl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String gas_Id = input_Id.getText().toString().trim();
            String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(gas_Id, "UTF-8");
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
                Intent intent = new Intent(ScanOriginalQRCode.this, GasRegister.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ScanOriginalQRCode.this, Remain_Gas.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.i("Gas_Data Exception", e.toString());
        }
    }


    //scanner
    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(ScanOriginalQRCode.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
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

    //    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
//        barcodeView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);
//
//        Preview preview = new Preview.Builder()
//                .build();
//
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();
//
//        preview.setSurfaceProvider(previewView.createSurfaceProvider());
//
//        ImageAnalysis imageAnalysis =
//                new ImageAnalysis.Builder()
//                        .setTargetResolution(new Size(1280, 720))
//                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                        .build();
//
//        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
//            @Override
//            public void onQRCodeFound(String _qrCode) {
//                qrCode = _qrCode;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        input_Id.setText(qrCode);
//                    }
//                });
//                Log.i(ScanReceiptQRCode.class.getSimpleName(), "QR Code Found: " + qrCode);
//            }
//
//            @Override
//            public void qrCodeNotFound() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        order_ID_Text.setText(""); // Clear the EditText when QR code is not found
//                    }
//                });
//            }
//        }));
//        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the request code matches the ZXing scanner's request code
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                String scannedData = result.getContents(); // The scanned QR code data

                // Update the EditText with the scanned QR code data
                input_Id.setText(scannedData);
                Log.i("Scanned QR Code", scannedData);
            }
        }
    }
}
