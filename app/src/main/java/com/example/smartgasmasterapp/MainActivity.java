package com.example.smartgasmasterapp;

import static com.example.smartgasmasterapp.ui.login.LoginActivity.Worker_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.smartgasmasterapp.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       // Check login status from SharedPreferences
////       SharedPreferences sharedPref = getSharedPreferences("login_data", Context.MODE_PRIVATE);
////       boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);
//////
//       if (isLoggedIn) {
////           int workerId = sharedPref.getInt("Worker_ID", -1);
////           Log.d("MainActivity", "Logged in. Worker ID: " + sharedPref.getInt("Worker_ID", -1));
//
//
//           navigateToHomepage(workerId);
 //      }

        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login_page_button);
        register = findViewById(R.id.register_page_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private void navigateToHomepage(int workerId) {
        Intent intent = new Intent(MainActivity.this, Homepage.class);
        intent.putExtra("Worker_ID", workerId);
        startActivity(intent);
        finish(); // Optional: Finish MainActivity to prevent going back to it from Homepage
    }

}