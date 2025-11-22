package banking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Bank {
  static Connection con = null;
  static PreparedStatement ps = null;
  static int loggedIn = 0;

  static void createAccount(Scanner sc) throws ClassNotFoundException, SQLException {
    System.out.println("enter the name");
    String name = sc.next();
    sc.nextLine();
    System.out.println("enter the email");
    String mail = sc.nextLine();
    System.out.println("enter the password");
    String pass = sc.nextLine();
    System.out.println("enter the initial balance");
    double bal = sc.nextDouble();
    con = ProvideConnection.giveusConnection();
    String iqry = "insert into bankaccount(name,email,password,balance) values(?,?,?,?)";
    PreparedStatement ps = con.prepareStatement(iqry);
    ps.setString(1, name);
    ps.setString(2, mail);
    ps.setString(3, pass);
    ps.setDouble(4, bal);
    int rs = ps.executeUpdate();
    if (rs > 0) {
      System.out.println("Account created Successfully");
    } else {
      System.out.println("failed to create account");
    }
  }

  static void login(Scanner sc) throws ClassNotFoundException, SQLException {
    System.out.println("Enter the email");
    String email = sc.next();
    sc.nextLine();
    System.out.println("enter the password");
    String pass = sc.nextLine();
    String sqry = "select * from bankaccount where email=? and password=?";
    con = ProvideConnection.giveusConnection();
    ps = con.prepareStatement(sqry);

    ps.setString(1, email);
    ps.setString(2, pass);

    ResultSet rs = ps.executeQuery();
    if (rs.next()) {
      System.out.println("logged in sucess");
      System.out.println("welcome " + rs.getString("name"));
      loggedIn = rs.getInt(1);
      if (loggedIn != 0) {
        loginMenu(sc);
      }

    } else {
      System.out.println("no record found");
      System.out.println("Loggin failed");
    }

  }
//  withdraw()
//  deposit()
//  viewAccount()
//  transferAmount()
//  transactionHistory()

  private static void loginMenu(Scanner sc) throws SQLException {
    while (true) {
      System.out.println("1. withdraw");
      System.out.println("2. deposit");
      System.out.println("3. View Account");
      System.out.println("4. transfer Amount");
      System.out.println("5 .transaction");
      System.out.println("6. logout");
      System.out.println("Enter your choice");
      int choice = sc.nextInt();

      switch (choice) {
      case 1:
        withdraw(sc);
        break;
      case 2:
        deposit(sc);
        break;
      case 3:
        viewAccount();
        break;
      case 4:
        transfer(sc);
        break;
      case 5:
        transactionHistory();
        break;
      case 6:
        System.out.println("logged out");
        return;
      default:
        System.out.println("Invalid choice");
        break;
      }
    }
  }

  private static void transactionHistory() throws SQLException {
    String qry = "select * from transaction where accNum=? order by date DESC";
    ps = con.prepareStatement(qry);
    ps.setInt(1, loggedIn);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      System.out.println("trx_id :" + rs.getInt(1) + " |  trx_type :" + rs.getString(2) + " |  trx_amnt :"
          + rs.getDouble(3) + " |  trx_Account ID :" + rs.getInt(4) + " |  trx_date :" + rs.getTimestamp(5));
    }

  }
  private static void transfer(Scanner sc) {
	    System.out.println("enter the account id to send amount");
	    int id = sc.nextInt();
	    System.out.println("Enter the amount to transfer");
	    double amnt = sc.nextDouble();
	    try {
	      con.setAutoCommit(false);
	      String qry1 = "update bankaccount set balance=balance-? where id=?";
	      ps = con.prepareStatement(qry1);
	      ps.setDouble(1, amnt);
	      ps.setInt(2, loggedIn);
	      int res = ps.executeUpdate();
	      if (res > 0) {
	        System.out.println("Amount deducting from your account....");
	        recordTrnsactionHistory("debit ", amnt, loggedIn);
	      } else {
	        System.out.println("Failed");
	      }
	      String qry2 = "update bankaccount set balance=balance+? where id=?";
	      ps = con.prepareStatement(qry2);
	      ps.setDouble(1, amnt);
	      ps.setInt(2, id);
	      int res1 = ps.executeUpdate();
	      if (res1 > 0) {
	          System.out.println("Successfully transfered to account id " + id);
	          recordTrnsactionHistory("credit", amnt, id);
	      } else {
	          System.out.println("Transfer failed: no account found with id " + id);
	          con.rollback(); // rollback if needed
	          return;
	      }

	      recordTrnsactionHistory("credit", amnt, id);
	    } catch (SQLException e) {
	      try {
	        System.out.println("transfer failed");
	        con.rollback();
	      } catch (SQLException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
	      }
	    } finally {
	      try {
	        con.commit();
	      } catch (SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }
	    }

	  }

	  private static void viewAccount() throws SQLException {
	    System.out.println("Your account information :");
	    String sqry = "select * from bankaccount where id=?";
	    ps = con.prepareStatement(sqry);
	    ps.setInt(1, loggedIn);
	    ResultSet rs = ps.executeQuery();
	    if (rs.next()) {
	      System.out.println("your Account id : " + rs.getInt("id"));
	      System.out.println("Account Holder name : " + rs.getString("name"));
	      System.out.println("your email id : " + rs.getString("email"));
	      System.out.println("your balance is : " + rs.getDouble("balance"));
	    } else {
	      System.out.println("no record found");
	    }

	  }

	  private static void deposit(Scanner sc) throws SQLException {
	    System.out.println("Enter the amount");
	    double amnt = sc.nextDouble();
	    String uqry = "update bankaccount set balance=balance+? where id=?";
	    ps = con.prepareStatement(uqry);
	    ps.setDouble(1, amnt);
	    ps.setInt(2, loggedIn);
	    int res = ps.executeUpdate();
	    if (res > 0) {
	      recordTrnsactionHistory("deposit", amnt, loggedIn);
	      System.out.println("deposit successfull of amount " + amnt);

	    } else {
	      System.out.println("failed to deposit");
	    }
	  }

	  private static void withdraw(Scanner sc) throws SQLException {
	    System.out.println("Enter the amount");
	    double amnt = sc.nextDouble();

	    String sqry = "select balance from bankaccount where id=?";
	    ps = con.prepareStatement(sqry);
	    ps.setInt(1, loggedIn);
	    ResultSet rs = ps.executeQuery();
	    if (rs.next()) {
	      if (amnt <= rs.getDouble("balance")) {
	        String uqry = "update bankaccount set balance=balance-? where id=?";
	        ps = con.prepareStatement(uqry);
	        ps.setDouble(1, amnt);
	        ps.setInt(2, loggedIn);
	        int res = ps.executeUpdate();
	        if (res > 0) {
	          System.out.println("Withdraw successfull of amount " + amnt);
	          recordTrnsactionHistory("withdraw", amnt, loggedIn);

	        } else {
	          System.out.println("failed to withdraw");
	        }
	      } else {
	        System.out.println("Insufficient balance");
	      }
	    } else {
	      System.out.println("no record found");
	    }

	  }

	  private static void recordTrnsactionHistory(String type, double amnt, int id) throws SQLException {
	    String iqry = "insert into transaction(type,amount,accNum,date) values(?,?,?,?)";
	    
	    ps = con.prepareStatement(iqry);
	    ps.setString(1, type);
	    ps.setDouble(2, amnt);
	    ps.setInt(3, id);
	    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
	    int res1 = ps.executeUpdate();
	    if (res1 > 0) {
	        System.out.println("Transaction saved ");
	    } else {
	        System.out.println("Transaction not saved");
	    }

	    
	  }
	}