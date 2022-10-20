package com.example.mydemopos.util;


import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.MainThread;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * Created by zhoukengwen 2022/9/23
 * 专门针对美元金额输入格式化
 */
public class AmountFormatUtilWatcher implements TextWatcher {
    private EditText editText;
    private double realNum;
    public AmountFormatUtilWatcher(){

    }
    public AmountFormatUtilWatcher(EditText editText){
        this.editText = editText;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        Log.e("before", "变化起始位置" + charSequence + "start :" + start + "count:" + count + "after:" + after);
        editText.setSelection(editText.getText().toString().length());
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        Log.e("onChanged", "变化起始位置" + charSequence + "start:" + start + "before:" + before + " count:" + count);
        //删除
        if (count == 0 && before > 0) {
            //由于将double / 10 会出现小数点后多位 因为只保留两位数字
            DecimalFormat df = new DecimalFormat("#.00");
            //去掉字符串中的美元符号,以及如果有逗号了,需要将逗号也删除， 最后将字符串转化为double，再/10
            double number = Double.parseDouble(formatAmount(charSequence.toString())) / 10;
            realNum = Double.parseDouble(df.format(number));
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            editText.setText(nf.format(realNum));
            editText.setSelection(editText.getText().toString().length());
        }
        //增加
        else if (count >= 1 && before == 0) {
            //去掉字符串中的美元符号,以及如果有逗号了,需要将逗号也删除， 最后将字符串转化为double，再x10
            realNum = Double.parseDouble(formatAmount(charSequence.toString())) * 10;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            editText.setText(nf.format(realNum));
            editText.setSelection(editText.getText().toString().length());
        }
        //替换
        else if (count >= 1 && before >= 1) {
            //
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    //将美元符号以及逗号一并删除，解析为真正的数额
    public String formatAmount(String str){
        return str.replaceAll("[\\$,]", "");
    }
}
