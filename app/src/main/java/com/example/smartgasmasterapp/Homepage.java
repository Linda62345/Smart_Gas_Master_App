package com.example.smartgasmasterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.smartgasmasterapp.ui.login.LoginActivity;

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
import java.util.Objects;

public class Homepage extends AppCompatActivity {

    private Button editProfile;
    private Button orderList;
    private Button scanOrder;
    private String email;
    public int Worker_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Objects.requireNonNull(getSupportActionBar()).hide();

        editProfile = findViewById(R.id.edit_personal_info_button);
        orderList = findViewById(R.id.orderList_page_button);
        scanOrder = findViewById(R.id.open_scanner_button);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, EditPersonalInfo.class);
                startActivity(intent);
            }
        });

        orderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, OrderList.class);
                startActivity(intent);
            }
        });


        scanOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, ScanReceiptQRCode.class);
                startActivity(intent);
            }
        });

    }
}