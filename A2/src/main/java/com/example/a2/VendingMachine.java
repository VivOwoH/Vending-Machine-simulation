package com.example.a2;

import java.util.*;
import java.util.Map.Entry;

import com.example.a2.products.Product;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class VendingMachine {
    private List<Product> productInventory;
    private List<Currency> currencyInventory;
    private DBManage database;
    private HashMap<Integer, Integer> cart = new HashMap<>(); // Map<prodID,qty>
    public final static String[] categories = { "Drinks", "Chocolates", "Chips", "Candies" }; // pre-defined; can't be
                                                                                              // modified
    public final static String[] denominations = { "0.05", "0.10", "0.20", "0.50", "1.0", "2.0", "5.0", "10.0", "20.0", "50.0",
            "100.0" };
    public final static String[] products = { "water", "sprite", "coke", "pepsi", "juice",
            "mars", "m&m", "bounty", "snicker",
            "smiths", "pringles", "kettles", "thins",
            "mentos", "sourpatch", "skittles" };
    public static Map<String, ArrayList<String>> productMap = null;

    private Timer idleTimer;
    private TimerTask cancelTransactionTask;
    private long idleLimit = 120000;
    // private Alert alert;

    static {
        Map<String, ArrayList<String>> aMap = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            aMap.put(categories[i], new ArrayList<String>());
        }
        for (int i = 0; i < 5; i++) {
            ArrayList<String> current = aMap.get(categories[0]);
            current.add(products[i]);
        }
        for (int i = 5; i < 9; i++) {
            ArrayList<String> current = aMap.get(categories[1]);
            current.add(products[i]);
        }
        for (int i = 9; i < 13; i++) {
            ArrayList<String> current = aMap.get(categories[2]);
            current.add(products[i]);
        }
        for (int i = 13; i < 16; i++) {
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
    }

    private void makeTimerTask() {
        cancelTransactionTask = new TimerTask() {
            public void run() {
                clearCart();

                ImageIcon icon = new ImageIcon(getClass().getResource("/alert.png"));
                icon = new ImageIcon(icon.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
                JOptionPane.showMessageDialog(null, "No activity for too long. Transaction cancelled.", "Alert",
                        JOptionPane.INFORMATION_MESSAGE, icon);
            }
        };
    }

    public void updateProductInventory() {
        this.productInventory = database.getProducts();
    }

    private List<Integer> listAllProductID() {
        List<Integer> result = new ArrayList<Integer>();
        for (Product product : this.productInventory) {
            result.add(product.getCode());
        }
        return result;
    }

    private List<String> listAllProductName() {
        List<String> result = new ArrayList<String>();
        for (Product product : this.productInventory) {
            result.add(product.getName());
        }
        return result;
    }

    public Product findProductByID(int prodID) {
        for (Product product : this.productInventory) {
            if (product.getCode() == prodID)
                return product;
        }
        return null;
    }

    public String updateProduct(int prodID, String newValue, String field) {
        // "Name", "Code", "Category", "Quantity", "Price"
        Product selectedProduct = this.findProductByID(prodID);

        try {
            // ------------------ Defense ---------------------------
            if (selectedProduct == null) // no product of this code OR invalid code
                throw new IllegalArgumentException("Product not found");

            if (newValue == null || field == null) // null input
                throw new IllegalArgumentException("Invalid input");

            if ((field.equals("Code") || field.equals("Quantity") ||
                    field.equals("Price")) && Integer.parseInt(newValue) < 0)
                throw new IllegalArgumentException("Negative input not allowed");

            if (field.equals("Quantity") && Integer.parseInt(newValue) > 15)
                throw new IllegalArgumentException("Maximum 15 for each product");

            if (field.equals("Code") && listAllProductID().contains(Integer.parseInt(newValue)))
                throw new IllegalArgumentException("Conflicting code");

            if (field.equals("Name") && listAllProductName().contains(newValue))
                throw new IllegalArgumentException("Conflicting name");

            // ------------------ Update ---------------------------
            switch (field) {
                case "Name":
                    selectedProduct.setName(newValue);
                    break;
                case "Code":
                    selectedProduct.setCode(Integer.parseInt(newValue));
                    break;
                case "Category":
                    selectedProduct.setCategoryStr(newValue);
                    break;
                case "Quantity":
                    selectedProduct.setQty(Integer.parseInt(newValue));
                    break;
                case "Price":
                    selectedProduct.setCost(Double.parseDouble(newValue));
                    break;
                default:
                    System.out.println("Error, invalid field.");
            }

            database.updateProduct(selectedProduct.getCost(), selectedProduct.getName(),
                    selectedProduct.getQty(), selectedProduct.getCategoryStr(), selectedProduct.getCode());
            return String.format("Product %d updated", selectedProduct.getCode());

        } catch (NumberFormatException e) {
            String err = "Update product: input of wrong format";
            System.out.println(err);
            return "Input of wrong format";
        } catch (IllegalArgumentException e) {
            System.out.println("Update product: " + e.getMessage());
            return e.getMessage();
        }
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
        makeTimerTask();
        idleTimer.schedule(cancelTransactionTask, idleLimit);
    }

    // ---------------------------
    // -------- Cart -------------
    // ---------------------------

    // Button(prodID) + input qty -> addToCart
    public String addToCart(int prodID, int qty) {
        try {
            // ------------------ Defense ---------------------------
            // 0 or negative input (code, quantity)
            if (prodID <= 0 || qty <= 0) 
                throw new IllegalArgumentException("Invalid input.");

            // invalid code (not found)
            if (this.findProductByID(prodID) == null) 
                throw new IllegalArgumentException("Invalid code, product not found.");

            // invalid quantity (>current stock)
            if (this.findProductByID(prodID).getQty() < qty)
                throw new IllegalArgumentException("Stock not available.");
            
            // ------------------ Add to cart -----------------------
            // add to cart multiple times, and quantity does not exceed -> add up
            if (this.cart.containsKey(prodID)) {
                this.cart.put(prodID, this.cart.get(prodID) + qty);
            } else { // add to cart 1st time
                this.cart.put(prodID, qty);
            }

            int newQty = this.findProductByID(prodID).getQty() - qty;
            this.findProductByID(prodID).setQty(newQty);
            
            return String.format("Item add to cart! Stock: %d", newQty);

        } catch (NumberFormatException e) {
            return "Invalid input.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public void clearCart() {
        this.cart.clear();
    }

    // ---------------------------
    // -------- Money ------------
    // ---------------------------

    public HashMap<Double, Integer> changeCalc(double amount, Double aDouble) {
        HashMap<Double, Integer> result = new HashMap<>();

        for (String stringRep : this.denominations) {
            double denomination = Double.parseDouble(stringRep);
            if (denomination < aDouble) {
                if (!(amount % denomination == amount)) {
                    double without_remainder = amount - (amount % denomination);
                    double amount_denom = without_remainder / denomination;
                    result.put(denomination, (int) amount_denom);

                    amount = amount - (amount_denom * denomination);
                    if (amount > 0 && amount < 0.05) { // rounding error
                        amount = 0.05;
                    }
                } else {
                    result.put(denomination, 0);
                }
            }
        }

        return result;
    }

    /**
     * Asks database for change according to optimal config calculated via
     * changeCalc function.
     * Updates the amount of change left to cover and the change given accordingly.
     * 
     * @param change
     * @return updated change left and change given HashMaps
     */
    public ArrayList<HashMap<Double, Integer>> requestChange(HashMap<Double, Integer> change) {
        HashMap<Double, Integer> actualChange = new HashMap<>();
        ArrayList<HashMap<Double, Integer>> result = new ArrayList<>();
        result.add(change);
        result.add(actualChange);

        for (Double key : change.keySet()) {
            // find the denomination in SQL
            int quantity = database.getCurrencyQuantity(key);

            if (quantity >= change.get(key)) {
                Integer difference = quantity - change.get(key);
                database.updateCurrency(key, difference);
                actualChange.put(key, change.get(key));
                change.put(key, 0);
            } else { // need to make up the leftover notes
                database.updateCurrency(key, 0);
                Integer difference = change.get(key) - quantity;
                change.put(key, difference);
                actualChange.put(key, quantity);
            }
        }

        return result;
    }

    /**
     * Function uses the output from requestChange as the input. It then attempts to
     * use other denominations
     * to fill the change still needed. Thus it assumes that every denomination
     * still not covered has no equivalent
     * denominations left in the database and must use the next smallest
     * denomination to fill.
     * 
     * @param changes - result from requestChange is 2 maps with info on the change
     *                still left to fill and the change given
     *                so far.
     * @return updated result which has tried to fill the change still left to give.
     */
    public ArrayList<HashMap<Double, Integer>> fillGap(ArrayList<HashMap<Double, Integer>> changes) {
        System.out.println("-----FILLING----");

        HashMap<Double, Integer> left = changes.get(0);
        HashMap<Double, Integer> given = changes.get(1);
        ArrayList<Double> denominations = new ArrayList<>();

        // sort iterable for easy iteration
        for (String denomination : VendingMachine.denominations) {
            Double key = Double.parseDouble(denomination);
            denominations.add(key);
        }
        Collections.sort(denominations, Collections.reverseOrder());

        int original_amount = 0;

        if (left.containsKey(0.05)) {
            original_amount = left.get(0.05);
        }

        for (int i = 0; i < denominations.size(); i++) {
            // still change left to try
            if (left.containsKey(denominations.get(i)) && left.get(denominations.get(i)) != 0) {
                Double toCover = denominations.get(i) * left.get(denominations.get(i));

                if (denominations.get(i) == 0.05) { // last one
                    int quantity = database.getCurrencyQuantity(denominations.get(i));
                    int needed = left.get(denominations.get(i)) + original_amount;
                    if (quantity < needed) {
                        int difference = needed - quantity;
                        left.put(denominations.get(i), difference);
                        database.updateCurrency(denominations.get(i), 0);
                    } else {
                        int difference = quantity - needed;
                        left.put(denominations.get(i), 0);
                        database.updateCurrency(denominations.get(i), difference);
                    }

                    break;
                }
                // use these denominations to fill
                for (int j = i + 1; j < denominations.size(); j++) {
                    double roundCover = toCover * 100;
                    double roundDenom = denominations.get(j) * 100;
                    double possiblyCovered = roundCover - (roundCover % roundDenom);
                    double roundPossibleCover = (double) Math.round(possiblyCovered) / 100;

                    int quantity = database.getCurrencyQuantity(denominations.get(j));
                    int possibleQuantity = (int) (roundPossibleCover / denominations.get(j)); // how much we need to
                                                                                              // complete order

                    if (quantity >= possibleQuantity) {
                        Integer difference = quantity - possibleQuantity;
                        database.updateCurrency(denominations.get(j), difference);

                        given.put(denominations.get(j), possibleQuantity + given.get(denominations.get(j)));
                        left.put(denominations.get(i), 0);

                        break;
                    } else { // need to make up the leftover notes
                        database.updateCurrency(denominations.get(j), 0);
                        Integer difference = possibleQuantity - quantity;

                        toCover = (double) Math.round(toCover - (difference * denominations.get(j)) * 100) / 100;
                        toCover = Math.abs(toCover);
                        given.put(denominations.get(j), quantity + given.get(denominations.get(j)));
                        left.put(denominations.get(i), 0);
                    }

                    if (toCover != 0 && denominations.get(j) != 0.05) {
                        // now need to break up difference into smaller denominations
                        HashMap<Double, Integer> remaining = changeCalc(toCover, denominations.get(j));

                        for (Double key : remaining.keySet()) {
                            int add = 0;
                            if (left.containsKey(key)) {
                                add = left.get(key);
                            } else {
                                add = 0;
                            }

                            left.put(key, add + remaining.get(key));
                        }
                    } else if (toCover != 0) { // can't cover this
                        int amount = (int) (toCover / 0.05);
                        left.put(denominations.get(j), amount);
                    }
                }
            }
        }

        return changes;
    }

    public ArrayList<HashMap<Double, Integer>> makeCashPurchase(double cost, double input) {
        double change = input - cost;
        if (change < 0) {
            System.out.println("Not enough money");
            return null;
        }
        ArrayList<HashMap<Double, Integer>> result = fillGap(requestChange(changeCalc(change, 100.1)));

        double num_remaining = (result.get(0).get(0.05)) * 0.05;
        if(num_remaining != 0){
            result.set(0, changeCalc(num_remaining, 100.1));
        }

        return result;
    }

    public boolean checkInput(double input) {
        double valid[] = {0.01, 0.1, 0.2, 0.5, 1, 2, 5, 10, 20, 50};

        for (double v : valid) {
            if (input == v) return true;
        }

        return false;
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

    public HashMap<Integer, Integer> getCart() {
        return this.cart;
    }

    public double getTotalCost() {
        double val = 0;

        for (Entry<Integer, Integer> entry: this.cart.entrySet()) {
            Integer prodID = entry.getKey();
            Integer qty = entry.getValue();

            val += findProductByID(prodID).getCost() * qty;
        }

        return val;
    }
}
