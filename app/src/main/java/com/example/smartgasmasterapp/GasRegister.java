package com.example.smartgasmasterapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GasRegister extends AppCompatActivity {
    // Declare public elements
    public androidx.camera.view.PreviewView newScanner;
    public TextView manuallyEnterCode;
    public EditText mannuallyEnterGasCode;
    public Button nextGas;
    public TextView gasEmptyWeight;
    public EditText inputGasEmptyWeight;
    public TextView newScannerString;
    public TextView newText;
    public TextView changeableNewID;
    public TextView textView5;
    public TextView changeableNewVolume;
    public Button confirmNewScanButton;

    // Other public variables
    public String order_Id;
    public String currentDateTimeString;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find views by ID
        newScanner = findViewById(R.id.newScanner);
        manuallyEnterCode = findViewById(R.id.manuallyEnterCode);
        mannuallyEnterGasCode = findViewById(R.id.mannuallyEnterGasCode);
        nextGas  = findViewById(R.id.next_gas);
        gasEmptyWeight = findViewById(R.id.gas_empty_weight);
        inputGasEmptyWeight = findViewById(R.id.input_gas_empty_weight);
        newScannerString = findViewById(R.id.newScannerString);
        newText = findViewById(R.id.NewText);
        changeableNewID = findViewById(R.id.changeableNewID);
        textView5 = findViewById(R.id.textView5);
        changeableNewVolume = findViewById(R.id.changeableNewVolume);
        confirmNewScanButton = findViewById(R.id.confirm_NewScan_button);

        confirmNewScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //從origin 來的去remain gas
                //從new 回去原來的頁面
                //Intent intent = new Intent(GasRegister.this, ecchangeSucced.class);
                //startActivity(intent);
            }
        });
    }
}
