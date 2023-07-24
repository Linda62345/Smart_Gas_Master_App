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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditPersonalInfo extends AppCompatActivity {
    public int Worker_ID;
    public String Worker_Name, Worker_Address, Worker_Email;
    public int Worker_Tel,Worker_Phone;
    private EditText Name, Address, Email, Tel, Phone;
    private Button save;
    public String worker_name="",phone="",tel="",address="",email="";
    public Homepage homepage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_info);
        Name = findViewById(R.id.editName);
        Address = findViewById(R.id.editCompanyAddress);
        Email = findViewById(R.id.editEmail);
        Tel = findViewById(R.id.editHousePhone);
        Phone = findViewById(R.id.editPhoneNo);
        save = findViewById(R.id.saveInfo_button);

        LoginActivity loginActivity = new LoginActivity();
        Worker_ID = loginActivity.getWorkerID();
        Log.i("editInfo", String.valueOf(Worker_ID));

        //因為呼叫API(Internet) 所以要用thread避免等待時間過長
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    showData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = findViewById(R.id.editName);
                Address = findViewById(R.id.editCompanyAddress);
                Email = findViewById(R.id.editEmail);
                Tel = findViewById(R.id.editHousePhone);
                Phone = findViewById(R.id.editPhoneNo);
                saveProfile();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }
    public void showData() throws MalformedURLException {
        try{
        String Showurl = "http://54.199.33.241/test/Show_Worker_Profile.php";
        URL url = new URL(Showurl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(Worker_ID), "UTF-8");
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
        Log.i("result", "["+result+"]");
        JSONObject responseJSON = new JSONObject(result);
        Worker_Name = responseJSON.getString("Worker_Name");
        Name.setText(Worker_Name);
        Worker_Phone = responseJSON.getInt("Worker_Phone");
        Phone.setText(String.valueOf(Worker_Phone));
        Worker_Tel = responseJSON.getInt("Worker_Tel");
        Tel.setText(String.valueOf(Worker_Tel));
        Worker_Address = responseJSON.getString("Worker_Address");
        Address.setText(Worker_Address);
        Worker_Email = responseJSON.getString("Worker_Email");
        Email.setText(Worker_Email);
        Log.i("Worker_Name",Worker_Name);
    } catch (Exception e) {
        Log.i("Edit Exception", e.toString());
    }
    }

    public void saveProfile(){
        try {
            String URL = "http://54.199.33.241/test/Save_Worker_Profile.php";
            worker_name = Name.getText().toString().trim();
            phone = Phone.getText().toString().trim();
            tel = Tel.getText().toString().trim();
            address = Address.getText().toString().trim();
            email = Email.getText().toString().trim();
            if (worker_name.equals("") || phone.equals("") || tel.equals("") || address.equals("") || email.equals("")) {
                Toast.makeText(this, "以上不可為空白", Toast.LENGTH_SHORT).show();
            } else {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("success")) {
                            Log.i("saveProfile", "Successfully registered.");
                            Intent intent = new Intent(EditPersonalInfo.this, Homepage.class);
                            intent.putExtra("email", Email.getText().toString());
                            startActivity(intent);
                            save.setClickable(false);
                        } else if (response.contains("failure")) {
                            Log.i("saveProfile", "Something went wrong!");
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
                        data.put("id", String.valueOf(Worker_ID));
                        data.put("name", worker_name);
                        data.put("phone", phone);
                        data.put("houseTel", tel);
                        data.put("email", email);
                        data.put("address", address);

                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        }
        catch (Exception e){
            Log.i("Save Exception", e.toString());
        }
    }
}