package com.example.a2;

import java.util.*;

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
    public final static String[] denominations = { "5c", "10c", "20c", "50c", "1d", "2d", "5d", "10d", "20d", "50d",
            "100d" };
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
        this.updateProductInventory(); // refresh inventory just in case

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
        } catch (NumberFormatException e) {
            String err = "Update product: input of wrong format";
            System.out.println(err);
            return err;
        } catch (IllegalArgumentException e) {
            System.out.println("Update product: " + e.getMessage());
            return e.getMessage();
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
        makeTimerTask();
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

    public HashMap<Integer, Integer> getCart() {
        return this.cart;
    }
}
