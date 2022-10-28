package com.example.a2;

public class Owner implements Role {

    public String modifyRole(Sys system, int userID, String newRole) {
        for (User user : system.getUserbase().getUserList()) {
            if (user.getID() == userID) {
                switch (newRole) {
                    case "Owner":
                        user.setRole(new Owner());
                        break;
                    case "Seller":
                        user.setRole(new Seller());
                        break;
                    case "Cashier":
                        user.setRole(new Cashier());
                        break;
                    case "User":
                        user.setRole(null);
                        break;
                    default:
                        System.out.println("Modify role: Something went wrong.");
                        return null;
                }
            }
        }
        System.out.println(String.format("New role %s set for user:%d", newRole, userID));
        return String.format("New role %s set for user:%d", newRole, userID);
    };

    public void getReport(Sys system) {

    };
}