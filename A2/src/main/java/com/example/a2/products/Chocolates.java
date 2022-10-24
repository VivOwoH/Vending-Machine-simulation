package com.example.a2.products;

public class Chocolates extends Product {
    
    private String category;
    
    public Chocolates(int code, String name, double cost, int qty) {
        super(code, name, cost, qty);
        this.category = "Chocolates";
    }

    public String getCategoryStr() {
        return this.category;
    }

    public void setCategoryStr(String category) {
        this.category = category;
    }
}