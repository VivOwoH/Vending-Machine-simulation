package com.example.a2;

import com.example.a2.products.Product;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class databaseTesting {

    private static DBManage database;
    private final String url = "jdbc:sqlite:src/main/data/test.sqlite";
    private Connection connection = null;

    @BeforeAll
    static void setUp(){
        database = new DBManage("test.sqlite");
        database.createDB();
        System.out.println("DONE!");
    }

    @Test
    // test if users are being added
    void addUserTest() {
        try {
            connection = DriverManager.getConnection(url);

            database.addUser("user1", "password1", "User");
            database.addUser("user2", "password2", "User");
            database.addUser("user3", "password3", "User");

            String insertStatement = "SELECT count(*) as total FROM Users";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            ResultSet productList = preparedStatement.executeQuery();
            int size = productList.getInt("total");
            preparedStatement.close();

            assertEquals(4, size);


        } catch (Exception e){
            // assume can connect as db was created
            System.out.println(e);
            assertNotNull(e);
        }  finally {
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
        // test if transactions are being added
    void addTransactionTest() {

        database.addTransaction(1, true, 0, 1, 10, 0.50);
        database.addTransaction(2, true, 0, 1,11, 0.50);
        database.addTransaction(3, true, 0, 1,12, 0.50);

        try {
            connection = DriverManager.getConnection(url);

            String insertStatement = "SELECT count(*) as total FROM Transactions";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            ResultSet productList = preparedStatement.executeQuery();
            int size = productList.getInt("total");
            preparedStatement.close();

            assertEquals(3, size);

        } catch (Exception e){
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
        // test if correct number of products has been initialised
    void productInitTest() {
        ArrayList<Product> products = database.getProducts();
        assertEquals(16, products.size());
    }

    @Test
    void getCurrencyTest() {
        // every currency gets instantiated as 5
        int quantity = database.getCurrencyQuantity(5.0);
        assertEquals(5, quantity);
    }

    @Test
    void getPasswordTest(){
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();
        tempDB.addUser("user4", "password4", "User");
        String password = tempDB.getUserPassword("user4");
        assertEquals("password4", password);
        tempDB.deleteDB();
    }

    @Test
    void getUserIDTest(){
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();
        tempDB.addUser("user4", "password4", "User");
        assertEquals(1, tempDB.getUserID("user4"));
        tempDB.deleteDB();
    }

    @Test
    void transactionHistoryTest(){
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();
        tempDB.addTransaction(1,true,0,1,1,1);
        String history = tempDB.getTransactionHistory().substring(18);
        assertEquals("| 1 | 1.00 | 1.00 | CASH\n", history);
        tempDB.deleteDB();
    }

    @Test
    void getLastFiveTransactions(){
        DBManage tempDB = new DBManage("test3.sqlite");
        tempDB.createDB();
        tempDB.addTransaction(1,true,0,1,1,1);
        tempDB.addTransaction(3,true,0,1,1,1);
        tempDB.addTransaction(2,true,0,1,1,1);

        ArrayList<Transaction> transactions = tempDB.getLastFiveTransactionsByUserID(0);

        assertEquals(1, transactions.get(2).getProdID());
        assertEquals(3, transactions.get(1).getProdID());
        assertEquals(2, transactions.get(0).getProdID());
        tempDB.deleteDB();
    }

    @AfterAll
    static void completeTest() {
        database.deleteDB();
    }

}
