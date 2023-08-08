package com.example.smartgasmasterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.smartgasmasterapp.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
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
import java.util.Objects;

public class OrderListUnfinished extends AppCompatActivity {

    private Button finished;
    public String Worker_Id;
    private ListView orderlist;
    private String[] data,order_Id;
    public static String static_order_id;
    InputStream is = null;
    String line,result = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_unfinished);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        finished = findViewById(R.id.order_finished);

        finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderListUnfinished.this, OrderList.class);
                startActivity(intent);
            }
        });
        LoginActivity loginActivity = new LoginActivity();
        Worker_Id = String.valueOf(loginActivity.getWorkerID());
        Log.i("Unfinish: ",Worker_Id);

        orderlist = (ListView)findViewById(R.id.list_item);
        getData();
        try {
            getOrderList();
        } catch (Exception e) {
            Log.i("OrderList create Exception",e.toString());
        }
        setAdapter();
        orderlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //當備案下時
                String msg=data[position];
                Toast.makeText(OrderListUnfinished.this,msg,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrderListUnfinished.this, OrderInfo.class);
                String Id = order_Id[position];
                static_order_id = Id;
                //intent.putExtra("order_Id", Id);
                startActivity(intent);
            }
        });
    }
    private void setAdapter() {
        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        orderlist.setAdapter(adapter);
    }
    private void getOrderList() throws MalformedURLException {
        try{
            String Showurl = "http://10.0.2.2/SQL_Connect/Worker_UnOrderList.php";
            URL url = new URL(Showurl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(Worker_Id), "UTF-8");
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
            try{
                JSONArray ja = new JSONArray(result);
                JSONObject jo = null;

                data = new String[ja.length()];
                order_Id = new String[ja.length()];

                for(int i = 0; i<ja.length();i++){
                    jo = ja.getJSONObject(i);
                    data[i] = jo.getString("DELIVERY_Address")+String.valueOf(i+1)+"訂單";
                    Log.i("order data",data[i]);
                    order_Id[i] = jo.getString("ORDER_Id");
                }
            }catch(Exception e){
                Log.i("OrderList JSON Exception",e.toString());
            }
        } catch (Exception e) {
            Log.i("GetOrderList Exception", e.toString());
        }
    }
    private void getData(){
        //還要把worker Id丟過去
        try {
            String dataurl = "http://10.0.2.2/SQL_Connect/Worker_UnOrderList.php";
            URL url = new URL(dataurl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
        }
        catch(Exception e){
            Log.i("OrderList Exception",e.toString());
        }
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            while((line = br.readLine())!=null){
                sb.append(line+"\n");
            }
            is.close();
            result = sb.toString();
            Log.i("UnOrderList result",result);
        }catch(Exception e){
            Log.i("UnOrderList Exception2",e.toString());
        }
        try{
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;

            data = new String[ja.length()];

            for(int i = 0; i<ja.length();i++){
                jo = ja.getJSONObject(i);
                data[i] = jo.getString("Assign_Id");
                Log.i("order data",data[i]);
            }
        }catch(Exception e){
            Log.i("UnOrderList JSON Exception",e.toString());
        }
    }
}