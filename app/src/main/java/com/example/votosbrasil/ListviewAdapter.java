package com.example.votosbrasil;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListviewAdapter extends BaseAdapter {

    private ArrayList<String> mSpinnerItems, mData;
    private Context mContext;

    LayoutInflater mInflater;
    public ListviewAdapter(ArrayList<String> data, ArrayList<String> spinnerItem, Context context){
        mData = data;
        mSpinnerItems = spinnerItem;
        mContext = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_adapter, null);
        }

        Spinner spinner = (Spinner) view.findViewById(R.id.sp_candidato);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mSpinnerItems);
        spinner.setAdapter(adapter);
        return view;
    }

}
