package com.example.mydemopos.util.PIN;
 import android.util.Log;

 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Map;

/**
 * Created by zhoukengwen 2022/9/14
 * Format0 格式
 *              PIN                                             PAN
 * 总共64bit 16进制4bit位为1位 所以总共有16位        |   总共64bit 16进制4bit位为1位 所以总共有16位
 * 第一位固定是0x0  0                             |   第1 ~ 4位固定是0x0   0 0 0 0
 * 第二位是长度                                   |   第5 ~ 16位去掉最右边的一位有效数子 从右往左数12位
 * 剩下必须有14位 不足14位补0x0                     |
 */
public class Fromat0 {
    static Map<String,Integer> map = new HashMap<>();

    //将银行卡号在选择模式0的时候进行移位
    public static byte[] getDataInWith0(String PAN){
        String PANSub = PAN.substring(0, PAN.length() - 1);
        String PANField = "0000"
                +  PANSub.substring(PANSub.length() - 12,PANSub.length());
        return PANField.getBytes();
    }

    //得到pin还有pan异或的结果
    public static String format0(String PIN,String PAN){
        map.put("A",10);
        map.put("B",11);
        map.put("C",12);
        map.put("D",13);
        map.put("E",14);
        map.put("F",15);

        String PINField = "0"
                +Integer.toHexString(PIN.length())
                + String.format("%-14s", PIN).replace(' ', 'F');

        String PANSub = PAN.substring(0, PAN.length() - 1);
        String PANField = "0000"
                +  PANSub.substring(PANSub.length() - 12,PANSub.length());

        Log.e("format0",PANField + "\n" + PINField);
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < 16 ; i ++){
            int pinstr = map.containsKey(String.valueOf(PINField.charAt(i))) == true
                    ? map.get(String.valueOf(PINField.charAt(i)))
                    : Integer.parseInt(String.valueOf(PINField.charAt(i)));

            int panstr = map.containsKey(String.valueOf(PANField.charAt(i))) == true
                    ? map.get(String.valueOf(PANField.charAt(i)))
                    : Integer.parseInt(String.valueOf(PANField.charAt(i)));
            String res = (pinstr ^ panstr) > 9 ? getDecTransfer(pinstr ^ panstr) : String.valueOf(pinstr ^ panstr);
            sb.append(res);
        }
        return sb.toString();
    }

    //将十进制超过9的转化为A B C D E F
    public static String getDecTransfer(int dec){
        for(String str : map.keySet()){
            if(map.get(str) == dec){
                return str;
            }
        }
        return "";
    }

    //16进制转化成byte
    public static byte[] HextransferByte(String str){
        byte [] resByte = new byte[8];
        int i;
        for(i = 0 ; i < str.length() ; i += 2){
            int cur = Integer.parseInt(String.valueOf(str.charAt(i)));
            int next = Integer.parseInt(String.valueOf(str.charAt(i + 1)));
            int curBin[] = Dec2Bin(cur);//得到2进制数组
            int nextBin[] = Dec2Bin(next);//得到2进制数组
            int resBin[] = new int[curBin.length + nextBin.length];
            //合并为最终2进制数组
            System.arraycopy(curBin, 0, resBin, 0, curBin.length);
            System.arraycopy(nextBin, 0, resBin, curBin.length, nextBin.length);
            //将2进制转化为10进制
            StringBuilder sb = new StringBuilder();
            for(int j : resBin){
                sb.append(String.valueOf(j));
            }
            resByte[i / 2] = (byte) Integer.parseInt(sb.toString(),2);
        }
        return resByte;
    }
    //十进制转化为2进制
    public static int[] Dec2Bin(int num){
        int arr [] = new int[4];
        int idx = 0;
        String temp = Integer.toBinaryString(num);//将10进制转化为2进制
        StringBuilder sb = new StringBuilder();//用于补前导0
        //如果不足4位 差多少位 就补多少个0
        for(int i = 0 ; i < 4 - temp.length() ; i ++){
            sb.append("0");
        }
        sb.append(temp);
        //最终存到arr数组中
        for(int i = 0 ; i < sb.toString().length() ; i ++){
            arr[idx ++] = Integer.parseInt(String.valueOf(sb.toString().charAt(i)));
        }
        return arr;
    }

    //校验两个生成的pinblock是否相同
    public static boolean verify(byte [] b1,byte [] b2){
        return Arrays.equals(b1,b2);
    }
}
