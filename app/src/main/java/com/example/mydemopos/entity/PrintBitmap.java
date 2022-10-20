package com.example.mydemopos.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by zhoukengwen 2022/10/17
 */
public class PrintBitmap implements Serializable {
    Bitmap bitmap;

    public PrintBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
