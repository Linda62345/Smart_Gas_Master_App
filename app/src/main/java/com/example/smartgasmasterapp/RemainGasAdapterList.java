package com.example.smartgasmasterapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class RemainGasAdapterList extends ArrayAdapter<String> {
    int mresource;
    private Context mContext;
    int id;
    public Remain_Gas remain_gas;

    public RemainGasAdapterList (Context context, int resource, ArrayList<String> objects) {
        super(context,resource,objects);
        mresource = resource;
        mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String volumn = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mresource,parent,false);

        Button delete = (Button) convertView.findViewById(R.id.deleteButton);
        TextView gasvolumn = (TextView) convertView.findViewById(R.id.gasvolume);
        if(volumn!=null){
            gasvolumn.setText(volumn);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remain_gas = new Remain_Gas();
                if(remain_gas.remainGas.size()>0){
                    remain_gas.remainGas.remove(position);
                    remain_gas.remainGasVolumnList.remove(position);
                    remain_gas.finalSensorId.remove(position);
                    notifyDataSetChanged();
                }
            }
        });

        return  convertView;
    }

}
