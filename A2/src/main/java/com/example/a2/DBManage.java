package com.example.a2;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

import com.example.a2.products.Candies;
import com.example.a2.products.Chips;
import com.example.a2.products.Chocolates;
import com.example.a2.products.Drinks;
import com.example.a2.products.Product;

import java.lang.System;
import java.util.Date;

public class DBManage {

    public Connection connection = null;
    public String url = "jdbc:sqlite:src/main/data/";
    public String fileName;

    public DBManage(String fileName){
        this.fileName = fileName;
    }

    // create the database
    public void createDB() {
        url = url + fileName;
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            // user table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Users " +
                    "(username TEXT, " +
                    "password TEXT, " +
                    "userID INTEGER PRIMARY KEY NOT NULL, " +
                    "role TEXT)");
            // products Table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Products " +
                    "(cost FLOAT, " +
                    "name TEXT UNIQUE, " +
                    "prodID INTEGER PRIMARY KEY NOT NULL, " +
                    "quantity INTEGER DEFAULT (7), " +
                    "Category TEXT)");
            // currency Table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Currencies " +
                    "(amount FLOAT PRIMARY KEY, " +
                    "quantity INTEGER DEFAULT (5))");
            // transactions Table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Transactions " +
                    "(transID INTEGER PRIMARY KEY NOT NULL, " +
                    "userID REFERENCES Users(userID), " +
                    "prodID REFERENCES Products(prodID) NOT NULL," +
                    "success BIT NOT NULL," +
                    "quantity INTEGER DEFAULT (7)," +
                    "date TIMESTAMP)");
            java.lang.System.out.println("------------DB created------------");

            //populate currecies
            for(String denomination : VendingMachine.denominations){
                String toExecute = "INSERT INTO Currencies(amount, quantity) "+
                        "VALUES(\"" + denomination + "\", 5);";
                statement.executeUpdate(toExecute);
            }
            //populate products
            for(String key : VendingMachine.productMap.keySet()){
               ArrayList<String> products = VendingMachine.productMap.get(key);
               for(String product : products){
                   this.addProduct(0, product, key);
               }
            }

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at createDB_________________________");
            java.lang.System.err.println(e.getMessage());
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

    public void deleteDB() {
        File myObj = new File("src/main/data/" + fileName);
        if (myObj.delete()) {
            System.out.println("Deleted the file: " + myObj.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
    }

    public String getUser(String userName){
        String resultPassword = null;

        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "SELECT * FROM Users WHERE (? = Users.Username)";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setString(1, userName);
            ResultSet result = preparedStatement.executeQuery();

            if(result.isClosed()){
                return null;
            }

            resultPassword = result.getString("password");

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at addUser_________________________");
            java.lang.System.err.println(e.getMessage());
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

        return resultPassword;
    }

    // add user to database
    public void addUser(String userName, String password, String role){
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "INSERT INTO Users (username, password, role) VALUES(?,?,?)";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3,role);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at addUser_________________________");
            java.lang.System.err.println(e.getMessage());
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

    // remove user from database using their ID
    public void removeUser(int userID){
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "DELETE FROM users WHERE userID = ?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at removeUser_________________________");
            java.lang.System.err.println(e.getMessage());
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

    // add product to database
    public void addProduct(double cost, String name, String category){
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "INSERT INTO Products (Cost, Name,  Category) VALUES(?,?,?)";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setDouble(1, cost);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, category);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at addProduct_________________________");
            java.lang.System.err.println(e.getMessage());
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

    // remove product from database using their ID
    public void removeProduct(int prodID){
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "DELETE FROM Products WHERE prodID = ?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setInt(1, prodID);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at removeProduct_________________________");
            java.lang.System.err.println(e.getMessage());
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

    // add purchase history (customer has account)(the time of transaction will be recorded when this function is called)
    public void addTransaction(int prodID, boolean success, int userID){
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            int successBit;
            if (success){
                successBit = 1;
            } else {
                successBit = 0;
            }

            Timestamp timestamp = new Timestamp(java.lang.System.currentTimeMillis());

            String insertStatement = "INSERT INTO Transactions (userID, prodID, success, date) VALUES(?,?,?,?)";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, prodID);
            preparedStatement.setInt(3, successBit);
            preparedStatement.setTimestamp(4, timestamp);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at addTransaction_________________________");
            java.lang.System.err.println(e.getMessage());
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

    // add purchase history (anonymous buyer)
    public void addTransaction(int prodID, boolean success){
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            int successBit;
            if (success){
                successBit = 1;
            } else {
                successBit = 0;
            }

            Timestamp timestamp = new Timestamp(java.lang.System.currentTimeMillis());

            String insertStatement = "INSERT INTO Transactions (prodID, success, date) VALUES(?,?,?)";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setInt(1, prodID);
            preparedStatement.setInt(2, successBit);
            preparedStatement.setTimestamp(3, timestamp);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at addTransaction_________________________");
            java.lang.System.err.println(e.getMessage());
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

    // get the last 5 transaction of a user
    public ArrayList<Transaction> getLastFiveTransactionsByUserID(int userID){
        ArrayList<Transaction> transactions = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(url);

            // make sure the order is same using "order by"
            String insertStatement = "SELECT * FROM Transactions WHERE userID = ? ORDER BY date DESC";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setInt(1, userID);
            ResultSet productList = preparedStatement.executeQuery();

            int i = 0;
            while (productList.next() && i < 5) {
                int prodID = productList.getInt("prodID");
                int transID = productList.getInt("transID");
                boolean success = productList.getInt("success") == 1;
                Date date = new Date(productList.getTimestamp("date").getTime());
                int quantity = productList.getInt("quantity");

                transactions.add(new Transaction(transID, prodID, quantity, success, date, userID));
                i++;
            }
        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at getLastFiveTransactionsByUserID_________________________");
            java.lang.System.err.println(e.getMessage());
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
        return transactions;
    }


    // get all products currently in db
    public ArrayList<Product> getProducts(){
        ArrayList<Product> products = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(url);

            // make sure the order is same using "order by"
            String insertStatement = "SELECT * FROM products";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            ResultSet productList = preparedStatement.executeQuery();

            while (productList.next()) {
                int prodID = productList.getInt("prodID");
                String prodName = productList.getString("name");
                double cost = productList.getFloat("cost");
                int qty = productList.getInt("quantity");

                // Don't remove this -> this populates product inventory that shows on UI
                // all category cases to create different subclasses of Product
                switch (productList.getString("Category")) {
                    case "Drinks":
                        products.add(new Drinks(prodID, prodName, cost, qty));
                        break;
                    case "Chocolates":
                        products.add(new Chocolates(prodID, prodName, cost, qty));
                        break;
                    case "Chips":
                        products.add(new Chips(prodID, prodName, cost, qty));
                        break;
                    case "Candies":
                        products.add(new Candies(prodID, prodName, cost, qty));
                        break;
                    default:
                        System.out.println("product category invalid");
                }
                
            }
        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at getProducts_________________________");
            java.lang.System.err.println(e.getMessage());
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
        return products;
    }

    // get 5 most recent products
}
