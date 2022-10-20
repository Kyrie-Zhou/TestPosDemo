package com.example.mydemopos.activity;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mydemopos.R;
import com.example.mydemopos.util.ActivityController;
import com.example.mydemopos.util.FormatAccountUtil;
import com.example.mydemopos.util.MyDatabaseHelper;
/*
展示Receipt
 */
public class DisplayInfoActivity extends AppCompatActivity {

    Bitmap bitmap;
    byte[] bytes;
    ImageView img_dis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);
        ActivityController.addActivity(this);
        mapView();//初始化
        ViewResolver();//视图解析
    }
    //初始化所有控件以及视图
    public void mapView(){
        img_dis = (ImageView) findViewById(R.id.img_dis);
        bytes = getIntent().getByteArrayExtra("bitmap");
        if(bytes != null){
            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//        因为Bitmap本身实现了Parcelable接口
//        bitmap = (Bitmap) getIntent().getParcelableExtra("bitmap");
        }
    }

    //将数据渲染到对应的控件上
    @SuppressLint("Range")
    public void ViewResolver(){
        if(bytes != null){
            Matrix matrix = new Matrix(); //接收图片之后放大2倍
            matrix.postScale(2.0f, 2.0f);
            Bitmap bitmap_large = Bitmap.createBitmap(this.bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            img_dis.setImageBitmap(bitmap_large);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("startmsg-dis", "DisplayActivity onStArt: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("resumemsg-dis", "DisplayActivity onResume: ");
    }


    @Override
    protected void onPause() {
        super.onPause();
        for (Activity activity : ActivityController.getAllActivity()) {
            activity.finish();
        }
        Log.d("pausemsg-dis", "DisplayActivity onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("stopmsg-dis", "DisplayActivity onStop: ");
    }

    //栈顶出栈 必定destory
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("destorymsg-dis","DisplayActivity onDestory");
    }

    protected void onRestart() {
        super.onRestart();
//        this.finish();
        Log.d("restartmsg-dis", "DisplayActivity onRestart: ");
    }

    @Override
    @MainThread
    //重写返回键事件 点击返回直接跳转到首页
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}