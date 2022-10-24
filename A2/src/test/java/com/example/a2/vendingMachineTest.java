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

    private Sys system;
    private VendingMachine model;

    @BeforeEach
    public void setUp() {
        system = new Sys();
        model = system.getVendingMachine();
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
    void testDatabaseChange() {
        // add a product
        // delete a product
    }

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
        // TODO: can category conflict?
        assertTrue(model.updateProduct(1, "2", "Code")
                .contains("Conflicting"));
        assertTrue(model.updateProduct(1, "juice", "Name")
                .contains("Conflicting"));
    }
}
