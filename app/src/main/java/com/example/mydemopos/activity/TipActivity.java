package com.example.mydemopos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydemopos.R;
import com.example.mydemopos.constant.MyKeyBoardView;
import com.example.mydemopos.entity.Transaction;
import com.example.mydemopos.util.AmountFormatUtilWatcher;

import java.lang.reflect.Method;

/*
小费
 */
public class TipActivity extends AppCompatActivity {

    EditText edit_tip;
    MyKeyBoardView myKeyBoardView;
    Button btn_tip;
    Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);
        mapView();//初始化
        edit_tip.setText("$0.00");//赋初始值
        edit_tip.setSelection(edit_tip.getText().toString().length());//光标显示在最后
        myKeyBoardView.setEditText(edit_tip);//让自定义键盘与自定义的输入框关联在一起
        myKeyBoardView.setVisibility(View.INVISIBLE);//先让键盘不显示
        edit_tip.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                myKeyBoardView.setVisibility(View.VISIBLE);//当聚焦的时候让键盘显示
            }
        });
        edit_tip.addTextChangedListener(new AmountFormatUtilWatcher(edit_tip));
    }

    //初始化所有控件以及视图
    public void mapView(){
        edit_tip = findViewById(R.id.edit_tip);
        editInit(edit_tip);
        myKeyBoardView = findViewById(R.id.mykeyboardview);
        btn_tip = findViewById(R.id.btn_tip);
        transaction = (Transaction) getIntent().getSerializableExtra("data");
    }

    //跳过
    public void skip(View view){
        float tip = Float.parseFloat(getResult(edit_tip.getText().toString()));
        transaction.setTipAmount(tip);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("data",transaction);
        startActivity(intent);
    }

    //确认
    public void checkTip(View view){
        float tip = Float.parseFloat(getResult(edit_tip.getText().toString()));
        transaction.setTipAmount(tip);
        Intent intent = new Intent(this, MainActivity.class);
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

    protected void onRestart() {
        super.onRestart();
        myKeyBoardView.setVisibility(View.INVISIBLE);
    }
}