package com.example.mydemopos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mydemopos.R;
import com.example.mydemopos.constant.MyKeyBoardView;
import com.example.mydemopos.entity.Transaction;
import com.example.mydemopos.util.AmountFormatUtilWatcher;

import java.lang.reflect.Method;

/*
交易金额
 */
public class SaleActivity extends AppCompatActivity {
    EditText edit_sale;
    MyKeyBoardView myKeyBoardView;
    Button btn_sale;
    Transaction transaction;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        mapView();//初始化
        edit_sale.setText("$0.00");//赋初始值
        edit_sale.setSelection(edit_sale.getText().toString().length());//光标显示在最后
        myKeyBoardView.setEditText(edit_sale);//让自定义键盘与自定义的输入框关联在一起
        myKeyBoardView.setVisibility(View.INVISIBLE);//先让键盘不显示
        edit_sale.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                myKeyBoardView.setVisibility(View.VISIBLE);//当聚焦的时候让键盘显示
            }
        });
        edit_sale.addTextChangedListener(new AmountFormatUtilWatcher(edit_sale));
    }

    //初始化所有控件以及视图
    public void mapView(){
        edit_sale = findViewById(R.id.edit_sale);
        editInit(edit_sale);
        myKeyBoardView = findViewById(R.id.mykeyboardview);
        btn_sale = findViewById(R.id.btn_sale);
        transaction = new Transaction();
    }

    //确认交易金额
    public void submitSale(View view){
        Intent intent = new Intent(this,TipActivity.class);
        float sale_money = Float.parseFloat(getResult(edit_sale.getText().toString()));
        transaction.setBaseAmount(sale_money);
        intent.putExtra("data",transaction);
        startActivity(intent);

    }

    //取得无逗号无美元符号的完整数额
    public String getResult(String str){
        return new AmountFormatUtilWatcher().formatAmount(str);
    }

    //修改输入框的默认属性，不显示系统键盘 同时聚焦的时候光标依然出现
    public void editInit(EditText editText){
        editText.setCursorVisible(true);//不显示系统键盘
        editText.setInputType(InputType.TYPE_NULL);//聚焦的时候光标依然出现
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myKeyBoardView.setVisibility(View.INVISIBLE);
    }
}