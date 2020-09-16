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
    private static String bankId;

    // For Payment domain
    private static boolean internalAccountInstructed;
    private static boolean externalAccountCredited;
    private static boolean paymentNetworkConnected;

    // For Billing domain
    private static String withdrawalId;
    private static String depositId;
    public static void main(String[] args) {
        // Given account id of user
        accountId = "ACCOUNT NUMBER: 0989821187";
        accountBalance = 32768.029;
        // Amount that user wants to send to another account (as payment order)
        amount = 10000.905;
        // One of the 3 commands : withdraw, deposit, payment in order to execute other actors and behaviors
        mainCommand = "Payment";
        // Package is one of `Normal` or `Student` has different product definition features
        userPackage = "Student";
        // Payment order id for requesting sending amount to external account
        paymentOrderId = 892126099L;
        // The id of bank that external account is registered
        bankId = "BANK ID: 1209309204930125CZ";
        // Status of instruction for internal, external account and network connection in Payment domain
        internalAccountInstructed = true;
        externalAccountCredited = true;
        paymentNetworkConnected = false;
        // Withdraw and Deposit Process Ids
        withdrawalId = "WITHDRAWAL ID: 89298420";
        depositId = "DEPOSIT ID: 04932409";

        // Create Account actor and other actors according to type of command user enters 
        if (mainCommand == "Payment") {

                ActorSystem.create(Account.create(accountId, accountBalance, amount, mainCommand, userPackage, paymentOrderId, bankId, 
                withdrawalId, depositId),
                "Account-actor");
                ActorSystem.create(Payment.create(accountId, bankId, internalAccountInstructed, 
                externalAccountCredited, paymentNetworkConnected), "Payment-actor");
                ActorSystem.create(Billing.create(), "Billing-actor");

        } else if ((mainCommand == "Withdraw") || (mainCommand == "Deposit")) {

                ActorSystem.create(Account.create(accountId, accountBalance, amount, mainCommand, userPackage, paymentOrderId, bankId, 
                withdrawalId, depositId),
                "Account-actor");
                ActorSystem.create(Billing.create(), "Billing-actor");                

        }
    }

}

