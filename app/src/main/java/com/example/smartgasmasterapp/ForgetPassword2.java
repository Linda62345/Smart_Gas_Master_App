package com.example.smartgasmasterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.smartgasmasterapp.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ForgetPassword2 extends AppCompatActivity {

    private Button back;
    public ForgetPassword1 forgetPassword1;
    public EditText NewPassword,reNewPassword;
    private TextView tvStatus;
    public String newpassword, renewpassword;
    public int phone;
    private String URL = "http://10.0.2.2/SQL_Connect/reSetPassword.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password2);

        back = findViewById(R.id.backToLoginButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
                //Intent intent = new Intent(ForgetPassword2.this, LoginActivity.class);
                //startActivity(intent);
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        phone = bundle.getInt("phone");
        //從forgetpassword1拿到phone value的值
        Log.i("phone value", String.valueOf(phone));
        NewPassword = findViewById(R.id.enterNewPass);
        reNewPassword = findViewById(R.id.enterConfirmNewPass);
        tvStatus = findViewById(R.id.tvStatus);
    }
    public void resetPassword(){
        newpassword = NewPassword.getText().toString().trim();
        Log.i("org",newpassword);
        renewpassword = reNewPassword.getText().toString().trim();
        Log.i("new",renewpassword);
        if(!newpassword.equals(renewpassword)){
            Toast.makeText(this,"密碼與確認密碼不相同",Toast.LENGTH_SHORT).show();
        }
        else if(newpassword.equals("")||renewpassword.equals("")){
            Toast.makeText(this,"密碼與確認密碼必填",Toast.LENGTH_SHORT).show();
        }
        else{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        Intent intent = new Intent(ForgetPassword2.this, LoginActivity.class);
                        startActivity(intent);
                        tvStatus.setText("Successfully changePassword.");
                        back.setClickable(false);
                    } else if (response.equals("failure")) {
                        tvStatus.setText("Something went wrong!");
                    } else if (response.equals("the phone didn't sign up yet!!!")){
                        tvStatus.setText("此手機號碼尚未註冊");
                    }
            }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("phone", String.valueOf(phone));
                    data.put("NewPassword", newpassword);
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);

        }
    }
}