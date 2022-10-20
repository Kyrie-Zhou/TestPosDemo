package com.example.mydemopos.activity;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydemopos.R;
import com.example.mydemopos.broadcastRev.PINBroadCastRev;
import com.example.mydemopos.entity.Transaction;
import com.example.mydemopos.service.MagCardService;
import com.example.mydemopos.service.MagCardService;
import com.example.mydemopos.util.ActivityController;
import com.example.mydemopos.util.FormatTransNoUtil;
import com.example.mydemopos.util.MyDatabaseHelper;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import okhttp3.OkHttpClient;


/*
主要信息 卡号和到期日期
 */
public class MainActivity extends AppCompatActivity{
    boolean isSwiped;//是否是刷卡或者是手动输入
    OkHttpClient client ;
    String curY;//当前年
    String curM;//当前月
    ProgressDialog progressDialog;//进度条
    MyDatabaseHelper myDatabaseHelper;
    EditText edit_cardnumber;//卡号
    EditText edit_date;//有效期
    Button btn_main;//确认按钮
    Transaction transaction;//封装的实体
    TextView txv_warning;//日期检验格式 显示下方的textView
    final String MAG_SWIP = "com.pax.intent.action.magSwip";
    //利用广播 接受服务回传的卡号信息
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case MAG_SWIP:
                    String info = intent.getStringExtra("info");
                try {
                    if(!("").equals(info)){
                        String track2s[] = info.split("=");
                        String account = track2s[0];
                        String expiry_date = track2s[1];
                        edit_date.setText(expiry_date);
                        edit_cardnumber.setText(account);
                        isSwiped = true;
                        stopService(new Intent(MainActivity.this,MagCardService.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                    break;
                default:
                    break;
            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView();//初始化
        ActivityController.addActivity(this);
        //开启服务
        Intent intent = new Intent(this, MagCardService.class);
        startService(intent);//开启服务
        registerBroadcast();//注册广播
        edit_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //增加
                if(count >= 1 && before == 0){
                    //01 - 21 肯定过期
                    Log.e("date", "onTextChanged: " + s);
                    FormatCheckDate(s.toString());
                }
                //删除
                else if(count == 0 && before >= 1){
                    txv_warning.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//
    }
    //初始化所有控件以及视图
    @MainThread
    public void mapView(){
        edit_cardnumber = findViewById(R.id.edit_cardnumber);
        edit_date = findViewById(R.id.edit_date);
        btn_main = findViewById(R.id.btn_main);
        getCurDate();
        txv_warning = findViewById(R.id.txv_warning);
        transaction = (Transaction) getIntent().getSerializableExtra("data");
        myDatabaseHelper = new MyDatabaseHelper(this,"Receipt",null);
    }

    //注册广播
    @MainThread
    public void registerBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.pax.intent.action.magSwip");
        registerReceiver(broadcastReceiver,intentFilter);
    }
    //确认按钮
    @SuppressLint("Range")
    @MainThread
    public void CheckInfo(View view) throws InterruptedException {
        String account = edit_cardnumber.getText().toString();//获得卡号
        String expiry_date = edit_date.getText().toString();//获得有效期
        transaction.setAccount(account);//添加卡号
        transaction.setExpiryDate(expiry_date);//添加日期
        if(isSwiped == true){
            transaction.setEntryMode("Swipe");//添加进入模式为 刷卡
        }else{
            transaction.setEntryMode("Manual");//添加进入模式为 手动输入
        }
        //查看数据库中最后一条记录
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Receipt", null);
        int counts = cursor.getCount();
        String transNo = "";
        if(counts == 0){
            transNo = "000001";
        }else{
            cursor.moveToPosition(cursor.getCount() - 1);//移动到最后一行
            transNo = FormatTransNoUtil.format(cursor.getString(cursor.getColumnIndex("trans_no")));//格式化自鞥
        }
        transaction.setTransNo(transNo);//添加交易号
        //直接跳转到输入安全密码界面PINActivity
        Intent intent = new Intent(this,PINActivity.class);
        intent.putExtra("data",transaction);
        startActivity(intent);
    }


    //显示弹出框
    @UiThread
    public AlertDialog.Builder showMyDialog(String titile, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titile);
        builder.setMessage(message);
        return builder;
    }


//    }
    //获得当前日期
    public void getCurDate(){
        Calendar calendar = Calendar.getInstance();
        curY = String.valueOf(calendar.get(Calendar.YEAR)).substring(2,4);
        curM = calendar.get(Calendar.MONTH) + 1 >= 10 ? String.valueOf(calendar.get(Calendar.MONTH) + 1)
                                                      : "0" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
    }

    //日期校验格式
    @MainThread
    public void FormatCheckDate(String s){
        if(s.toString().length() == 1){
            if(s.toString().charAt(0) == '1' || s.toString().charAt(0) == '0'){
                txv_warning.setText("过期了！");
                edit_date.setSelection(edit_date.getText().toString().length());
            }
        }
        else if(s.length() == 2 && Integer.parseInt(s.toString().substring(0,2)) < Integer.parseInt(curY)){
            txv_warning.setText("过期了！");
            edit_date.setSelection(edit_date.getText().toString().length());
        }
        else if(s.toString().length() == 3 && s.toString().charAt(2) != '1' && s.toString().charAt(2) != '0'){
            txv_warning.setText("月份格式不对！");
            edit_date.setSelection(edit_date.getText().toString().length());
        }
        else if(s.toString().length() == 4){
            if(Integer.parseInt(s.toString().substring(2,4)) > 12){
                txv_warning.setText("月份格式不对！");
                edit_date.setSelection(edit_date.getText().toString().length());
            }else if(s.toString().substring(0,2).equals("22") && Integer.parseInt(s.toString().substring(2,4)) <= Integer.parseInt(curM)){
                txv_warning.setText("过期了！");
                edit_date.setSelection(edit_date.getText().toString().length());
            }
        }
        else{
            txv_warning.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        stopService(new Intent(MainActivity.this,MagCardService.class));
        super.onStop();
    }

    //    @Override
//    @WorkerThread
//    public void magInfo(String info) {
//
//    }
}
