package com.example.a2.products;

public class Candies extends Product {
    
    private String category;
    
    public Candies(int code, String name, double cost, int qty) {
        super(code, name, cost, qty);
        this.category = "Candies";
    }

    public String getCategoryStr() {
        return this.category;
    }

    public void setCategoryStr(String category) {
        this.category = category;
    }
}