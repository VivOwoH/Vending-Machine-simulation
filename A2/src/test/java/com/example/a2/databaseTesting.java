package com.example.a2;

import com.example.a2.products.Product;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class databaseTesting {

    private static DBManage database;
    private final String url = "jdbc:sqlite:src/main/data/test.sqlite";
    private Connection connection = null;

    @BeforeAll
    static void setUp() {
        database = new DBManage("test.sqlite");
        database.createDB();
        System.out.println("DONE!");
    }

    @Test
    void getCostTest() {
        // we use this test file because no update
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();

        for (int i = 1; i < 17; i++) {
            assertEquals(2.0, tempDB.getCost(i));
        }
    }

    @Test
    // test if users are being added
    void addRemoveUserTest() {
        // add user first
        try {
            connection = DriverManager.getConnection(url);

            database.addUser("user1", "password1", "User");
            database.addUser("user2", "password2", "User");
            database.addUser("user3", "password3", "User");

            String insertStatement = "SELECT count(*) as total FROM Users";
            PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
            ResultSet productList = preparedStatement.executeQuery();
            int size = productList.getInt("total");
            preparedStatement.close();

            assertEquals(4, size);

        } catch (Exception e) {
            // assume can connect as db was created
            System.out.println(e);
            assertNotNull(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                java.lang.System.err.println(e.getMessage());
            }
        }

        // remove user
        try {
            connection = DriverManager.getConnection(url);

            database.removeUser(database.getUserID("user1"));
            database.removeUser(database.getUserID("user2"));
            database.removeUser(database.getUserID("user3"));

            String insertStatement = "SELECT count(*) as total FROM Users";
            PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
            ResultSet productList = preparedStatement.executeQuery();
            int size = productList.getInt("total");
            preparedStatement.close();

            assertEquals(1, size);

        } catch (Exception e) {
            // assume can connect as db was created
            System.out.println(e);
            assertNotNull(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                java.lang.System.err.println(e.getMessage());
            }
        }
    }

    @Test
    void getUsers() {
        // all roles of users
        database.addUser("seller", "1", "Seller");
        database.addUser("cashier", "2", "Cashier");
        database.addUser("user", "3", "User");

        ArrayList<User> list = database.getUsers();
        assertEquals(4, list.size());
        assertEquals(Owner.class, list.get(0).getRole().getClass());
        assertEquals(Seller.class, list.get(1).getRole().getClass());
        assertEquals(Cashier.class, list.get(2).getRole().getClass());
        assertEquals(null, list.get(3).getRole());
    }

    @Test
    // test if transactions are being added (also cancelled transactions added)
    void addgetTransactionTest() {

        database.addTransaction(1, true, 0, 1, 10, 0.50, "CASH");
        database.addTransaction(2, true, 0, 1, 11, 0.50, "CASH");
        database.addTransaction(3, true, 0, 1, 12, 0.50, "CASH");

        database.addCancelledTransaction("user cancelled");
        database.addCancelledTransaction("timeout");

        try {
            connection = DriverManager.getConnection(url);

            String insertStatement = "SELECT count(*) as total FROM Transactions";
            String insertStatement2 = "SELECT count(*) as total2 FROM Transactions WHERE success = 0";

            PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
            PreparedStatement preparedStatement2 = connection.prepareStatement(insertStatement2);
            ResultSet productList = preparedStatement.executeQuery();
            ResultSet productList2 = preparedStatement2.executeQuery();

            int size = productList.getInt("total");
            int sizeCancelled = productList2.getInt("total2");

            preparedStatement.close();
            preparedStatement2.close();

            assertEquals(5, size);
            assertEquals(2, sizeCancelled);

        } catch (Exception e) {
            // assume can connect as db was created
            System.out.println(e);
            assertNotNull(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                java.lang.System.err.println(e.getMessage());
            }
        }

        // TODO: report does not print userID now, need to change this text when fixed
        // also test getCancelledTransaction here

        Timestamp timestamp = new Timestamp(java.lang.System.currentTimeMillis()); // should work if tests don't run too
                                                                                   // long

        Date date = new Date(timestamp.getTime());
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy, hh:mm").format(date);

        String expectedReport = String.format("%s | null | user cancelled\n", formattedDate) +
                String.format("%s | null | timeout\n", formattedDate);
        assertEquals(expectedReport, database.getCancelledTransactions());

    }

    @Test
    // test if correct number of products has been initialised
    void productInitTest() {
        ArrayList<Product> products = database.getProducts();
        assertEquals(16, products.size());
    }

    @Test
    void addRemoveProductTest() {
        // initially 16 products
        assertTrue(database.addProduct(0, "pringles", "Chips").contains("already"));
        assertTrue(database.addProduct(0, "test", "Drinks").contains("added"));

        database.removeProduct(17);

        try {
            connection = DriverManager.getConnection(url);

            String insertStatement = "SELECT count(*) as total FROM Products";

            PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
            ResultSet productList = preparedStatement.executeQuery();

            int size = productList.getInt("total");

            preparedStatement.close();

            assertEquals(16, size);

        } catch (Exception e) {
            // assume can connect as db was created
            System.out.println(e);
            assertNotNull(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                java.lang.System.err.println(e.getMessage());
            }
        }

    }

    @Test
    void getCurrencyTest() {
        // every currency gets instantiated as 5
        int quantity = database.getCurrencyQuantity(5.0);
        assertEquals(5, quantity);
    }

    @Test
    void getPasswordTest() {
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();
        tempDB.addUser("user4", "password4", "User");
        String password = tempDB.getUserPassword("user4");
        assertEquals("password4", password);
        tempDB.deleteDB();
    }

    @Test
    void getUserIDTest() {
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();
        tempDB.addUser("user4", "password4", "User");
        assertEquals(1, tempDB.getUserID("user4"));
        tempDB.deleteDB();
    }

    @Test
    void transactionHistoryTest() {
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();
        tempDB.addTransaction(1, true, 0, 1, 1, 1, "CASH");
        tempDB.addCancelledTransaction("user cancelled");

        Timestamp timestamp = new Timestamp(java.lang.System.currentTimeMillis()); // should work if tests don't run too
                                                                                   // long
        Date date = new Date(timestamp.getTime());
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy, hh:mm").format(date);

        String history = tempDB.getTransactionHistory();

        String expectedReport = String.format("%s | 1 | 1.00 | 1.00 | CASH\n", formattedDate) + 
                                String.format("%s | N/A | N/A | N/A | CANCELLED\n", formattedDate);
        assertEquals(expectedReport, history);
        tempDB.deleteDB();
    }

    @Test
    void getLastFiveTransactions() {
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();
        tempDB.addTransaction(1, true, 0, 1, 1, 1, "CASH");
        tempDB.addTransaction(3, true, 0, 1, 1, 1, "CASH");
        tempDB.addTransaction(2, true, 0, 1, 1, 1, "CASH");

        ArrayList<Transaction> transactions = tempDB.getLastFiveTransactionsByUserID(0);

        assertEquals(1, transactions.get(2).getProdID());
        assertEquals(3, transactions.get(1).getProdID());
        assertEquals(2, transactions.get(0).getProdID());
        tempDB.deleteDB();
    }

    @Test
    void updateSoldTest() {
        try {
            connection = DriverManager.getConnection(url);

            database.updateSold(1, 5); // sold

            String insertStatement = "SELECT sold FROM Products where prodID = 1";
            PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
            ResultSet productList = preparedStatement.executeQuery();
            int qty = productList.getInt("sold");

            assertEquals(5, qty);

            preparedStatement.close();

            database.updateSold(1, 2); // sold again (concatenate)

            String insertStatement2 = "SELECT sold FROM Products where prodID = 1";
            PreparedStatement preparedStatement2 = connection.prepareStatement(insertStatement2);
            ResultSet productList2 = preparedStatement2.executeQuery();
            int newQty = productList2.getInt("sold");

            assertEquals(7, newQty);

            preparedStatement2.close();

        } catch (Exception e) {
            // assume can connect as db was created
            System.out.println(e);
            assertNotNull(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                java.lang.System.err.println(e.getMessage());
            }
        }
    }

    @Test
    void savegetCCInfoTest() {
        // test admin (0)
        assertTrue(database.saveCreditCardInfo("test", 111, 0).contains("added"));
        ArrayList<String> result = database.getCCInfo("admin");
        assertEquals("test", result.get(0));
        assertEquals("111", result.get(1));
    }

    @Test
    void testValidCredit() {
        database.loadCreditConfig();

        // invalid
        assertFalse(database.creditCardIsValid("test", 0));
        // valid
        assertTrue(database.creditCardIsValid("Charles", 40691));
        assertTrue(database.creditCardIsValid("Sergio", 42689));
        assertTrue(database.creditCardIsValid("Kasey", 60146));
    }

    @Test
    void updateCurrencyAndReportTest() {
        database.updateCurrency(0.05, 100);
        assertEquals(100, database.getCurrencyQuantity(0.05));

        database.updateCurrency(0.05, 5); // reset back
        String[] denominations = new String[] { "100", "50", "20", "10", "5", "2", "1", "0.5", "0.2", "0.1", "0.05" };

        // also test getCurrencyReport here
        String expectedReport = "";
        for (int i = 0; i < 11; i++) {
            expectedReport += denominations[i] + " | 5\n";
        }
        assertEquals(expectedReport, database.getCurrencyReport());
    }

    @Test
    void getItemDetailsTest() {
        // we use this test file because no update
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();

        String[] categories = { "Chips", "Candies", "Drinks", "Chocolates" };
        String[] products = { "smiths", "pringles", "kettles", "thins",
                "mentos", "sourpatch", "skittles",
                "water", "sprite", "coke", "pepsi", "juice",
                "mars", "m&m", "bounty", "snicker" };
        String expectedReport = "";
        for (int i = 1; i < 17; i++) {
            String category = "";
            if (i >= 1 && i <= 4) {
                category = categories[0];
            } else if (i >= 5 && i <= 7) {
                category = categories[1];
            } else if (i >= 8 && i <= 12) {
                category = categories[2];
            } else if (i >= 13 && i <= 16) {
                category = categories[3];
            }
            // initial price is 2.00
            expectedReport += products[i - 1] + " | " + i + " | " + 7 + " | " +
                    "2.00" + " | " + category + "\n";
        }
        assertEquals(expectedReport, tempDB.getItemDetails());
    }

    @Test
    void getItemSummaryTest() {
        // we use this test file because no update
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();

        String[] products = { "smiths", "pringles", "kettles", "thins",
                "mentos", "sourpatch", "skittles",
                "water", "sprite", "coke", "pepsi", "juice",
                "mars", "m&m", "bounty", "snicker" };

        String expectedReport = "";
        for (int i = 1; i < 17; i++) {
            expectedReport += products[i - 1] + " | " + i + " | " + 0 + "\n";
        }
        assertEquals(expectedReport, tempDB.getItemSummary());
    }

    @Test
    void getUsersReportTest() {
        // we use this test file because no update
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();

        String expectedReport = "admin | Owner\n";
        assertEquals(expectedReport, tempDB.getUsersReport());
    }

    @AfterAll
    static void completeTest() {
        database.deleteDB();
    }

}
