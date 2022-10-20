package com.example.mydemopos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydemopos.R;
import com.example.mydemopos.activity.adapter.TransactionAdapter;
import com.example.mydemopos.entity.TransList;
import com.example.mydemopos.entity.Transaction;
import com.example.mydemopos.util.FormatAccountUtil;
import com.example.mydemopos.util.MyDatabaseHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhoukengwen 2022/10/9
 * 展示所有transaction数据 使用listView显示
 */
public class AllRecordsActivity extends AppCompatActivity {
    ListView listView;
    TextView txv_total;
    List<TransList> transLists = new ArrayList<TransList>();
    SQLiteDatabase db;
    MyDatabaseHelper myDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_records);
        mapView();//初始化所有视图
        initTransLists();//初始化listview中的数据
        TransactionAdapter arrayAdapter = new TransactionAdapter(this,0,R.layout.records_list,transLists);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            //根据id查询记录
            int Id = (int) (id + 1);
            getResById(Id);
        });
    }

    //各种初始化操作
    public void mapView(){
        listView = findViewById(R.id.all_records);
        txv_total = findViewById(R.id.txv_total);
        myDatabaseHelper = new MyDatabaseHelper(this,"Receipt",null);

    }

    //初始化数据 为listView中每一个item初始化数据，从数据库中查询
    @SuppressLint("Range")
    public void initTransLists(){
        //查询数据库
        db = myDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.query("Receipt", null,null,null,null,null,null);
        String totalCount = cursor.getCount() == 0 ? "暂无" : String.valueOf(cursor.getCount());
        txv_total.setText(totalCount);
        if(cursor.moveToFirst()){
            do{
                String transNo = cursor.getString(cursor.getColumnIndex("trans_no"));
                Float base_amount = cursor.getFloat(cursor.getColumnIndex("base_amount"));
                String response = cursor.getString(cursor.getColumnIndex("response"));
                Integer imageId = R.mipmap.list_img_item;
                TransList transList = new TransList(imageId,transNo,base_amount,response);
                transLists.add(transList);
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                Log.e("id","" + id);
            }while (cursor.moveToNext());
        }else{
            cursor.close();
        }
    }

    //根据id查询数据
    @SuppressLint("Range")
    public void getResById(int id){
        Cursor cursor = db.rawQuery("select * from Receipt where id = ?", new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            String transNo = cursor.getString(cursor.getColumnIndex("trans_no"));
            String account = cursor.getString(cursor.getColumnIndex("account"));
            String entry_mode = cursor.getString(cursor.getColumnIndex("entry_mode"));
            Float base_amount = cursor.getFloat(cursor.getColumnIndex("base_amount"));
            Float tip_amount = cursor.getFloat(cursor.getColumnIndex("tip_amount"));
            Float total_amount = cursor.getFloat(cursor.getColumnIndex("total_amount"));
            String expiry_date = cursor.getString(cursor.getColumnIndex("expiry_date"));
            String authCode = cursor.getString(cursor.getColumnIndex("auth_code"));
            String response = cursor.getString(cursor.getColumnIndex("response"));
            Transaction transaction = new Transaction(transNo,account,entry_mode,base_amount,tip_amount,total_amount,expiry_date,authCode,response);
            showDialogWithTable(transaction);
        }
        cursor.close();
    }

    //展示弹出框带有transaction表格数据 用于显示点击listview中的每一个item数据
    public void showDialogWithTable(Transaction transaction){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("数据显示")
               .setPositiveButton("确定", (dialog1, which) -> dialog1.dismiss());
        //获取自定义的显示数据布局
        View dialogView = View.inflate(AllRecordsActivity.this, R.layout.table_transaction, null);
        TextView transNo = dialogView.findViewById(R.id.txv_transNo);//交易号
        TextView account = dialogView.findViewById(R.id.txv_account);//卡号
        TextView entryMode = dialogView.findViewById(R.id.txv_entry);//进入模式
        TextView baseAmount = dialogView.findViewById(R.id.txv_baseAmount);//基本金额
        TextView tip = dialogView.findViewById(R.id.txv_tip);//小费
        TextView totalAmount = dialogView.findViewById(R.id.txv_totalAmount);//总价
        TextView expiryDate = dialogView.findViewById(R.id.txv_expiryDate);//有效日期
        TextView authCode = dialogView.findViewById(R.id.txv_auth);//授权码
        TextView response = dialogView.findViewById(R.id.txv_res);//响应信息
        transNo.setText(transaction.getTransNo());
        account.setText(FormatAccountUtil.format(transaction.getAccount()));
        entryMode.setText(transaction.getEntryMode());
        baseAmount.setText(String.valueOf(transaction.getBaseAmount()));
        tip.setText(String.valueOf(transaction.getTipAmount()));
        totalAmount.setText(String.valueOf(transaction.getTotalAmount()));
        expiryDate.setText(transaction.getExpiryDate());
        authCode.setText(transaction.getAuthCode());
        response.setText(transaction.getResponse());
        //设置对话框布局
        builder.setView(dialogView);
        //数据准备好 显示
        builder.show();

    }
}