package com.example;

// # reactive-account

import akka.actor.typed.ActorSystem;

public class BankManager {

    private static String accountId;
    private static Double accountBalance;
    private static Double amount;

    private static String mainCommand;
    private static String userPackage;

    private static Long paymentOrderId;
    private static long bankId;

    public static void main(String[] args) {
        // Given account id of user
        accountId = "ACCOUNT NUMBER: 09898211873";
        accountBalance = 32768.029;
        // Amount that user wants to send to another account (as payment order)
        amount = 10000.90;
        // One of the 3 commands : withdraw, deposit, payment in order to execute other actors and behaviors
        mainCommand = "Payment";
        // Package is one of `Normal` or `Student` has different product definition features
        userPackage = "Student";
        // Payment order id for requesting sending amount to external account
        paymentOrderId = 892126099L;
        // The id of bank that external account is registered
        bankId = 1209309204930L;
        // Create Account actor
        ActorSystem.create(Account.create(accountId, accountBalance, amount, mainCommand, userPackage, paymentOrderId, bankId),
        "Account-actor");
    }

}

