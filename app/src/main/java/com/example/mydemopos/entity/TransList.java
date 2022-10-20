package com.example.mydemopos.entity;
/**
 * Created by zhoukengwen 2022/10/10
 * 封装listview中每一个item数据的实体类
 */
public class TransList {
    private int imageId;
    private String transNo;
    private Float baseAmount;
    private String response;

    public TransList(int imageId, String transNo, Float baseAmount,String response) {
        this.imageId = imageId;
        this.transNo = transNo;
        this.baseAmount = baseAmount;
        this.response = response;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public void setBaseAmount(Float baseAmount) {
        this.baseAmount = baseAmount;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTransNo() {
        return transNo;
    }

    public Float getBaseAmount() {
        return baseAmount;
    }

    public String getResponse() {
        return response;
    }
}
