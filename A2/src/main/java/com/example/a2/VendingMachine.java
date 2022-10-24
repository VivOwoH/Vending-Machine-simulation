package com.example.a2;

import java.math.BigDecimal;
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
    public final static String[] denominations = {"100", "50", "20", "10", "5", "2", "1", "0.5", "0.2", "0.1", "0.05"};
    public final static String[] products = {"water", "sprite", "coke", "pepsi", "juice",
                                        "mars", "m&m", "bounty", "snicker",
                                        "smiths", "pringles", "kettles", "thins",
                                        "mentos", "sourpatch", "skittles"};
    public static Map<String, ArrayList<String>> productMap = null;

    private Timer idleTimer;
    private TimerTask cancelTransactionTask;
    private long idleLimit = 120000;
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
    // -------- Money ------------
    // ---------------------------

    public HashMap<Double, Integer> changeCalc(double amount){
        HashMap<Double, Integer> result = new HashMap<>();

        for(String stringRep:this.denominations) {
            double denomination = Double.parseDouble(stringRep);
            System.out.printf("%s %s\n", denomination, amount);
                if (!(amount % denomination == amount)) {
                    double without_remainder = amount - (amount % denomination);
                    double amount_denom = without_remainder / denomination;
                    result.put(denomination, (int) amount_denom);

                    amount = amount - (amount_denom * denomination);
                    if(amount > 0 && amount < 0.05){ //rounding error
                        amount = 0.05;
                    }
                }
        }

        return result;
    }

    /**
     * Asks database for change according to optimal config calculated via changeCalc function.
     * Updates the amount of change left to cover and the change given accordingly.
     * @param change
     * @return updated change left and change given HashMaps
     */
    public ArrayList<HashMap<Double, Integer>> requestChange(HashMap<Double, Integer> change){
        HashMap<Double, Integer> actualChange = new HashMap<>();
        ArrayList<HashMap<Double, Integer>> result = new ArrayList<>();
        result.add(change);
        result.add(actualChange);

        for(Double key : change.keySet()) {
            //find the denomination in SQL
            int quantity = database.getCurrencyQuantity(key);

            if (quantity >= change.get(key)) {
                Integer difference = quantity - change.get(key);
                database.updateCurrency(key, difference);
                actualChange.put(key, change.get(key));
                change.put(key, 0);
            }
	        else { //need to make up the leftover notes
                database.updateCurrency(key, 0);
                Integer difference = change.get(key) - quantity;
                change.put(key, difference);
                actualChange.put(key, quantity);
            }
        }

        return result;
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
