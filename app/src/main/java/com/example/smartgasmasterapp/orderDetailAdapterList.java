package com.example.smartgasmasterapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class orderDetailAdapterList extends ArrayAdapter<orderDetail> {
    int mresource;
    private Context mContext;
    public orderDetailAdapterList(Context context, int resource, ArrayList<orderDetail> objects) {
        super(context, resource, objects);
        mresource = resource;
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        String quantity = getItem(position).getQuantity();
        String type = getItem(position).getType();
        String weight = getItem(position).getWeight();

        orderDetail orderDetail = new orderDetail(quantity,type,weight);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mresource,parent,false);

        TextView tvQuantity = (TextView) convertView.findViewById(R.id.quantity);
        TextView tvType = (TextView) convertView.findViewById(R.id.type);
        TextView tvWeight = (TextView) convertView.findViewById(R.id.weight);

        if(tvQuantity!=null&&tvType!=null&&tvWeight!=null){
            tvQuantity.setText("數量: "+quantity);
            tvType.setText("類別: "+type);
            tvWeight.setText("重量: "+weight);
        }

        return  convertView;
    }
}
