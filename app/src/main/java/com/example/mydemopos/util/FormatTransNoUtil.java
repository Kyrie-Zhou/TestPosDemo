package com.example.mydemopos.util;

/**
 * Created by zhoukengwen 2022/9/13
 * 对TransNo 交易号进行格式化 【000001 ~ 999999】6位
 */
public class FormatTransNoUtil {
    public static String format(String str){
        String pre = "";//前导0
        String next = "";//后半部分
        String temp = "";//临时字符串
        int len = str.length();
        int i = 0 ;
        //判断有多少个前导0
        while(i < len  && str.charAt(i) == '0'){
            i ++;
        }
        temp = String.valueOf(Integer.parseInt(str.substring(i, len)));
        next = String.valueOf(Integer.parseInt(str.substring(i, len)) + 1);
        pre = String.valueOf(Integer.parseInt(next)).length() > temp.length() ? str.substring(0,i - 1) : str.substring(0,i);
        return pre + next;
    }
}
