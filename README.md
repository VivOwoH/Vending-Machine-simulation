# Vending Machine

To run the application, please use:

gradle run

# Typical use cases

## User
1. Login with username and password. If no account, redirect to sign-up page and create an account.
2. You can show products by category. Click on any product or manually enter its code, then enter the quantity you want. Press enter to add to cart (initial stock = 7).
3. Checkout. You can go back to add more items. Choose payment method.
4. For cash, input valid note/coin. Machine will feed you changes if necessary.
5. For card, enter valid details.
6. Confirm transaction should auto logout. If pay by card, you can choose to save card info or quit without saving.

## Seller
*You can do anything a User can do*
1. Login with username and password.
2. You can redirect to "Seller" admin page.
3. You can update product details in the vending machine.
4. You can generate 1 report on item details, and 1 report on item summary.
- Report details
  - A list of the current available items that include the item details. 
  - A summary that includes items codes, item names and the total number of quantity sold for each item.

## Cashier
*You can do anything a User can do*
1. Login with username and password.
2. You can redirect to "Cashier" admin page.
3. You can update notes/coins in the vending machine.
4. You can generate 1 report on currency details, and 1 report on transaction history.
- Report details
  - A list of the current available change (the quantity of each coin and each note in the vending machine).
  - A summary of transactions that includes transaction date and time, item sold, amount of money paid, returned change and payment method. 

## Owner
*You can do anything a User, Seller, Cashier can do*
1. Login with username and password.
2. You can redirect to "Owner" admin page.
3. You can perform any seller/cashier action. In addition, you can update user roles for existing user accounts.
4. You can generate seller/cashier reports. In addition, you can generate 1 report on users, and 1 report on cancelled transactions.
- Report details
  - A list of usernames in the vending machine with the associated role.
  - A summary of cancelled transaction. This summary only includes date and time of the cancelled, the user and the reasons.   
