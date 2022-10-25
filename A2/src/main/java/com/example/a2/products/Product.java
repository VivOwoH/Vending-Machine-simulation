package com.example.a2.products;

public abstract class Product {
    protected int code;
    protected String name;
    protected double cost;
    protected int qty;

    public Product(int code, String name, double cost, int qty) {
        this.code = code;
        this.name = name;
        this.cost = cost;
        this.qty = qty;
    }

    public abstract String getCategoryStr();
    public abstract void setCategoryStr(String category);

    public void setCode(int code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getCode() {
        return this.code;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public int getQty() {
        return this.qty;
    }
    
}
