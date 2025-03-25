package com.example.demo.warehouse;

public class MissingOrderItem {

    private String code;
    private int amount;

    public MissingOrderItem() {
    }

    public MissingOrderItem(String code, int amount) {
        this.code = code;
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}