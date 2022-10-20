package com.example.mydemopos.activity;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydemopos.R;
import com.example.mydemopos.api.transaction.HttpApi;
import com.example.mydemopos.broadcastRev.PINBroadCastRev;
import com.example.mydemopos.entity.FontSize;
import com.example.mydemopos.entity.Transaction;
import com.example.mydemopos.util.FormatAccountUtil;
import com.example.mydemopos.util.JsonFormat;
import com.example.mydemopos.util.MyDatabaseHelper;
import com.example.mydemopos.util.PIN.Fromat0;
import com.pax.api.BaseSystemManager;
import com.pax.api.PedException;
import com.pax.api.PedManager;
import com.pax.api.PrintException;
import com.pax.api.PrintManager;
import com.pax.gl.page.IPage;
import com.pax.gl.page.PaxGLPage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by $｛Bob｝ $｛2022/9/23｝
 */
public class TestActivityPin extends AppCompatActivity implements PINBroadCastRev.ChannelRecv {
    ProgressDialog progressDialog;//进度条圆圈
    TextView txv_pin;
    Transaction transaction;
    OkHttpClient client ;
    MyDatabaseHelper myDatabaseHelper;
    PINBroadCastRev pinBroadCastRev;//注册的广播
    byte [] pinblock;//安全键盘输入密码后加密的字节数组
    final static int NETWORK_TIMEOUT = 0;
    final static int CONFIRM_PINBLOCK = 1;
    final static int PRINT_EXCEPTION = 2;
    Handler handler ;
    static class MyHandler extends Handler {
        WeakReference<TestActivityPin> mWeakReference;
        public MyHandler(TestActivityPin activity) {
            mWeakReference = new WeakReference<TestActivityPin>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            TestActivityPin activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what){
                    case NETWORK_TIMEOUT://网络超时
                        activity.progressDialog.dismiss();
                        activity.showMyDialog("出错","网络连接超时，请稍后重试...").show();
                        break;

                    case CONFIRM_PINBLOCK:
                        activity.progressDialog.setTitle("交易提示");
                        activity.progressDialog.setMessage("交易处理中,请稍等...");
                        activity.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        activity.progressDialog.setCancelable(false);//在转圈的时候 禁止点击返回键
                        activity.progressDialog.show();
                        break;

                    case PRINT_EXCEPTION:
                        activity.showMyDialog("出错","打印机发生错误，请检查后后重试...").show();
                        break;

                    default:
                        break;
                }
            }
        }
    }
    @MainThread
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pin);
        mapView();//初始化各种资源
        registerBroadcast();//注册广播
        new Thread(() -> {
            PedManager pedManager = null;
            try {
                pedManager= PedManager.getInstance();
                pedManager.pedGetPinBlock((byte) 2, "4,6", Fromat0.getDataInWith0(transaction.getAccount()), (byte) 0x00, 15000);
                //同步方法 只有当点击确认的时候
                confirmPinBlock();
            } catch (PedException e) {
                e.printStackTrace();
                showMyDialog("警告","长时间未输入，关闭交易，请重试！").show();
                finish();
            }
        }).start();
    }

    //各种初始化操作
    @MainThread
    public void mapView(){
        transaction = (Transaction) getIntent().getSerializableExtra("data");
        txv_pin = findViewById(R.id.txv_pin);
        myDatabaseHelper = new MyDatabaseHelper(this,"Receipt",null);
        progressDialog = new ProgressDialog(this);
        handler = new MyHandler(this);
    }

    //注册广播
    @MainThread
    public void registerBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.pax.intent.action.PINPEDEVENT");
        pinBroadCastRev = new PINBroadCastRev();
        registerReceiver(pinBroadCastRev,intentFilter);
        pinBroadCastRev.setChannelRecv(this);
    }

    //pin确认
    @WorkerThread
    @SuppressLint("Range")
    public void confirmPinBlock(){
        //输入完安全密码后，直接发起网络请求，将数据发送到后台，回显数据并且添加到数据库
        transactionSubmit();
    }

    @MainThread
    @Override
    public void recv(byte msg) {
        switch (msg){
            //按下 按钮
            case 42:
                txv_pin.append("*");
                break;

            //确认 按钮
            case 13:

                break;

            //删除 按钮
            case 8:
                txv_pin.setText("");
                break;

            //取消 按钮
            default:
                AlertDialog.Builder builder = showMyDialog("警告", "交易已取消！");
                builder.setPositiveButton("确定", (dialog, which) -> startActivity(new Intent(TestActivityPin.this,HomeActivity.class)));
                builder.show();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //发起网络请求，将数据发送到后台，回显数据并且添加到数据库
    @WorkerThread
    private void transactionSubmit() {
        try {
            //向主线程发送消息 要显示一个progressDialog
            Message message = new Message();
            message.what = CONFIRM_PINBLOCK;
            handler.sendMessage(message);
            Thread.sleep(2000);
            //将transaction【交易编号 卡号 进入模式 基本金额 小费 到期日期】转化为为json字符串发送给服务器
            Map<String,String> map = new HashMap<String,String>();
            map.put("transNo",transaction.getTransNo());
            map.put("account",transaction.getAccount());
            map.put("entryMode",transaction.getEntryMode());
            map.put("baseAmount",String.valueOf(transaction.getBaseAmount()));
            map.put("tipAmount",String.valueOf(transaction.getTipAmount()));
            map.put("expiryDate",transaction.getExpiryDate());
            //发送网络请求 并设置连接超时时间5秒
            client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
            Request request = HttpApi.submitTransaction(map,2);
            //接受响应数据 autoCode、response
            Response response = client.newCall(request).execute();
            String data = response.body().string();//此时是json字符串对象
            String [] responseKeys = {"authCode","response"};

            Object[] objects = JsonFormat.parseJSONWithJSONObject(data, responseKeys);
            transaction.setAuthCode(String.valueOf(objects[0]));//将回显的auth_code添加到transaction中
            transaction.setResponse(String.valueOf(objects[1]));//将回显的response添加到transaction中
            formatTransaction(transaction);//添加到数据库
            //数据成功添加之后 ，将结果打印出来
            //先判断当前设备是否内置打印功能
            byte isSupport = BaseSystemManager.getInstance().getDeviceInfo().isPrinterSupport;
            //不支持打印功能
            if (isSupport == 0){
                Bitmap bitmap = getPrintBitmap();//得到视图页面
                byte[] bytes = compressBitmapToByteArray(bitmap);//压缩bitmap转化为byte[]
                Intent intent = new Intent(this,DisplayInfoActivity.class);
//                    本来因为Bitmap实现Parcelable，直接传递，但是由于bitmap有点大，传递的时候出现跳转不过去，所以只好转化为byte[]进行压缩
//                    intent.putExtra("bitmap",bitmap);
                intent.putExtra("bitmap",bytes);
                progressDialog.dismiss();//根据业务自身处理 时间加载
                //跳转到DisplayInfoActivity
                startActivity(intent);
            }
            //支持打印功能
            else{
                print();
                progressDialog.dismiss();//根据业务自身处理 时间加载
                //打印成功，跳转到MainActivity
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            //网络异常
            if(e instanceof IOException){
                //向main主线程发送消息 网络超时 要显示一个alertDialog
                Message message1 = new Message();
                message1.what = NETWORK_TIMEOUT;
                handler.sendMessage(message1);
            }
            //打印机异常
            else{
                //向main主线程发送消息 网络超时 要显示一个alertDialog
                Message message1 = new Message();
                message1.what = PRINT_EXCEPTION;
                handler.sendMessage(message1);
            }
            e.printStackTrace();
        }
    }

    //自定义显示dialog
    @UiThread
    public AlertDialog.Builder showMyDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        return builder;
    }

    //将transaction对象进行解析 获取字段信息添加到数据库中
    @WorkerThread
    public void formatTransaction(Transaction transaction){
        //获取交易金额
        Float baseAmount = transaction.getBaseAmount();
        //获取小费
        Float tipAmount = transaction.getTipAmount() ;
        //获取总价
        Float totalAmount = baseAmount + tipAmount;
        transaction.setTotalAmount(totalAmount);
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        db.execSQL("insert into Receipt (trans_no,account,entry_mode,base_amount,tip_amount,total_amount,expiry_date,auth_code,response) values (?,?,?,?,?,?,?,?,?)",
                new Object[]{
                        transaction.getTransNo()
                        ,transaction.getAccount()
                        ,transaction.getEntryMode()
                        ,transaction.getBaseAmount()
                        ,transaction.getTipAmount()
                        ,transaction.getTotalAmount()
                        ,transaction.getExpiryDate()
                        ,transaction.getAuthCode()
                        ,transaction.getResponse()
                });
    }

    //打印
    @WorkerThread
    public void print() throws PrintException {
        Bitmap bitmap = getPrintBitmap();
        PrintManager pm = PrintManager.getInstance(this);
        pm.prnSetGray(3);//设置灰度等级
        //打印器 打印
        pm.prnInit();//打印机初始化
        pm.prnBitmap(bitmap);//打印位图
        pm.prnStart();//打印结果
    }

    //绘制打印页面，抽离出来可以进行页面显示，不用再次自定义创建activity
    public Bitmap getPrintBitmap(){
        PaxGLPage paxGLPage = PaxGLPage.getInstance(this);
        IPage page = paxGLPage.createPage();//创建空页面
        IPage.ILine appreciation = page.addLine().addUnit("Thank you", FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT);//创建行【Thank you】
        page.addLine().addUnit(" ",FontSize.MEDIUM.getSize());//创建空行
        page.addLine().addUnit(" ",FontSize.MEDIUM.getSize());//创建空行
        page.addLine().addUnit(" ",FontSize.MEDIUM.getSize());//创建空行
        IPage.ILine tnLine = page.addLine()     //创建行【Trans. No.】
                .addUnit("Trans. No.：", FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT)
                .addUnit(transaction.getTransNo(),FontSize.MEDIUM.getSize(), IPage.EAlign.RIGHT);
        page.addLine().addUnit(" ",FontSize.TINY.getSize());//创建空行

        //创建行【Sale】
        IPage.ILine saleLine = page.addLine().addUnit("Sale",FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT);
        page.addLine().addUnit(" ",FontSize.TINY.getSize());//创建空行

        //创建行【account】
        SpannableString format = FormatAccountUtil.format(transaction.getAccount());//隐藏部分 用*替换
        String account = format.toString();
        IPage.ILine accountLine = page.addLine()
                .addUnit("Account：",FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT)
                .addUnit(account,FontSize.MEDIUM.getSize(), IPage.EAlign.RIGHT);
        page.addLine().addUnit(" ",FontSize.TINY.getSize());//创建空行

        //创建行【entry】
        IPage.ILine Entry = page.addLine()
                .addUnit("Entry：",FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT)
                .addUnit(transaction.getEntryMode(),FontSize.MEDIUM.getSize(), IPage.EAlign.RIGHT);
        page.addLine().addUnit(" ",FontSize.TINY.getSize());//创建空行

        //创建行【baseAmount】
        IPage.ILine baseLine = page.addLine()
                .addUnit("Base Amount：",FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT)
                .addUnit(String.valueOf(transaction.getBaseAmount()),FontSize.MEDIUM.getSize(), IPage.EAlign.RIGHT);
        page.addLine().addUnit(" ",FontSize.TINY.getSize());//创建空行

        //创建行【tipAmount】
        IPage.ILine tipLine = page.addLine()
                .addUnit("Tip Amount：",FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT)
                .addUnit(String.valueOf(transaction.getTipAmount()),FontSize.MEDIUM.getSize(), IPage.EAlign.RIGHT);
        page.addLine().addUnit(" ",FontSize.TINY.getSize());//创建空行

        //创建行【totalAmount】
        IPage.ILine totalLine = page.addLine()
                .addUnit("Total Amount：",FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT)
                .addUnit(String.valueOf(transaction.getTotalAmount()),FontSize.MEDIUM.getSize(), IPage.EAlign.RIGHT);
        page.addLine().addUnit(" ",FontSize.TINY.getSize());//创建空行

        //创建行【expiryDate】
        IPage.ILine dateLine = page.addLine()
                .addUnit("Expiry Date：",FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT)
                .addUnit(transaction.getExpiryDate(),FontSize.MEDIUM.getSize(), IPage.EAlign.RIGHT);
        page.addLine().addUnit(" ",FontSize.TINY.getSize());//创建空行

        //创建行【authCode】
        IPage.ILine authLine = page.addLine()
                .addUnit("Auth Code：",FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT)
                .addUnit(transaction.getAuthCode(),FontSize.MEDIUM.getSize(), IPage.EAlign.RIGHT);
        page.addLine().addUnit(" ",FontSize.TINY.getSize());//创建空行

        //创建行【response】
        IPage.ILine resLine = page.addLine()
                .addUnit("Response：",FontSize.MEDIUM.getSize(), IPage.EAlign.LEFT)
                .addUnit(transaction.getResponse(),FontSize.MEDIUM.getSize(), IPage.EAlign.RIGHT);

        page.addLine().addUnit(" ",FontSize.MEDIUM.getSize());//创建空行
        page.addLine().addUnit(" ",FontSize.MEDIUM.getSize());//创建空行
        page.addLine().addUnit(" ",FontSize.MEDIUM.getSize());//创建空行
        page.addLine().addUnit(" ",FontSize.MEDIUM.getSize());//创建空行
        page.addLine().addUnit(" ",FontSize.MEDIUM.getSize());//创建空行
        page.addLine().addUnit(" ",FontSize.MEDIUM.getSize());//创建空行

        //page页面丰富完毕 开始转化成BitMap
        Bitmap bitmap = paxGLPage.pageToBitmap(page, 384);
        return bitmap;
    }

    //将bitmap进行压缩转化成byte[]
    public byte[] compressBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    @MainThread
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pinBroadCastRev);
    }

}