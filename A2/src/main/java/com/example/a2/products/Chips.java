package com.example.a2.products;

public class Chips extends Product {
    
    private String category;
    
    public Chips(int code, String name, double cost, int qty) {
        super(code, name, cost, qty);
        this.category = "Chips";
    }

    public String getCategoryStr() {
        return this.category;
    }

    public void setCategoryStr(String category) {
        this.category = category;
    }
}