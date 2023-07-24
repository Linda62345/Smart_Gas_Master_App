package com.example.smartgasmasterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

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
import java.util.ArrayList;
import java.util.Objects;

public class ScanOriginalQRCode extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private Button next,next_gas;
    public String Original_Order_Id;
    private TextView gas_Id;
    private EditText input_Id;
    public int gas_quantity;
    public static ArrayList<String> Gas_Id_Array = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_original_qrcode);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        next = findViewById(R.id.confirm_originalScan_button);
        next_gas = findViewById(R.id.next_gas);

        OrderInfo orderInfo = new OrderInfo();
        gas_quantity = orderInfo.gas_quantity;
        Gas_Id_Array = new ArrayList<String>();


        gas_Id = findViewById(R.id.changableOriginalID);

        input_Id = findViewById(R.id.mannuallyEnterGasCode);
        input_Id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                gas_Id.setText(input_Id.getText().toString());
            }
        });

        //for(int i=0;i<gas_quantity;i++){
        next_gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_Id.getText().toString()!=null&&input_Id.getText().toString()!=""){
                    Gas_Id_Array.add(input_Id.getText().toString());
                }
                input_Id.setText("");
            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_Id.getText().toString()!=null&&input_Id.getText().toString()!=""){
                    Gas_Id_Array.add(input_Id.getText().toString());
                }
                sure();
            }
        });

        CodeScannerView scannerView = findViewById(R.id.originalScanner);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanOriginalQRCode.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
    public void sure(){
            try{
                String Showurl = "http://54.199.33.241/test/Show_Gas_Info.php";
                URL url = new URL(Showurl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String gas_Id = input_Id.getText().toString().trim();
                String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(gas_Id), "UTF-8");
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
                Log.i("Gas_ID", "["+result+"]");
                JSONObject responseJSON = new JSONObject(result);
                if(responseJSON.getString("response").contains("failure")){
                    Toast.makeText(this, "此瓦斯桶尚未註冊", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.i("Gas_Data Exception", e.toString());
            }
            Intent intent = new Intent(ScanOriginalQRCode.this, Remain_Gas.class);
            startActivity(intent);
        }


}