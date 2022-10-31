package com.example.a2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.example.a2.products.Candies;
import com.example.a2.products.Chips;
import com.example.a2.products.Chocolates;
import com.example.a2.products.Drinks;
import com.example.a2.products.Product;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


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
                    "role TEXT," +
                    "cardName TEXT," +
                    "cardNumber INTEGER)");
            // products Table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Products " +
                    "(cost FLOAT, " +
                    "name TEXT UNIQUE, " +
                    "prodID INTEGER PRIMARY KEY NOT NULL, " +
                    "quantity INTEGER DEFAULT (7), " +
                    "Category TEXT)");
            // currency Table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Currencies " +
                    "(amount STRING PRIMARY KEY, " +
                    "quantity INTEGER DEFAULT (5))");
            // transactions Table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Transactions " +
                    "(transID INTEGER PRIMARY KEY, " +
                    "userID REFERENCES Users(userID), " +
                    "prodID REFERENCES Products(prodID)," +
                    "success BIT NOT NULL," +
                    "cancelReason VARCHAR(50)," +
                    "cash FLOAT," +
                    "change FLOAT," +
                    "quantity INTEGER DEFAULT (7)," +
                    "date TIMESTAMP)");
            // credit card Table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Credit_Card " +
                    "(name STRING, " +
                    "number INTEGER DEFAULT (5))");
            java.lang.System.out.println("------------DB created------------");

            //populate currecies
            for(String denomination : VendingMachine.denominations){
                String toExecute = "INSERT INTO Currencies(amount, quantity) "+
                        "VALUES( \""+ denomination + "\", 5);";
                statement.executeUpdate(toExecute);
            }
            //populate products
            for(String key : VendingMachine.productMap.keySet()){
               ArrayList<String> products = VendingMachine.productMap.get(key);
               for(String product : products){
                   this.addProduct(0, product, key);
               }
            }
            //populate users
            String toExecute = "INSERT INTO Users(username, password, userID, role) " +
                    "VALUES(\"admin\", \"admin\", 0, \"Owner\");";
            statement.executeUpdate(toExecute);

        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE"))
                return;
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

    public Integer getCurrencyQuantity(Double denom){
        Integer resultAmount = 0;
        String denomination = denom.toString();

        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "SELECT quantity FROM Currencies WHERE (? = Currencies.Amount)";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setString(1, denomination);
            ResultSet result = preparedStatement.executeQuery();

            if(result.isClosed()){
                return null;
            }

            resultAmount = result.getInt("quantity");

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

        return resultAmount;
    }

    public String getUsersReport() {
        String resultStr = null;

        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "SELECT username, role FROM Users";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            ResultSet result = preparedStatement.executeQuery();

            if (result.isClosed()) {
                return null;
            }
            if (result.getString(1) == null) {
                return null;
            }

            String tmp = "";
            resultStr = result.getString(1) + " | " + result.getString(2) + "\n";
            while (result.next()){
                resultStr += tmp;
                tmp = result.getString(1) + " | " + result.getString(2) + "\n";
            }

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at getUsers_________________________");
            java.lang.System.err.println(e.getMessage());
        }

        return resultStr;
    }

    public String getCurrencyReport() {
        String resultStr = null;

        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "SELECT amount, quantity FROM Currencies";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            ResultSet result = preparedStatement.executeQuery();

            if (result.isClosed()) {
                return null;
            }
            if (result.getString(1) == null) {
                return null;
            }

            String tmp = "";
            resultStr = result.getString(1) + " | " + result.getString(2) + "\n";
            while (result.next()){
                resultStr += tmp;
                tmp = result.getString(1) + " | " + result.getString(2) + "\n";
            }

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at getUsers_________________________");
            java.lang.System.err.println(e.getMessage());
        }

        return resultStr;
    }

    public String getUserPassword(String userName){
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
            java.lang.System.out.println("_________________________ERROR at getUserPassword_________________________");
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

    public int getUserID(String userName){
        int userID = 0;

        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "SELECT * FROM Users WHERE (? = Users.Username)";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setString(1, userName);
            ResultSet result = preparedStatement.executeQuery();

            userID = result.getInt("userID");

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at getUserPassword_________________________");
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

        return userID;
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

    public String updateUserRole(String role, int userID) {
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "UPDATE Users SET role=? WHERE userID=?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setString(1, role);
            preparedStatement.setInt(2, userID);
            preparedStatement.executeUpdate();
 
            return "Role updated";

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at updateUserRole_________________________");
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
        return null;
    }

    // add product to database
    public String addProduct(double cost, String name, String category){
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

            return "Product added";

        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE")) {
                return "Product already in DB.";
            }
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
        return null;
    }

    public String updateProduct(double cost, String name, int qty, String category, int prodID) {
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "UPDATE Products SET cost=?,name=?,quantity=?,Category=? WHERE prodID=?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setDouble(1, cost);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, qty);
            preparedStatement.setString(4, category);
            preparedStatement.setInt(5, prodID);
            preparedStatement.executeUpdate();
 
            return "Product updated";

        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE")) {
                String err = "Product violates UNIQUE constraint.";
                System.out.println(err);
                return err;
            }
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
        return null;
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

    public void addCancelledTransaction(String reason) {
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            Timestamp timestamp = new Timestamp(java.lang.System.currentTimeMillis());

            String insertStatement = "INSERT INTO Transactions (date, success, cancelReason) VALUES(?,?,?)";
            PreparedStatement preparedStatement =
                connection.prepareStatement(insertStatement);
            preparedStatement.setTimestamp(1, timestamp);
            preparedStatement.setInt(2, 0);
            preparedStatement.setString(3, reason);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at addCancelledTransaction_________________________");
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
    public void addTransaction(int prodID, boolean success, int userID, int quantity, double cash, double change){
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

            String insertStatement = null;
            PreparedStatement preparedStatement = null;
            if (cash == -1) {
                insertStatement = "INSERT INTO Transactions (userID, prodID, success, date, quantity) VALUES(?,?,?,?,?)";
                preparedStatement =
                        connection.prepareStatement(insertStatement);
                preparedStatement.setInt(5, quantity);
            } else {
                insertStatement = "INSERT INTO Transactions (userID, prodID, success, date, cash, change, quantity) VALUES(?,?,?,?,?,?,?)";
                preparedStatement =
                    connection.prepareStatement(insertStatement);
                preparedStatement.setFloat(5, (float)cash);
                preparedStatement.setFloat(6, (float)change);
                preparedStatement.setInt(7, quantity);
            }
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

    //get all cancelled transactions
    public String getCancelledTransactions() {
        String returnResult = null;

        try {
            connection = DriverManager.getConnection(url);

            // make sure the order is same using "order by"
            String insertStatement = "SELECT * FROM Transactions WHERE success = 0";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            ResultSet list = preparedStatement.executeQuery();

            returnResult = "";
            while (list.next()) {
                String userID = list.getString("userID");
                Date date = new Date(list.getTimestamp("date").getTime());
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy, hh:mm").format(date);
                String reason = list.getString("cancelReason");

                returnResult += String.format("%s | %s | %s\n", formattedDate, userID, reason);
            }
        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at getCancelledTransaction_________________________");
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
        return returnResult;
    }

    //get all transactions
    public String getTransactionHistory() {
        String history = null;

        try {
            connection = DriverManager.getConnection(url);

            // make sure the order is same using "order by"
            String insertStatement = "SELECT * FROM Transactions";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            ResultSet list = preparedStatement.executeQuery();

            history = "";
            while (list.next()) {
                Date date = new Date(list.getTimestamp("date").getTime());
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy, hh:mm").format(date);
                if (list.getInt("success") == 0) {
                    history += String.format("%s | N/A | N/A | N/A | CANCELLED\n", formattedDate);
                }
                String prodID = list.getString("prodID");
                float cash = list.getFloat("cash");
                if (!list.wasNull()) { 
                    float change = list.getFloat("change");
                    history += String.format("%s | %s | %.2f | %.2f | CASH\n", formattedDate, prodID, cash, change);
                } else {
                    history += String.format("%s | %s | N/A | N/A | CARD\n", formattedDate, prodID);
                }
            }
        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at getTransactionHistory_________________________");
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
        return history;
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

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<User>();

        try {
            connection = DriverManager.getConnection(url);

            // make sure the order is same using "order by"
            String insertStatement = "SELECT * FROM users";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            ResultSet userList = preparedStatement.executeQuery();

            while (userList.next()) {
                String username = userList.getString("username");
                String password = userList.getString("password");
                int userID = userList.getInt("userID");

                // Don't remove this -> this populates product inventory that shows on UI
                // all category cases to create different subclasses of Product
                switch (userList.getString("role")) {
                    case "User":
                        users.add(new User(username, password, userID));
                        break;
                    case "Owner":
                        users.add(new User(username, password, userID).setRole(new Owner()));
                        break;
                    case "Cashier":
                        users.add(new User(username, password, userID).setRole(new Cashier()));
                        break;
                    case "Seller":
                        users.add(new User(username, password, userID).setRole(new Seller()));
                        break;
                    default:
                        System.out.println("User role invalid");
                }
                
            }
        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at getUsers_________________________");
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
        return users;
    }

    /**
     * Function updates the amount associated with a denomination of currency
     *
     * @param denomination - desired denomination to update
     * @param new_amount - new amount for denomination
     */
    public void updateCurrency(Double denomination, int new_amount) {
        String insert_denomination = String.valueOf(denomination);
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "UPDATE Currencies SET quantity = ? WHERE amount = ?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setInt(1, new_amount);
            preparedStatement.setString(2, insert_denomination);
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

    public void loadCreditConfig() {
        JSONParser jsonParser = new JSONParser();
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // clear table everytime it is loaded
            statement.executeUpdate("DELETE FROM credit_card");

            try (FileReader reader = new FileReader("src/main/data/credit_cards.json")) {
                //Read JSON file
                Object obj = jsonParser.parse(reader);

                JSONArray creditCardList = (JSONArray) obj;

                for (Object creditCard : creditCardList) {
                    JSONObject creditCardObject = (JSONObject) creditCard;

                    String name = (String) creditCardObject.get("name");

                    int number = Integer.parseInt((String) creditCardObject.get("number"));

                    String insertStatement = "INSERT INTO credit_card (Name, Number) VALUES(?,?)";
                    PreparedStatement preparedStatement =
                            connection.prepareStatement(insertStatement);
                    preparedStatement.setString(1, name);
                    preparedStatement.setInt(2, number);
                    preparedStatement.executeUpdate();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at loadCreditConfig_________________________");
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

    // check for name number and returns if exists
    public boolean creditCardIsValid(String name, int number){
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "SELECT * FROM credit_card WHERE (name = ?) AND (number = ?)";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, number);
            ResultSet result = preparedStatement.executeQuery();

            // result closed if there are no entry
            return !result.isClosed();

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at creditCardIsValid_________________________");
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
        return false;
    }

    public void saveCreditCardInfo(String cardName, int cardNumber, int userID) {
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "UPDATE users " +
                    "SET cardName = ?, cardNumber = ? " +
                    "WHERE userID = ?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setString(1, cardName);
            preparedStatement.setInt(2, cardNumber);
            preparedStatement.setInt(3, userID);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at saveCreditCardInfo_________________");
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

    // return array where first position is cc name and second is cc num
    public ArrayList<String> getCCInfo(String userName){
        ArrayList<String> namePassword = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String insertStatement = "SELECT cardName, cardNumber FROM Users " +
                    "WHERE (? = Users.Username)";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertStatement);
            preparedStatement.setString(1, userName);
            ResultSet result = preparedStatement.executeQuery();

            // no entry
            if(result.isClosed()){
                return namePassword;
            }

            namePassword.add(result.getString("cardName"));
            namePassword.add(result.getString("cardNumber"));

        } catch (Exception e) {
            java.lang.System.out.println("_________________________ERROR at getCCInfo_________________________");
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

        return namePassword;
    }
}
