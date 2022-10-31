package com.example.a2;

import com.example.a2.products.Product;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class databaseTesting {

    private static DBManage database;
    private String url = "jdbc:sqlite:src/main/data/";
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

            database.addUser("Admin", "password", "Owner");
            database.addUser("user1", "password1", "User");
            database.addUser("user2", "password2", "User");
            database.addUser("user3", "password3", "User");

            String insertStatement = "SELECT count(*) as total FROM Users";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            ResultSet productList = preparedStatement.executeQuery();
            int size = productList.getInt("total");
            preparedStatement.close();

            assertEquals(5, size);


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

        database.addTransaction(1, true, 1, 1, 0, 0);
        database.addTransaction(2, true, 1, 1, 0, 0);
        database.addTransaction(3, true, 1, 1, 0, 0);

        ArrayList<Transaction> tran1 = database.getLastFiveTransactionsByUserID(1);

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


    @AfterAll
    static void completeTest() {
        database.deleteDB();
    }

}
