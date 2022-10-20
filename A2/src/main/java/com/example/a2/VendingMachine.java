package com.example.a2;

import java.util.*;

import com.example.a2.products.Product;

public class VendingMachine {
    private List<Product> productInventory;
    private List<Currency> currencyInventory;
    private HashMap<Integer,Integer> cart = new HashMap<>(); // Map<prodID,qty>
    public final static String[] categories = {"Drinks", "Chocolates", "Chips", "Candies"}; // pre-defined; can't be modified
    public final static String[] denominations = {"5c", "10c", "20c", "50c", "1d", "2d", "5d", "10d", "20d", "50d", "100d"};
    public final static String[] products = {"water", "sprite", "coke", "pepsi", "juice",
                                        "mars", "m&m", "bounty", "snicker",
                                        "smiths", "pringles", "kettles", "thins",
                                        "mentos", "sourpatch", "skittles"
    };
    public static Map<String, ArrayList<String>> productMap = null;
    static {
        Map<String, ArrayList<String>> aMap = new HashMap<>();
        for(int i = 0; i < 4; i++){
            aMap.put(categories[i], new ArrayList<String>());
        }
        for(int i = 0; i < 5; i++){
            ArrayList<String> current = aMap.get(categories[0]);
            current.add(products[i]);
        }
        for(int i = 5; i < 9; i++){
            ArrayList<String> current = aMap.get(categories[1]);
            current.add(products[i]);
        }
        for(int i = 9; i < 13; i++){
            ArrayList<String> current = aMap.get(categories[2]);
            current.add(products[i]);
        }
        for(int i = 13; i < 16; i++){
            ArrayList<String> current = aMap.get(categories[3]);
            current.add(products[i]);
        }
        productMap = Collections.unmodifiableMap(aMap);
    }
    private DBManage database;

    public VendingMachine(DBManage database) {
        this.database = database;
        updateProductInventory();
    }

    public void updateProductInventory() {
        this.productInventory = database.getProducts();
    }

    public Product findProductByID(int prodID) {
        for (Product product : this.productInventory) {
            if (product.getCode() == prodID)
                return product;
        }
        return null;
    }

    public List<Product> ShowProductCategorized(String category) {
        List<Product> resultSet = new ArrayList<Product>();
        for (Product product : this.productInventory) {
            if (product.getCategoryStr().equals(category)) {
                resultSet.add(product);
            }
        }
        return resultSet;
    }

    // ---------------------------
    // -------- Cart -------------
    // ---------------------------
    
    // Button(prodID) + input qty -> addToCart
    public void addToCart(int prodID, int qty) {
        this.cart.put(prodID, qty);
    }

    // ---------------------------
    // ----- SETTER/GETTER -------
    // ---------------------------

    public String[] getCategories() {
        return this.categories;
    }

    public List<Product> getProductInventroy() {
        return this.productInventory;
    }

    public List<Currency> getCurrencyInventory() {
        return this.currencyInventory;
    }

    public HashMap<Integer,Integer> getCart() {
        return this.cart;
    }
}
