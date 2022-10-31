package com.example.a2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.a2.products.Product;

public class vendingMachineTest {

    private HelloApplication app;
    private Sys system;
    private VendingMachine model;

    @BeforeEach
    public void setUp() {
        app = new HelloApplication();
        system = new Sys(app);
        model = system.getVendingMachine();
    }

    // ---------------------------------------------

    // will re-enable this test when db test is fixed

    // @Test
    // void testInitialProducts() {
    //     // 16 products
    //     List<Product> ls = model.getProductInventroy();
    //     assertEquals(16, ls.size());

    //     // all field no null, default qty = 7
    //     for (Product product : ls) {
    //         assertNotNull(product.getCode());
    //         assertNotNull(product.getCost());
    //         assertNotNull(product.getCategoryStr());
    //         assertNotNull(product.getName());
    //         assertEquals(7, product.getQty());
    //     }
    // }

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
        assertTrue(model.addToCart(1, model.findProductByID(1).getQty()+1).contains("Stock"));

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
    void testPayByCash() {

    }

    @Test
    void testPayByCard() {

    }

    // ------------------------------------------
    // --------------- ADMIN --------------------
    // ------------------------------------------

    @Test
    void testUpdateProductInvalid() {
        // "Name", "Code", "Category", "Quantity", "Price"

        // product cannot be found by code & null input
        assertTrue(model.updateProduct(100, "testing", "Name")
                .contains("not found"));
        assertTrue(model.updateProduct(1, null, null)
                .contains("Invalid"));

        // negative input (price, quantity, code)
        assertTrue(model.updateProduct(1, "-1", "Code")
                .contains("Negative"));
        assertTrue(model.updateProduct(1, "-1", "Quantity")
                .contains("Negative"));
        assertTrue(model.updateProduct(1, "-1", "Price")
                .contains("Negative"));

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
    }

    @Test
    void testUpdateUserRole() {
        // username, role

        // input of wrong format(String username)

        // user not found

        // new role is the same
    }

    @Test
    void testUpdateCurrencies() {
        // input of wrong format (currencies, qty)

        // invalid currencies not in list (currencies)

        // 0 or negative input (qty)
    }

}
