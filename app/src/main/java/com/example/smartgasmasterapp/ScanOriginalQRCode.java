package com.example.smartgasmasterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Objects;

public class ScanOriginalQRCode extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private Button next,next_gas;
    public String Original_Order_Id;
    private TextView gas_Id;
    private EditText input_Id;
    public int gas_quantity;
    public static ArrayList<String> Gas_Id_Array = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_original_qrcode);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        next = findViewById(R.id.confirm_originalScan_button);
        next_gas = findViewById(R.id.next_gas);

        OrderInfo orderInfo = new OrderInfo();
        //Original_Order_Id = orderInfo.getOriginal_Order_Id();
        //Log.i("Original_Gas_Id",Original_Order_Id);
        gas_quantity = orderInfo.gas_quantity;
        Gas_Id_Array = new ArrayList<String>();


        gas_Id = findViewById(R.id.changableOriginalID);
        //gas_Id.setText(Original_Order_Id);

        input_Id = findViewById(R.id.mannuallyEnterGasCode);

        //for(int i=0;i<gas_quantity;i++){
        next_gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_Id.getText().toString()!=null&&input_Id.getText().toString()!=""){
                    Gas_Id_Array.add(input_Id.getText().toString());
                }
                input_Id.setText("");
            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_Id.getText().toString()!=null&&input_Id.getText().toString()!=""){
                    Gas_Id_Array.add(input_Id.getText().toString());
                }
                sure();
            }
        });

        CodeScannerView scannerView = findViewById(R.id.originalScanner);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanOriginalQRCode.this, result.getText(), Toast.LENGTH_SHORT).show();
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
    }

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
    public void sure(){
        Intent intent = new Intent(ScanOriginalQRCode.this, ScanNewQRCode.class);
        startActivity(intent);
    }

}