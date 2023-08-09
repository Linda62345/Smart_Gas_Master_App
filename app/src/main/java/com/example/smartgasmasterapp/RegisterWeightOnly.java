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

import java.util.HashMap;
import java.util.Map;

public class RegisterWeightOnly extends AppCompatActivity {

    public Button complete;
    public EditText gasEmptyWeight;
    public Remain_Gas remain_Gas;
    public String sensor_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_weight_only);

        complete = findViewById(R.id.confirmGasEmptyWeightButton);
        gasEmptyWeight = findViewById(R.id.input_gas_empty_weight);

        remain_Gas = new Remain_Gas();
        sensor_Id = remain_Gas.finalSensorId.get(0);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gasEmptyWeight.getText().toString()!=null){
                    saveGasEmptyWeight();
                }
                else{
                    Toast.makeText(getApplicationContext(), "請輸入瓦斯空桶重", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void saveGasEmptyWeight(){
        try{
        String URL = "http://54.199.33.241/test/inputGasEmptyWeight.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("gas register response", response);
                if (response.contains("success")) {
                    Toast.makeText(getApplicationContext(), "瓦斯桶新增成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterWeightOnly.this, ecchangeSucced.class);
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
                data.put("gasWeightEmpty", gasEmptyWeight.getText().toString());
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
}