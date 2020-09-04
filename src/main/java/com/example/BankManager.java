package com.example;

// # reactive-account

import akka.actor.typed.ActorSystem;

public class BankManager {

    private static String accountId;
    private static Double accountBalance;
    private static Double amount;

    public static void main(String[] args) {
        // Given account id of user
        accountId = "ACCOUNT NUMBER: 09898211873";
        accountBalance = 32768.029;
        // Amount that user wants to send to another account (as payment order)
        amount = 10000.90;

        // Create Account actor
        ActorSystem.create(Account.create(accountId, accountBalance, amount), "Account-actor");
    }

}

