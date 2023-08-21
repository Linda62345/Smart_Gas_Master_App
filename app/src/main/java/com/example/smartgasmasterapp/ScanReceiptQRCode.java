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
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.BarcodeCallback;


public class ScanReceiptQRCode extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private Button qrCodeFoundButton;
    private DecoratedBarcodeView barcodeView;
   // private DecoratedBarcodeView barcodeView;
    private String qrCode;
    private EditText order_ID_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_receipt_qrcode);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        barcodeView = findViewById(R.id.receiptScanner);
        barcodeView.decodeContinuous(callback);
        order_ID_Text = findViewById(R.id.mannuallyEnterReceiptCode);
        qrCodeFoundButton = findViewById(R.id.confirm_ReceiptScan_button);
        qrCodeFoundButton.setVisibility(View.VISIBLE);


        qrCodeFoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveIOT();
                // enterNewIot.setText(""); // Clear the EditText
//                enterNewIot.setText(qrCode);
//                Toast.makeText(getApplicationContext(), qrCode, Toast.LENGTH_SHORT).show();
//                Log.i(ScanReceiptQRCode.class.getSimpleName(), "QR Code Found: " + qrCode);
            }
        });

        //scanner
        qrCodeFoundButton.setVisibility(View.VISIBLE);

        qrCodeFoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveIOT();
                // enterNewIot.setText(""); // Clear the EditText
//                enterNewIot.setText(qrCode);
//                Toast.makeText(getApplicationContext(), qrCode, Toast.LENGTH_SHORT).show();
//                Log.i(ScanReceiptQRCode.class.getSimpleName(), "QR Code Found: " + qrCode);
                Intent intent = new Intent(ScanReceiptQRCode.this,Remain_Gas.class);
                startActivity(intent);
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
        @Override
        public void barcodeResult(BarcodeResult result) {


            String qrCodeText = result.getText();
            if (qrCodeText != null) {
                order_ID_Text.setText(qrCodeText);
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


    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(ScanReceiptQRCode.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
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
//        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);
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
//    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
//        @Override
//        public void onQRCodeFound(String _qrCode) {
//            qrCode = _qrCode;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    order_ID_Text.setText(qrCode);
//                }
//            });
//            Log.i(ScanReceiptQRCode.class.getSimpleName(), "QR Code Found: " + qrCode);
//        }
//
//        @Override
//        public void qrCodeNotFound() {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                        order_ID_Text.setText(""); // Clear the EditText when QR code is not found
//                }
//            });
//        }
//    }));
//    Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
//}
}
