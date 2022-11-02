package com.example.a2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.a2.products.Product;

public class vendingMachineTest {

    private static VendingMachine model;
    private static DBManage database;
    private static Userbase userbase;
    private static Sys system;

    @BeforeAll
    static void setUp() {
        database = new DBManage("test2.sqlite");
        database.createDB();
        database.loadCreditConfig();
        userbase = new Userbase(database, "admin", "admin", 0);

        system = new Sys(new HelloApplication());
        system.setDatabase(database);
        system.setUserbase(userbase);

        model = new VendingMachine(database, system);
        system.setVendingMachine(model);
    }

    // ---------------------------------------------

    @Test
    void testInitialProducts() {
        // 16 products
        List<Product> ls = model.getProductInventroy();
        assertEquals(16, ls.size());

        // all field no null, default qty = 7
        for (Product product : ls) {
            assertNotNull(product.getCode());
            assertNotNull(product.getCost());
            assertNotNull(product.getCategoryStr());
            assertNotNull(product.getName());
            assertEquals(7, product.getQty());
        }
    }

    @Test
    void testAddToCart() {
        // 0 or negative input (code, quantity)
        assertTrue(model.addToCart(0, 5).contains("Invalid"));
        assertTrue(model.addToCart(-1, 5).contains("Invalid"));
        assertTrue(model.addToCart(1, 0).contains("Invalid"));
        assertTrue(model.addToCart(1, -1).contains("Invalid"));

        // invalid code (not found)
        assertTrue(model.addToCart(20, 5).contains("not found"));

        // invalid quantity (>current stock)
        assertTrue(model.addToCart(1, model.findProductByID(1).getQty() + 1).contains("Stock"));

        // valid quantity boundary (=current stock)
        assertTrue(model.addToCart(1, model.findProductByID(1).getQty()).contains("add"));

        // add to cart twice, and 2nd time exceed current stock
        int tmpQty = model.findProductByID(2).getQty();
        assertTrue(model.addToCart(2, 2).contains("add"));
        assertTrue(model.addToCart(2, 10).contains("Stock"));

        // add to cart twice success, quantity adds up
        assertTrue(model.addToCart(2, 2).contains("add"));
        assertEquals(4, model.getCart().get(2)); // qty in cart
        assertEquals(tmpQty - 4, model.findProductByID(2).getQty()); // qrt in stock
    }

    @Test
    void testGetTotalCost() {
        assertTrue(model.updateProduct(1, "4.4", "Price")
                .contains("updated"));
        assertTrue(model.updateProduct(2, "3.3", "Price")
                .contains("updated"));
        model.addToCart(1, 5);
        model.addToCart(2, 5);
        assertEquals("38.50", String.format("%.2f", model.getTotalCost()));

        // clear cart
        model.clearCart();
        assertTrue(model.getCart().size() == 0);

        // reset update back
        model.findProductByID(1).setQty(7); // restore stock
        model.findProductByID(2).setQty(7);
        assertTrue(model.updateProduct(1, "2.0", "Price")
                .contains("updated"));
        assertTrue(model.updateProduct(2, "2.0", "Price")
                .contains("updated"));
    }

    @Test
    void testCheckInput() {
        // invalid negative input
        assertFalse(model.checkInput(-1));

        // invalid denomination
        assertFalse(model.checkInput(3.0));

        // valid & some alternative input check
        assertTrue(model.checkInput(1.0000)); // integer or double
        assertTrue(model.checkInput(1));

        assertTrue(model.checkInput(0.500000)); // many decimal places
    }

    @Test
    void testMakeCashPurcase() {
       model.makeCashPurchase(5.0, 100.0);

       assertEquals(model.getDatabase().getCurrencyQuantity(50.0), 4);
       assertEquals(model.getDatabase().getCurrencyQuantity(20.0), 3);
       assertEquals(model.getDatabase().getCurrencyQuantity(5.0), 4);

       model.makeCashPurchase(0.50, 10.0);

        assertEquals(model.getDatabase().getCurrencyQuantity(0.50), 4);
        assertEquals(model.getDatabase().getCurrencyQuantity(2.0), 3);
        assertEquals(model.getDatabase().getCurrencyQuantity(5.0), 3);

        model.makeCashPurchase(0.50, 10.0);

        assertEquals(model.getDatabase().getCurrencyQuantity(0.50), 3);
        assertEquals(model.getDatabase().getCurrencyQuantity(2.0), 1);
        assertEquals(model.getDatabase().getCurrencyQuantity(5.0), 2);

        model.makeCashPurchase(0.50, 5.0);

        assertEquals(model.getDatabase().getCurrencyQuantity(0.50), 2);
        assertEquals(model.getDatabase().getCurrencyQuantity(2.0), 0);
        assertEquals(model.getDatabase().getCurrencyQuantity(1.0), 3);

        model.makeCashPurchase(0.50, 5.0);

        assertEquals(model.getDatabase().getCurrencyQuantity(0.50), 0);
        assertEquals(model.getDatabase().getCurrencyQuantity(2.0), 0);
        assertEquals(model.getDatabase().getCurrencyQuantity(1.0), 0);
        assertEquals(model.getDatabase().getCurrencyQuantity(0.20), 3);

        model.makeCashPurchase(0.50, 5.0);

        //doesn't change bc it can't be done
        assertEquals(model.getDatabase().getCurrencyQuantity(0.20), 3);
        model.getDatabase().deleteDB();
    }

    // ------------------------------------------
    // --------------- ADMIN --------------------
    // ------------------------------------------

    @Test
    void testUpdateProduct() {
        // "Name", "Code", "Category", "Quantity", "Price"

        // product cannot be found by code & null input
        assertTrue(model.updateProduct(100, "testing", "Name")
                .contains("not found"));
        assertTrue(model.updateProduct(1, null, null)
                .contains("Invalid"));

        // negative input (price, quantity, code)
        assertTrue(model.updateProduct(1, "-1", "Code")
                .contains("than 0"));
        assertTrue(model.updateProduct(1, "-1", "Quantity")
                .contains("positive"));
        assertTrue(model.updateProduct(1, "-1", "Price")
                .contains("than 0"));

        // input of wrong format (price, quantity)
        assertTrue(model.updateProduct(1, "wrong", "Price")
                .contains("format"));
        assertTrue(model.updateProduct(1, "wrong", "Quantity")
                .contains("format"));

        // invalid quantity (>15)
        assertTrue(model.updateProduct(1, "16", "Quantity")
                .contains("Maximum"));

        // conflicting code/name
        assertTrue(model.updateProduct(1, "2", "Code")
                .contains("Conflicting"));
        assertTrue(model.updateProduct(1, "juice", "Name")
                .contains("Conflicting"));

        // Invalid cateogry
        assertTrue(model.updateProduct(1, "Snacks", "Category")
                .contains("category"));

        // valid update: cost, name, prodID, quantity, category
        assertTrue(model.updateProduct(1, "17", "Code")
                .contains("updated"));
        assertTrue(model.updateProduct(17, "1", "Code")
                .contains("updated")); // reset back

        assertTrue(model.updateProduct(1, "lemonade", "Name")
                .contains("updated"));
        assertTrue(model.updateProduct(1, "4.4129389", "Price")
                .contains("updated"));

        // we want to round price to nearest 0.05, so cash feature do not malfunction
        assertEquals("4.40", String.format("%.2f", model.findProductByID(1).getCost()));
        assertTrue(model.updateProduct(1, "9.763789", "Price")
                .contains("updated"));
        assertEquals("9.75", String.format("%.2f", model.findProductByID(1).getCost()));

        assertTrue(model.updateProduct(1, "smiths", "Name")
                .contains("updated")); // reset back
        assertTrue(model.updateProduct(1, "2.0", "Price")
                .contains("updated")); // reset back

        assertTrue(model.updateProduct(1, "15", "Quantity")
                .contains("updated")); // boundary case
        assertTrue(model.updateProduct(1, "0", "Quantity")
                .contains("greater than 0")); // boundary case
        assertTrue(model.updateProduct(1, "7", "Quantity")
                .contains("updated")); // reset back

        assertTrue(model.updateProduct(1, "Drinks", "Category")
                .contains("updated"));
        assertTrue(model.updateProduct(1, "Chips", "Category")
                .contains("updated")); // reset back
    }

    @Test
    void testUpdateUserRole() { // username, role
        // -------------- INVALID ---------------------
        // input of wrong format(username) -> handled at UI level
        // user not found -> handled at UI level

        // -------------- VALID ---------------------
        // new role is the same as b4 (update nonetheless)
        Owner owner = new Owner();
        assertTrue(owner.modifyRole(system, 0, "Owner").contains("New role Owner"));
        assertTrue(owner.modifyRole(system, 0, "User").contains("New role User"));
        assertTrue(owner.modifyRole(system, 0, "Cashier").contains("New role Cashier"));
        assertTrue(owner.modifyRole(system, 0, "Seller").contains("New role Seller"));
        assertTrue(owner.modifyRole(system, 0, "Owner").contains("New role Owner")); // reset
    }

    @Test
    void testShowProductCategorized() {
        // invalid category
        assertEquals(0, model.ShowProductCategorized("test").size());

        // valid category
        assertEquals(4, model.ShowProductCategorized("Chips").size());
        assertEquals(3, model.ShowProductCategorized("Candies").size());
        assertEquals(5, model.ShowProductCategorized("Drinks").size());
        assertEquals(4, model.ShowProductCategorized("Chocolates").size());
    }
}
