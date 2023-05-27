package com.example.smartgasmasterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import java.util.Objects;

public class Homepage extends AppCompatActivity {

    private Button editProfile;
    private Button orderList;
    //private Button scanOrder;
    private String email;
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
        //scanOrder = findViewById(R.id.open_scanner_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this,MainActivity.class);
                startActivity(intent);
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
        LoginActivity loginActivity = new LoginActivity();
        Worker_ID = loginActivity.getWorkerID();
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


    }
    public void WorkerName(){
        try{
            String Showurl = "http://10.0.2.2/SQL_Connect/Show_Worker_Profile.php";
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
            String Worker_Name = responseJSON.getString("Worker_Name");
            name.setText(Worker_Name);
        }
        catch(Exception e){
            Log.i("Worker name",e.toString());
        }
    }
}