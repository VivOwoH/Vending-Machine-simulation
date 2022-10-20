package com.example.a2;

import java.util.*;

import com.example.a2.products.Product;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class VendingMachine {
    private List<Product> productInventory;
    private List<Currency> currencyInventory;
    private DBManage database;
    private HashMap<Integer,Integer> cart = new HashMap<>(); // Map<prodID,qty>
    public final static String[] categories = {"Drinks", "Chocolates", "Chips", "Candies"}; // pre-defined; can't be modified
    public final static String[] denominations = {"5c", "10c", "20c", "50c", "1d", "2d", "5d", "10d", "20d", "50d", "100d"};
    public final static String[] products = {"water", "sprite", "coke", "pepsi", "juice",
                                        "mars", "m&m", "bounty", "snicker",
                                        "smiths", "pringles", "kettles", "thins",
                                        "mentos", "sourpatch", "skittles"};
    public static Map<String, ArrayList<String>> productMap = null;

    private Timer idleTimer;
    private TimerTask cancelTransactionTask;
    private long idleLimit = 2000;//120000;
    // private Alert alert;

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

    // ------------------------------------------
    public VendingMachine(DBManage database) {
        this.database = database;
        updateProductInventory();

        idleTimer = new Timer("idle timer");
        // alert = new Alert(AlertType.INFORMATION);
        cancelTransactionTask = new TimerTask() {
            public void run() {
                clearCart();

                ImageIcon icon = new ImageIcon(getClass().getResource("/alert.png"));
                icon = new ImageIcon(icon.getImage().getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH));
                JOptionPane.showMessageDialog(null, "No activity for too long. Transaction cancelled.", "Alert", JOptionPane.INFORMATION_MESSAGE, icon);
            }
        };
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

    public void triggerTimer() {
        if (!cart.isEmpty()) {
            idleTimer.cancel();
        }
        idleTimer.schedule(cancelTransactionTask, idleLimit);
    }

    // ---------------------------
    // -------- Cart -------------
    // ---------------------------
    
    // Button(prodID) + input qty -> addToCart
    public void addToCart(int prodID, int qty) {
        this.cart.put(prodID, qty);
    }

    public void clearCart() {
        this.cart.clear();
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
