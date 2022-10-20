package com.example.mydemopos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.mydemopos.R;
import com.example.mydemopos.util.ActivityController;
import com.example.mydemopos.util.PIN.Fromat0;
import com.pax.api.BaseSystemException;
import com.pax.api.BaseSystemManager;
import com.pax.api.PedException;
import com.pax.api.PedManager;
import com.pax.api.model.DEVICE_INFO;

import java.lang.reflect.Method;

public class HomeActivity extends AppCompatActivity {
    PopupMenu mypopup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.setTitle("HOME");

        String [] data = {
                "brief introduction",
                "transaction",
                "version",
                "export uri"
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,data);
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((parent,view,position,id)-> {
            switch (position){
                case 1:
                    createMenu(view);
                    break;

                case 2:
                    Toast.makeText(this, "SDK: " + android.os.Build.VERSION.SDK, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Transaction exports");
                    dialog.setMessage("Transaction uri has been exported");
                    dialog.show();
//                    //还未授权
//                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
//                        ActivityCompat.requestPermissions(this,new String[]{
//                                Manifest.permission.CALL_PHONE
//                        },1);
//                    }
//                    //已经授权
//                    else{
//                        startActivity(new Intent(this,DisplayInfoActivity.class));
//                    }
//                    break;
                default:
                    Toast.makeText(this, data[position] , Toast.LENGTH_SHORT).show();
            }
        });
    }

    //创建菜单
    public void createMenu(View view){
        mypopup = new PopupMenu(this,view);
        getMenuInflater().inflate(R.menu.mymenu,mypopup.getMenu());
        mypopup.setOnMenuItemClickListener((MenuItem menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.enter_transaction:
                    startActivity(new Intent(this,SaleActivity.class));
                    break;
                case R.id.select_transaction:
                    startActivity(new Intent(this,DisplayInfoActivity.class));
                    break;
                case R.id.records_transaction:
                    startActivity(new Intent(this,AllRecordsActivity.class));
                    break;
                default:
                    Toast.makeText(this,"你点击了" + menuItem.getTitle(),Toast.LENGTH_LONG);
                    break;
            }
            return true;
        });
        mypopup.show();
        setIconEnable(mypopup.getMenu(), true);
    }

    //enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
    private void setIconEnable(Menu menu, boolean enable)
    {
        try
        {
            @SuppressLint("PrivateApi") Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            @SuppressLint("DiscouragedPrivateApi") Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征
            m.invoke(menu, enable);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //授权回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(this,DisplayInfoActivity.class));
                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();

                }
                break;
            default:
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("startmsg-main", "MainActivity onStArt: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("resumemsg-main", "MainActivity onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("pasusemsg-main", "MainActivity onPause: ");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("stopmsg-main", "MainActivity onStop: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        this.finish();
        Log.d("restartmsg-main", "MainActivity oRestart: ");
    }

}