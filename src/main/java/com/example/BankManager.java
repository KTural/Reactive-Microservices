package com.example;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.Account.*;

import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;

// # reactive-account

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class BankManager extends AbstractBehavior<Account.Command> {

    public static final class InstructBankManager implements Account.Command {

        final String message;
        final ActorRef<BankManagerInstructed> instruct;

        public InstructBankManager(final String message, final ActorRef<BankManagerInstructed> instruct) {

                this.message = message;
                this.instruct = instruct;

        }

    }

    public static final class BankManagerInstructed {

        final String replyTo;

        public BankManagerInstructed(final String replyTo) {

                this.replyTo = replyTo;

        }

    }

    static enum Passivate implements Account.Command {

        INSTANCE

    }

    public static Behavior<Account.Command> create() {

        return Behaviors.setup(context -> new BankManager(context));

    }

    private BankManager(final ActorContext<Account.Command> context) {

        super(context);

        context.getLog().info(" Bank Manager is started \n");

    }

    @Override
    public Receive<Account.Command> createReceive() {

        return newReceiveBuilder().onMessage(InstructBankManager.class, this::onInstructBankManager)
        .onMessage(Passivate.class, m -> Behaviors.stopped()).onSignal(PostStop.class, signal -> onPostStop())
        .build();

    }

    private BankManager onInstructBankManager(final InstructBankManager bankManager) {

        getContext().getLog().info(" BANK MANAGER IS INSTRUCTED \n");

        bankManager.instruct.tell(new BankManagerInstructed("VERIFIED!"));

        System.out.println("\n\n\n INSTRUCTING BANK MANAGER ... \n\n\n");

        return this;

    }


    private Behavior<Account.Command> onPostStop() {
        
        return Behaviors.stopped();

}    
 

    private static String clientName;
    private static String accountId;
    private static String externalAccountId;
    private static Double accountBalance;
    private static Double amount;

    private static String mainCommand;
    private static String userPackage;

    private static Long paymentOrderId;
    private static String bankId;
    private static String currency;
    
    // For Payment domain
    private static boolean internalAccountInstructed;
    private static boolean externalAccountCredited;
    private static boolean paymentNetworkConnected;    

    // For Billing domain
    private static String withdrawalId;
    private static String depositId;

    // Write Logger
    static Logger logger = Logger.getLogger(BankManager.class.getName());

    // read from input
    static Scanner scanner = new Scanner(System.in);

    // main program
    public static void main(String[] args) {
        //Enter client username
        logger.log(Level.INFO, "ENTER CLIENT USERNAME : ");
        String user = scanner.nextLine();
        clientName  = user;
        // internal account id
        logger.log(Level.INFO, "ENTER INTERNAL ACCOUNT ID : ");
        // Given account id of user
        String id = scanner.nextLine();
        accountId = "N: " + id;
        // External Account id of user
        logger.log(Level.INFO, "ENTER EXTERNAL ACCOUNT ID : ");
        String id2 = scanner.nextLine();
        externalAccountId = "N: " + id2;
        // Balance of account
        logger.log(Level.INFO, "ENTER ACCOUNT BALANCE : ");
        Double balance = scanner.nextDouble();
        accountBalance = balance;
        // Amount that user wants to send to another account (as payment order)
        logger.log(Level.INFO, "ENTER AMOUNT THAT YOU WANT TO PROCESS : ");
        Double accAmount = scanner.nextDouble();
        amount = accAmount;
        // One of the 3 commands : withdraw, deposit, payment in order to execute other
        // actors and behaviors
        logger.log(Level.INFO, "ENTER ONE OF THESE COMMANDS - `Payment`, `Withdraw`, `Deposit` : ");
        logger.log(Level.INFO, "\nDEFAULT ORDERED COMMAND IS PAYMENT! YOU CAN CHANGE IT MANUALLY IN BANKMANAGER!\n");
        mainCommand = "Payment";
        // Package is one of `Normal` or `Student` has different product definition
        // features
        logger.log(Level.INFO, "ENTER ONE OF THESE PACKAGES - `Student`, `Normal` - FOR FURTHER PROCESSING : ");
        String pack = scanner.next();
        userPackage = pack;
        // Payment order id for requesting sending amount to external account
        paymentOrderId = 892126099L;
        // The id of bank that external account is registered
        bankId = "N: 1209309204930125CZ";
        // Status of instruction for internal, external account and network connection
        // in Payment domain
        internalAccountInstructed = true;
        externalAccountCredited = true;
        paymentNetworkConnected = true;
        // Withdraw and Deposit Process Ids
        withdrawalId = "WITHDRAWAL ID: 89298420";
        depositId = "DEPOSIT ID: 04932409";
        // Currency that all payments or transactions that will be calculated and
        // processed
        logger.log(Level.INFO, "ENTER VALID CURRENCY : ");
        String accountCurrency = scanner.next();
        currency = accountCurrency;
        // Create Account actor and other actors according to type of command user
        // enters
        if (mainCommand == "Payment") {

            final ActorSystem<Account.Command> account = ActorSystem.create(Account.create(accountId, externalAccountId, accountBalance,
                    amount, mainCommand, userPackage, paymentOrderId, bankId, currency, withdrawalId, depositId),
                    "Account-actor");

            final ActorSystem<Command> payment = ActorSystem.create(Payment.create(accountId, bankId, amount, paymentOrderId,
                    internalAccountInstructed, externalAccountCredited, paymentNetworkConnected), "Payment-actor");

            final ActorSystem<Command> billing = ActorSystem.create(Billing.create(clientName, accountId, externalAccountId, bankId, currency, amount, 
            accountBalance, userPackage, paymentOrderId, withdrawalId, depositId, mainCommand), "Billing-actor");

            try {
                System.out.println(">>> PRESS ENTER TO EXIT ACCOUNT ACTOR <<<\n");
                System.in.read();
            } catch (IOException ignored) {
            } finally {
                account.terminate();
            }

            try {
                System.out.println(">>> PRESS ENTER TO EXIT PAYMENT ACTOR <<<\n");
                System.in.read();
            } catch (IOException ignored) {
            } finally {
                payment.terminate();
            }

            try {
                System.out.println(">>> PRESS ENTER TO EXIT BILLING ACTOR <<<\n");
                System.in.read();
            } catch (IOException ignored) {
            } finally {
                billing.terminate();
            }

        } else if ((mainCommand == "Withdraw") || (mainCommand == "Deposit")) {

            final ActorSystem<Account.Command> account = ActorSystem.create(Account.create(accountId, externalAccountId, accountBalance,
            amount, mainCommand, userPackage, paymentOrderId, bankId, currency, 
            withdrawalId, depositId),
            "Account-actor");                

            final ActorSystem<Command> billing = ActorSystem.create(Billing.create(clientName, accountId, externalAccountId, bankId, currency, amount, 
            accountBalance, userPackage, paymentOrderId, withdrawalId, depositId, mainCommand), "Billing-actor");          

            try {
                System.out.println(">>> PRESS ENTER TO EXIT ACCOUNT ACTOR <<<\n");
                System.in.read();
            } catch (IOException ignored) {
            } finally {
                account.terminate();
            } 
                
            try {
                System.out.println(">>> PRESS ENTER TO EXIT BILLING ACTOR <<<\n");
                System.in.read();
                logger.log(Level.INFO, "ALL ACTORS FINISHED SUCCESSFULLY!");
            } catch (IOException ignored) {
            } finally {
                billing.terminate();
            } 
            
        }

       logger.log(Level.INFO, "ALL ACTORS PROCESSED SUCCESSFULLY!");

    }
    

}