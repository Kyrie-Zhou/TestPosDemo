package com.example.mydemopos.broadcastRev;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by zhoukengwen 2022/9/15
 * 监听PIN键盘输入时候发出的广播
 */
public class PINBroadCastRev extends BroadcastReceiver {

    ChannelRecv channelRecv;
    public static final String PIN_INPUT = "com.pax.intent.action.PINPEDEVENT";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("广播","监听到广播");
        switch (intent.getAction()){
            case PIN_INPUT:
                Log.e("pintype",""+intent.getStringExtra("status"));
                if(intent.getStringExtra("status").equals("PIN_CONTENT")){
                    Bundle extras = intent.getExtras();
                    byte data = extras.getByte("data");
                    Log.e("data",""+data);
                    channelRecv.recv(data);
                }
                break;
            default:
                break;
        }

    }

    //通过该接口定义的方法 将键盘按下的每一个byte发送给PinActivity
    public interface ChannelRecv{
        public void recv(byte msg);
    }

    //setter
    public void setChannelRecv(ChannelRecv channelRecv) {
        this.channelRecv = channelRecv;
    }
}
