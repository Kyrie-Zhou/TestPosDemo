package com.example.mydemopos.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by zhoukengwen 2022/9/8
 * 隐藏重要卡号 中间位数用*代替 工具类
 */
public class FormatAccountUtil {
    //格式化卡号 显示*
    public static SpannableString format(String str){
        String res = "";
        int len = str.length();
        StringBuffer sb = new StringBuffer();
        for(int i = 0 ; i < len - 4; i ++){
            sb.append("*");
        }
        sb.append(str.substring(len - 4,len));
        res = sb.toString();
        SpannableString sp = new SpannableString(res);
        sp.setSpan(new AbsoluteSizeSpan(20,true),6,12, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return sp;
    }
}
