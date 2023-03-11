package com.example.smartgasmasterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartgasmasterapp.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ForgetPassword1 extends AppCompatActivity {

    private Button newPass;
    public EditText phone;
    private String URL = "http://10.0.2.2/SQL_Connect/checkAccount.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password1);

        phone = findViewById(R.id.enterPhone_ForgetPass);
        newPass = findViewById(R.id.setNewPassButton);

        newPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAccount();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //顯示不出來
        Log.d("ForgetPassword1", phone.getText().toString());
    }
    private void checkAccount(){
        String phoneNum = phone.getText().toString().trim();
        if(phoneNum==""){
            Toast.makeText(this, "請輸入手機號碼", Toast.LENGTH_SHORT).show();
        }
        else{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("res", response);
                    if (response.equals("success")) {
                        Intent intent = new Intent(ForgetPassword1.this, ForgetPassword2.class);
                        intent.putExtra("phone", Integer.parseInt(phone.getText().toString()));
                        startActivity(intent);
                    } else if (response.equals("failure")) {
                        Toast.makeText(ForgetPassword1.this, "此手機尚未註冊", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    phone.setText(error.toString().trim());
                    Toast.makeText(ForgetPassword1.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("phone", phoneNum); //php,本地端
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }

    }
    public void OTP(){

    }
}
