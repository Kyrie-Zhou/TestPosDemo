package com.example.mydemopos.entity;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String transNo;//交易编号
    private String entryMode;//进入模式【手动 刷卡】
    private String account;//卡号
    private Float baseAmount;//基础金额
    private Float tipAmount;//小费金额
    private Float totalAmount;//总价
    private String expiryDate;//到期日期
    private String authCode;//授权码
    private String response;//响应数据

    public Transaction() {
    }

    public Transaction(String transNo, String account,String entryMode, Float baseAmount, Float tipAmount, Float totalAmount, String expiryDate, String authCode, String response) {
        this.transNo = transNo;
        this.account = account;
        this.entryMode = entryMode;
        this.baseAmount = baseAmount;
        this.tipAmount = tipAmount;
        this.totalAmount = totalAmount;
        this.expiryDate = expiryDate;
        this.authCode = authCode;
        this.response = response;
    }


    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setEntryMode(String entryMode) {
        this.entryMode = entryMode;
    }

    public void setBaseAmount(Float baseAmount) {
        this.baseAmount = baseAmount;
    }

    public void setTipAmount(Float tipAmount) {
        this.tipAmount = tipAmount;
    }

    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTransNo() {
        return transNo;
    }

    public String getEntryMode() {
        return entryMode;
    }

    public String getAccount() {
        return account;
    }

    public Float getBaseAmount() {
        return baseAmount;
    }

    public Float getTipAmount() {
        return tipAmount;
    }

    public Float getTotalAmount() {
        return totalAmount;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getAuthCode() {
        return authCode;
    }

    public String getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transNo='" + transNo + '\'' +
                ", account='" + account + '\'' +
                ", baseAmount=" + baseAmount +
                ", tipAmount=" + tipAmount +
                ", totalAmount=" + totalAmount +
                ", expiryDate='" + expiryDate + '\'' +
                ", authCode='" + authCode + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
