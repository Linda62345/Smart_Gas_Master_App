package com.example.smartgasmasterapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class ecchangeSucced extends AppCompatActivity {
    public Button check;
    public Remain_Gas remainGas;
    public OrderList orderList;
    public String successSensorId;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_succed);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        remainGas = new Remain_Gas();
        orderList = new OrderList();

        successSensorId = remainGas.finalSensorId.get(0);
        orderList.sensor_Id_Array.add(successSensorId);

        check = findViewById(R.id.exchangeSuccessNext);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ecchangeSucced.this, OrderInfo.class);
                startActivity(intent);
            }
        });
    }
}
