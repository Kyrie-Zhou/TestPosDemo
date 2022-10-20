package com.example.mydemopos.constant;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.example.mydemopos.R;

/**
 * Created by zhoukengwen 2022/9/26
 */
public class MyKeyBoardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {
    //自定义键盘布局
    private Keyboard myKeyboard;

    //绑定的输入框
    private EditText myEditText;


    public MyKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyKeyBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyKeyBoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        myKeyboard = new Keyboard(getContext(), R.xml.custom_keyboard);
        //使用数字键盘
        setKeyboard(myKeyboard);
        //是否启用预览
        setPreviewEnabled(false);
        //键盘动作监听
        setOnKeyboardActionListener(this);
    }

    public void setEditText(EditText editText){
        this.myEditText = editText;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {
    }



    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        Editable editable = myEditText.getText();
        int start = myEditText.getSelectionStart();
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE://删除
                if (editable != null && editable.length() > 0 && start > 0) {
                    editable.delete(start - 1, start);
                }
                break;
            case Keyboard.KEYCODE_DONE://完成
                //收起键盘
                this.setVisibility(INVISIBLE);
                //同时将让输入框失去焦点
                myEditText.clearFocus();
                break;
            default:
                editable.insert(start, Character.toString((char) primaryCode));
                break;
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
