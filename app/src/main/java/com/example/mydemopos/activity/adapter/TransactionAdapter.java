package com.example.mydemopos.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mydemopos.R;
import com.example.mydemopos.entity.TransList;
import com.example.mydemopos.entity.Transaction;

import java.util.List;

public class TransactionAdapter extends ArrayAdapter<TransList> {

    private int resId;//自己定义的listview中要存放的布局
    public TransactionAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<TransList> objects) {
        super(context, resource, textViewResourceId, objects);
        resId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        super.getView(position, convertView, parent);
        TransList transList = getItem(position);//获得listView中每一个item的封装的实体
        //获得listView中每一个item的布局
        View view = LayoutInflater.from(getContext()).inflate(resId,parent,false);
        //获得图片资源
        ImageView imageView = view.findViewById(R.id.imageView);
        //获得transNo textView
        TextView transNo = view.findViewById(R.id.transNo);
        //获得baseAmount textView
        TextView baseAmount = view.findViewById(R.id.baseAmount);
        //获得response textView
        TextView response = view.findViewById(R.id.txv_res);

        imageView.setImageResource(transList.getImageId());
        transNo.setText("TransNo：" + transList.getTransNo());
        baseAmount.setText(String.valueOf("BaseAmount：" + transList.getBaseAmount()));
        response.setText("Response：" + transList.getResponse());
        return view;
    }
}
