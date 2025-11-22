package banking;

import java.sql.SQLException;
import java.util.Scanner;

public class BankApp {
  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    System.out.println("Welcome to Pentagon space Bank");
    while (true) {
      System.out.println("1. Create Account");
      System.out.println("2. Login");
      System.out.println("3. Exit");

      Scanner sc = new Scanner(System.in);
      System.out.println("enter your choice");
      int choice = sc.nextInt();
      switch (choice) {
      case 1:
        Bank.createAccount(sc);
        break;
      case 2:
        Bank.login(sc);
        break;
      case 3:
        System.out.println("Thank you");
        System.exit(0);
        break;
      default:
        System.out.println("Invalid choice");

      }
    }
  }
}