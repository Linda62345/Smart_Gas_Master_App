package com.example.smartgasmasterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class Homepage extends AppCompatActivity {

    private Button editProfile;
    private Button orderList;
    //private Button scanOrder;
    private String email, password ;
    public int Worker_ID;
    public Button logout;
    public TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);

        Objects.requireNonNull(getSupportActionBar()).hide();


        logout = findViewById(R.id.logout);
        name = findViewById(R.id.changeable_username);
        editProfile = findViewById(R.id.edit_personal_info_button);
        orderList = findViewById(R.id.orderList_page_button);

        // Retrieve extras from intent
        Intent intent = getIntent();
        LoginActivity loginActivity = new LoginActivity();
        email = loginActivity.email;
        password = loginActivity.password;
        Worker_ID = loginActivity.Worker_ID;
        Log.d("Homepage", "Email: " + email + ", Password: " + password + ", Worker ID: " + Worker_ID);
        //scanOrder = findViewById(R.id.open_scanner_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this,MainActivity.class);
                clearLoginData();
                // intent.putExtra("c
                startActivity(intent);
                finish();
            }
        });

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


        /*scanOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, ScanReceiptQRCode.class);
                startActivity(intent);
            }
        });*/
//        LoginActivity loginActivity = new LoginActivity();
//        Worker_ID = loginActivity.getWorkerID();
//        Log.i("worker id：" , String.valueOf(Worker_ID));
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    WorkerName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(timeZone);
        String currentDateTimeString = dateFormat.format(new Date());
        Log.i("current home time",currentDateTimeString);
    }
    public void WorkerName(){
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String Worker_Name = responseJSON.getString("Worker_Name");
                        name.setText(Worker_Name);
                    }
                    catch (Exception e){
                        Log.i("Worker Name Ex",e.toString());
                    }
                }
            });
        }
        catch(Exception e){
            Log.i("Worker name",e.toString());
        }
    }

    private void clearLoginData() {
        SharedPreferences sharedPref = getSharedPreferences("login_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("email");
        editor.remove("password");
        editor.apply();
    }
}