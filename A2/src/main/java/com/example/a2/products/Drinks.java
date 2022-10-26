package com.example.a2.products;

public class Drinks extends Product {

    private String category;
    
    public Drinks(int code, String name, double cost, int qty) {
        super(code, name, cost, qty);
        this.category = "Drinks";
    }

    public String getCategoryStr() {
        return this.category;
    }

    public void setCategoryStr(String category) {
        this.category = category;
    }
}
