package com.example;

import java.util.Date;

import com.example.Account.*;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.HashMap;

import java.util.Calendar;
import java.time.LocalDate;
import java.time.ZoneId;

public class Billing extends AbstractBehavior<Account.Command> {

    public static final class CalculatePaymentOrderFee implements Account.Command {

        final String accountId;
        final Double amount;
        final String currency;
        protected Double balance;
        protected CompletePaymentOrder billingComplete;
        final String message;
        final ActorRef<PaymentOrderFeeCalculated> replyTo;
  
        public CalculatePaymentOrderFee(final String accountId, final Double amount, final String currency,
                    Double balance, final String message, final ActorRef<PaymentOrderFeeCalculated> replyTo) {

                        this.accountId = accountId;
                        this.amount = amount;
                        this.currency = currency;
                        this.balance = balance;
                        this.message = message;
                        this.replyTo = replyTo;

        }
    }

    public static final class PaymentOrderFeeCalculated {

        final String replyTo;
        protected CalculatePaymentOrderFee calculateFee;

        public PaymentOrderFeeCalculated(final String replyTo) {

                        this.replyTo = replyTo;

        }
    }

    public static final class DebitWithdrawnAccount implements Account.Command {

        final String withdrawalId;
        protected Double balance;
        final String accountId;
        final Date date;
        final Double amount;
        final String currency;
        protected WithdrawalVerified withdraw;
        final String userPackage;
        final String message;
        final ActorRef<WithdrawalCompleted> replyTo;

        public DebitWithdrawnAccount(final String withdrawalId, Double balance, final String userPackage,
                final String accountId, final Date date, final Double amount, final String currency,
                final String message, final ActorRef<WithdrawalCompleted> replyTo) {

                        this.withdrawalId = withdrawalId;
                        this.balance = balance;
                        this.userPackage = userPackage;
                        this.accountId = accountId;
                        this.date = date;
                        this.amount = amount;
                        this.currency = currency;
                        this.message = message;
                        this.replyTo = replyTo;

        }

    }

    public static final class CreditDepositedAccount implements Account.Command {

        final String depositId;
        protected Double balance;
        final String accountId;
        final Date date;
        final Double amount;
        final String currency;
        protected DepositVerified deposit;
        final String userPackage;
        final String message;
        final ActorRef<DepositCompleted> replyTo;

        public CreditDepositedAccount(final String depositId, Double balance, final String userPackage,
                final String accountId, final Date date, final Double amount, final String currency,
                final String message, final ActorRef<DepositCompleted> replyTo) {

                        this.depositId = depositId;
                        this.balance = balance;
                        this.userPackage = userPackage;
                        this.accountId = accountId;
                        this.date = date;
                        this.amount = amount;
                        this.currency = currency;
                        this.message = message;
                        this.replyTo = replyTo;

        }
    }

    public static final class CalculateEndOfMonthBill implements Account.Command {

        final String accountId;
        final Double amount;
        final String currency;
        final long paymentOrderId;
        protected Double balance;
        final String withdrawalId;
        final String depositId;
        final Date date;
        final String userPackage;
        final String message;
        final ActorRef<EndOfMonthBillCalculated> calculateBill;

        public CalculateEndOfMonthBill(final String accountId, final Double amount, final String currency,
                final long paymentOrderId, Double balance, final String withdrawalId, final String depositId, 
                final Date date, final String userPackage, final String message, final ActorRef<EndOfMonthBillCalculated> calculateBill) {

                        this.accountId = accountId;
                        this.amount = amount;
                        this.currency = currency;
                        this.paymentOrderId = paymentOrderId;
                        this.balance = balance;
                        this.withdrawalId = withdrawalId;
                        this.depositId = depositId;
                        this.date = date;
                        this.userPackage = userPackage;
                        this.message = message;
                        this.calculateBill = calculateBill;

                }

    }

    static enum Passivate implements Account.Command {
        INSTANCE;
    }

    final Date date = new Date(System.currentTimeMillis());

    public static Behavior<Account.Command> create(String clientName, String accountId, String externalAccountId, String bankId, String currency, Double amount, 
                Double accountBalance, String userPackage, long paymentOrderId, String withdrawalId, String depositId, String mainCommand) {

        return Behaviors.setup(context -> new Billing(context, clientName, accountId, externalAccountId, bankId, currency, amount, accountBalance, userPackage,
        paymentOrderId, withdrawalId, depositId, mainCommand));

    }

    private final String clientName;
    private final String accountId;
    private final String externalAccountId;
    private final String bankId;
    private final String currency;
    private final Double amount;
    private Double accountBalance;
    private final String userPackage;
    private final long paymentOrderId;
    private final String withdrawalId;
    private final String depositId;
    private final String mainCommand;

    protected long numberOfFeeWithdrawals;
    protected long numberOfFeeDeposits;
    protected long numberOfRequests;
    protected long numberOfWithdrawRequests;
    protected long numberOfDepositRequests;

    protected double studentInterestRate;
    protected double normalInterestRate;
    protected double percentage;

    protected long studentAtmLimit;
    protected long studentAtmFee;
    protected long incrementRequestOrOccurences;

    protected double normalAtmFee;

    protected long studentRequestsOrOccurences;
    protected long studentPackagePaymentFee;

    protected long normalRequestsOrOccurences;
    protected long normalPackagePaymentFee;

    protected Double debitedStudentAmount;
    protected Double debitedNormalAmount;
    protected Double withdrawAmount;
    protected Double depositAmount;

    private Billing(final ActorContext<Command> context, final String clientName, final String accountId, final String externalAccountId, final String bankId,
                final String currency, final Double amount, final Double accountBalance, final String userPackage,
                final long paymentOrderId, final String withdrawalId, final String depositId, final String mainCommand) {

        super(context);

        this.clientName = clientName;
        this.accountId = accountId;
        this.externalAccountId = externalAccountId;
        this.bankId = bankId;
        this.currency = currency;
        this.amount = amount;
        this.accountBalance = accountBalance;
        this.userPackage = userPackage;
        this.paymentOrderId = paymentOrderId;
        this.withdrawalId = withdrawalId;
        this.depositId = depositId;
        this.mainCommand = mainCommand;

        this.numberOfFeeWithdrawals = 0;
        this.numberOfFeeDeposits = 0;
        this.numberOfRequests = 0;
        this.numberOfWithdrawRequests = 0;
        this.numberOfDepositRequests = 0;

        this.studentInterestRate = 1.5;
        this.normalInterestRate = 0.5;

        this.studentAtmLimit = 3;
        this.studentAtmFee = 40;
        this.incrementRequestOrOccurences = 1;

        this.normalAtmFee = 0;

        this.percentage = 100.00;

        this.studentRequestsOrOccurences = 0;
        this.studentPackagePaymentFee = 5;

        this.normalRequestsOrOccurences = 0;
        this.normalPackagePaymentFee = 10;


        context.getLog().info("Billing actor is created\n");

    }

    @Override
    public Receive<Command> createReceive() {

        return newReceiveBuilder().onMessage(CalculatePaymentOrderFee.class, this::onCalculatePaymentOrderFee)
                .onMessage(DebitWithdrawnAccount.class, this::onDebitWithdrawnAccount)
                .onMessage(CreditDepositedAccount.class, this::onCreditDepositedAccount)
                .onMessage(CalculateEndOfMonthBill.class, this::onCalculateEndOfMonthBill)
                .onMessage(Passivate.class, m -> Behaviors.stopped()).onSignal(PostStop.class, signal -> onPostStop())
                .build();

    }

    private Behavior<Command> onCalculatePaymentOrderFee(final CalculatePaymentOrderFee calculateFee) {

        HashMap<String, Object> transactionLog = new HashMap<String, Object>();

        getContext().getLog()
                .info("Payment Order is now being calculated. Interest Rate will be calculated for each P.O requests!\n");

        if (this.amount < this.accountBalance) {

            if (this.userPackage == "Student") {

                if (Account.numberOfPaymentOrderRequests <= Account.studentPackagePaymentOrderLimit) {

                    this.numberOfRequests += incrementRequestOrOccurences;

                    this.accountBalance = (this.accountBalance - (this.studentRequestsOrOccurences + this.amount));

                    Account.numberOfPaymentOrderRequests += incrementRequestOrOccurences;

                    this.debitedStudentAmount = ((this.amount * this.studentInterestRate) / this.percentage)
                            * this.numberOfRequests;

                    this.accountBalance = this.accountBalance - this.debitedStudentAmount;

                    getContext().getLog().info(String.format("%.2f %s is debited from Account due to Interest Rate for Student package!",
                    this.debitedStudentAmount, this.currency));

                    calculateFee.replyTo.tell(new PaymentOrderFeeCalculated("P.O FEE IS CALCULATED!"));

                    System.out.println("\n\n\n CALCULATING PAYMENT ORDER FEE ... \n\n\n");

                } else {

                    this.numberOfRequests += incrementRequestOrOccurences;

                    this.studentRequestsOrOccurences = this.studentRequestsOrOccurences + incrementRequestOrOccurences;

                    this.accountBalance = (this.accountBalance - (this.studentRequestsOrOccurences * this.studentPackagePaymentFee + this.amount));

                    this.debitedStudentAmount = ((this.amount * this.studentInterestRate) / this.percentage)
                            * this.numberOfRequests;

                    this.accountBalance = this.accountBalance - this.debitedStudentAmount; 
                    
                    getContext().getLog().info(String.format("%.2f %s is debited from Account due to Interest Rate for Student package!",
                    this.debitedStudentAmount, this.currency));

                    calculateFee.replyTo.tell(new PaymentOrderFeeCalculated("P.O FEE IS CALCULATED!"));

                    System.out.println("\n\n\n CALCULATING PAYMENT ORDER FEE ... \n\n\n");                    

                }

            } else if (this.userPackage == "Normal") {

                if (Account.numberOfPaymentOrderRequests <= Account.normalPackagePaymentOrderLimit) {

                    this.numberOfRequests += incrementRequestOrOccurences;

                    this.accountBalance = (this.accountBalance - (this.normalRequestsOrOccurences + this.amount));

                    Account.numberOfPaymentOrderRequests += incrementRequestOrOccurences;

                    this.debitedNormalAmount = ((this.amount * this.normalInterestRate) / this.percentage)
                            * this.numberOfRequests;

                    this.accountBalance = this.accountBalance - this.debitedNormalAmount;

                    getContext().getLog().info(String.format("%.2f %s is debited from Account due to Interest Rate for Normal package!",
                    this.debitedNormalAmount, this.currency));                    

                    calculateFee.replyTo.tell(new PaymentOrderFeeCalculated("P.O FEE IS CALCULATED!"));

                    System.out.println("\n\n\n CALCULATING PAYMENT ORDER FEE ... \n\n\n");         
                    
                } else {

                    this.numberOfRequests += incrementRequestOrOccurences;

                    this.normalRequestsOrOccurences = this.normalRequestsOrOccurences + incrementRequestOrOccurences;

                    this.accountBalance = (this.accountBalance 
                            - (this.normalRequestsOrOccurences * this.normalPackagePaymentFee + this.amount));                    

                    this.debitedNormalAmount = ((this.amount * this.normalInterestRate) / this.percentage)
                    * this.numberOfRequests;

                    this.accountBalance = this.accountBalance - this.debitedNormalAmount;

                    getContext().getLog().info(String.format("%.2f %s is debited from Account due to Interest Rate for Normal package!",
                    this.debitedNormalAmount, this.currency));                    

                    calculateFee.replyTo.tell(new PaymentOrderFeeCalculated("P.O FEE IS CALCULATED!"));

                    System.out.println("\n\n\n CALCULATING PAYMENT ORDER FEE ... \n\n\n"); 

                }

            } else {

                getContext().getLog()
                        .info("ERROR! Payment Order could not be calculated! Please Enter relevant order package!\n\n");
                
                System.out.println("\n\n\n FAILING ... \n\n\n");
            }

            getContext().getLog().info(String.format("CURRENT BALANCE : %.2f\n", this.accountBalance));

            getContext().getLog().info(String.format(
                    "TRANSACTION LOG: *PAYMENT ORDER ID* - %o | *CLIENT ACCOUNT ID* - %s | *BANK ID* - %s | *RECEIVER ACCOUNT ID*  - %s |  | *DATE* - %tc | *AMOUNT* - %.2f | *BALANCE* - %.2f %s\n",
                    this.paymentOrderId, this.accountId, this.bankId, this.externalAccountId,
                    this.date, this.amount, this.accountBalance, this.currency));

            transactionLog.put("MAIN COMMAND ORDER ", this.mainCommand);
            transactionLog.put("CLIENT USERNAME ", this.clientName);
            transactionLog.put("PAYMENT ORDER ID ", this.paymentOrderId);
            transactionLog.put("CLIENT ACCOUNT ID ", this.accountId);
            transactionLog.put("BANK ID ", this.bankId);
            transactionLog.put("RECEIVER ACCOUNT ID ", this.externalAccountId);
            transactionLog.put("USER PACKAGE ", this.userPackage);
            transactionLog.put("NUMBER OF PAYMENT ORDER REQUESTS ", this.numberOfRequests);
            transactionLog.put("DATE", this.date);
            transactionLog.put("AMOUNT", this.amount);
            transactionLog.put("ACCOUNT BALANCE", this.accountBalance);
            transactionLog.put("CURRENCY", this.currency);

            System.out.println("\n\n\n TRANSACTION LOG DETAILS : \n\n\n");

            for (String i : transactionLog.keySet()) {
                System.out.println(" " + i + " : " + transactionLog.get(i) + " ");
            }

            System.out.println("\n");

        } else {

            getContext().getLog().info("NOT ENOUGH BALANCE!!!\n");

            System.out.println("\n\n\n FAILING ... \n\n\n");

        }

        return this;

    }

    private Behavior<Command> onDebitWithdrawnAccount(final DebitWithdrawnAccount debitAccount) {

        HashMap<String, Object> debitInfoTransactionLog = new HashMap<String, Object>();

        getContext().getLog().info(String.format("Current Account Balance : %.2f %s. Debitting Withdrawn Account\n",
        this.accountBalance, this.currency));

        if (this.amount < this.accountBalance) {

            if (this.userPackage == "Student") {

                if (this.numberOfFeeWithdrawals <= this.studentAtmLimit) {

                    this.numberOfRequests += incrementRequestOrOccurences;

                    this.accountBalance = this.accountBalance - (this.numberOfRequests + this.amount);

                    this.numberOfFeeWithdrawals += this.incrementRequestOrOccurences;

                    debitAccount.replyTo.tell(new WithdrawalCompleted(this.withdrawalId, this.accountBalance, this.userPackage,
                    this.accountId, this.date, this.amount, "WITHDRAWN!"));

                    getContext().getLog().info("Account with Id - `{}` is debited with withdrawal Id - `{}` for package - `{}` with amount - `{}` on {}",
                    this.accountId, this.withdrawalId, this.userPackage, this.amount, this.date);

                    getContext().getLog().info(String.format("%.2f %s is withdrawn. Final Account Balance : %.2f. STATUS : %s. NUMBER OF REQUESTS : %o", 
                    this.numberOfRequests + this.amount, this.currency, this.accountBalance, "PROCESSED!", this.numberOfRequests));

                    System.out.println("\n\n\n DEBITING WITHDRAWN ACCOUNT ... \n\n\n");                  

                } else {

                    this.numberOfRequests += incrementRequestOrOccurences;

                    this.accountBalance = this.accountBalance - (this.numberOfRequests * studentAtmFee + this.amount);

                    debitAccount.replyTo.tell(new WithdrawalCompleted(this.withdrawalId, this.accountBalance, this.userPackage,
                    this.accountId, this.date, this.amount, "WITHDRAWN!"));

                    getContext().getLog().info("Account with Id - `{}` is debited with withdrawal Id - `{}` for package - `{}` with amount - `{}` on {}",
                    this.accountId, this.withdrawalId, this.userPackage, this.amount, this.date);

                    getContext().getLog().info(String.format("%.2f %s is withdrawn. Final Account Balance : %.2f. STATUS : %s. NUMBER OF REQUESTS : %o", 
                    this.numberOfRequests * studentAtmFee + this.amount, this.currency, this.accountBalance, "PROCESSED!", this.numberOfRequests));            

                    System.out.println("\n\n\n DEBITING WITHDRAWN ACCOUNT ... \n\n\n");                    

                }

                debitInfoTransactionLog.put("CLIENT USERNAME ", this.clientName);
                debitInfoTransactionLog.put("WITHDRAWAL ID", this.withdrawalId);
                debitInfoTransactionLog.put("USER PACKAGE ", this.userPackage);
                debitInfoTransactionLog.put("ACCOUNT BALANCE ", this.accountBalance);
                debitInfoTransactionLog.put("AMOUNT ", this.amount);
                debitInfoTransactionLog.put("ACCOUNT ID ", this.accountId);
                debitInfoTransactionLog.put("BANK ID ", this.bankId);
                debitInfoTransactionLog.put("NUMBER OF WITHDRAWAL REQUESTS ", this.numberOfRequests);
                debitInfoTransactionLog.put("CURRENCY ", this.currency);
                debitInfoTransactionLog.put("DATE ", this.date);

                System.out.println("\n\n\n TRANSACTION LOG DETAILS : \n\n\n");

                for (String i : debitInfoTransactionLog.keySet()) {
                    System.out.println(" " + i + " : " + debitInfoTransactionLog.get(i) + " ");
                }  

                System.out.println("\n");

            } else if (this.userPackage == "Normal") {

                this.accountBalance = this.accountBalance - (normalAtmFee + this.amount);

                debitAccount.replyTo.tell(new WithdrawalCompleted(this.withdrawalId, this.accountBalance, this.userPackage,
                this.accountId, this.date, this.amount, "WITHDRAWN!"));

                getContext().getLog().info("Account with Id - `{}` is debited with withdrawal Id - `{}` for package - `{}` with amount - `{}` on {}",
                this.accountId, this.withdrawalId, this.userPackage, this.amount, this.date);

                getContext().getLog().info(String.format("%.2f %s is withdrawn. Final Account Balance : %.2f. STATUS : %s", 
                this.normalAtmFee + this.amount, this.currency, this.accountBalance, "PROCESSED!")); 
                
                System.out.println("\n\n\n DEBITING WITHDRAWN ACCOUNT ... \n\n\n"); 
                
                debitInfoTransactionLog.put("CLIENT USERNAME ", this.clientName);
                debitInfoTransactionLog.put("WITHDRAWAL ID", this.withdrawalId);
                debitInfoTransactionLog.put("USER PACKAGE ", this.userPackage);
                debitInfoTransactionLog.put("ACCOUNT BALANCE ", this.accountBalance);
                debitInfoTransactionLog.put("AMOUNT ", this.amount);
                debitInfoTransactionLog.put("ACCOUNT ID ", this.accountId);
                debitInfoTransactionLog.put("BANK ID ", this.bankId);
                debitInfoTransactionLog.put("CURRENCY ", this.currency);
                debitInfoTransactionLog.put("DATE ", this.date);

                System.out.println("\n\n\n TRANSACTION LOG DETAILS : \n\n\n");

                for (String i : debitInfoTransactionLog.keySet()) {
                    System.out.println(" " + i + " : " + debitInfoTransactionLog.get(i) + " ");
                }              
                
                System.out.println("\n");

            } else {

                getContext().getLog().info("ERROR! PLEASE, ENTER VALID PACKAGE NAME!\n");

                System.out.println("\n\n\n FAILING ...\n\n\n");

            }
        } else {

                debitAccount.replyTo.tell(new WithdrawalCompleted(this.withdrawalId, this.accountBalance, this.userPackage,
                this.accountId, this.date, this.amount, "FAILED!"));

                getContext().getLog().info("NOT ENOUGH BALANCE TO PROCESS FURTHER!\n");

                System.out.println("\n\n\n FAILING WITHDRAWING ACCOUNT ...\n\n\n");

        }

        return this;

    }

    private Behavior<Command> onCreditDepositedAccount(final CreditDepositedAccount creditAccount) {

        HashMap<String, Object> creditInfoTransactionLog = new HashMap<String, Object>();

        getContext().getLog().info(String.format("Current Account Balance : %.2f %s. Crediting Deposited Account\n",
        this.accountBalance, this.currency));

        if (this.userPackage == "Student") {

            if (this.numberOfFeeDeposits <= this.studentAtmLimit) {

                this.numberOfRequests += incrementRequestOrOccurences;

                this.accountBalance = this.accountBalance + (this.amount - numberOfRequests * studentAtmFee);

                this.numberOfFeeDeposits += this.incrementRequestOrOccurences;

                creditAccount.replyTo.tell(new DepositCompleted(this.depositId, this.accountBalance, this.userPackage,
                this.accountId, this.date, this.amount, "DEPOSITED!"));

                getContext().getLog().info("Account with Id - `{}` is credited with deposit Id - `{}` for package - `{}` with amount - `{}` on {}",
                this.accountId, this.depositId, this.userPackage, this.amount, this.date);

                getContext().getLog().info(String.format("%.2f %s is credited. Final Account Balance : %.2f. STATUS : %s. NUMBER OF REQUESTS : %o", 
                (this.amount - numberOfRequests * studentAtmFee), this.currency, 
                this.accountBalance, "PROCESSED!", this.numberOfRequests));

                System.out.println("\n\n\n CREDITING DEPOSITED ACCOUNT ... \n\n\n");            
                
            } else {

                this.numberOfRequests += incrementRequestOrOccurences;

                this.accountBalance = this.accountBalance + (this.amount - numberOfRequests * studentAtmFee);

                creditAccount.replyTo.tell(new DepositCompleted(this.depositId, this.accountBalance, this.userPackage,
                this.accountId, this.date, this.amount, "DEPOSITED!"));

                getContext().getLog().info("Account with Id - `{}` is credited with deposit Id - `{}` for package - `{}` with amount - `{}` on {}",
                this.accountId, this.depositId, this.userPackage, this.amount, this.date);

                getContext().getLog().info(String.format("%.2f %s is credited. Final Account Balance : %.2f. STATUS : %s. NUMBER OF REQUESTS : %o", 
                (this.amount - numberOfRequests * studentAtmFee), this.currency, 
                this.accountBalance, "PROCESSED!", this.numberOfRequests));
                
                System.out.println("\n\n\n CREDITING DEPOSITED ACCOUNT ... \n\n\n");                

            }

            creditInfoTransactionLog.put("CLIENT USERNAME ", this.clientName);
            creditInfoTransactionLog.put("DEPOSIT ID", this.depositId);
            creditInfoTransactionLog.put("USER PACKAGE ", this.userPackage);
            creditInfoTransactionLog.put("ACCOUNT BALANCE ", this.accountBalance);
            creditInfoTransactionLog.put("AMOUNT ", this.amount);
            creditInfoTransactionLog.put("ACCOUNT ID ", this.accountId);
            creditInfoTransactionLog.put("BANK ID ", this.bankId);
            creditInfoTransactionLog.put("NUMBER OF DEPOSIT REQUESTS ", this.numberOfRequests);
            creditInfoTransactionLog.put("CURRENCY ", this.currency);
            creditInfoTransactionLog.put("DATE ", this.date);

            System.out.println("\n\n\n TRANSACTION LOG DETAILS : \n\n\n");

            for (String i : creditInfoTransactionLog.keySet()) {
                System.out.println(" " + i + " : " + creditInfoTransactionLog.get(i) + " ");
            }  

            System.out.println("\n");

        } else if (creditAccount.userPackage == "Normal") {

            this.accountBalance = this.accountBalance + (this.amount - normalAtmFee);

            creditAccount.replyTo.tell(new DepositCompleted(this.depositId, this.accountBalance, this.userPackage,
            this.accountId, this.date, this.amount, "DEPOSITED!"));

            getContext().getLog().info("Account with Id - `{}` is credited with deposit Id - `{}` for package - `{}` with amount - `{}` on {}",
            this.accountId, this.depositId, this.userPackage, this.amount, this.date);

            getContext().getLog().info(String.format("%.2f %s is credited. Final Account Balance : %.2f. STATUS : %s", 
            (this.amount - numberOfRequests * studentAtmFee), this.currency, 
            this.accountBalance, "PROCESSED!"));           

            System.out.println("\n\n\n CREDITING DEPOSITED ACCOUNT ... \n\n\n");

            creditInfoTransactionLog.put("CLIENT USERNAME ", this.clientName);
            creditInfoTransactionLog.put("DEPOSIT ID", this.depositId);
            creditInfoTransactionLog.put("USER PACKAGE ", this.userPackage);
            creditInfoTransactionLog.put("ACCOUNT BALANCE ", this.accountBalance);
            creditInfoTransactionLog.put("AMOUNT ", this.amount);
            creditInfoTransactionLog.put("ACCOUNT ID ", this.accountId);
            creditInfoTransactionLog.put("BANK ID ", this.bankId);
            creditInfoTransactionLog.put("CURRENCY ", this.currency);
            creditInfoTransactionLog.put("DATE ", this.date);

            System.out.println("\n\n\n TRANSACTION LOG DETAILS : \n\n\n");

            for (String i : creditInfoTransactionLog.keySet()) {
                System.out.println(" " + i + " : " + creditInfoTransactionLog.get(i) + " ");
            }  

            System.out.println("\n");

        } else {

            getContext().getLog().info("ERROR! PLEASE, ENTER VALID PACKAGE NAME!\n");

            System.out.println("\n\n\n FAILING ...\n\n\n");

        }        

        return this;

    }

    private Behavior<Command> onCalculateEndOfMonthBill(final CalculateEndOfMonthBill calculateMonthly) {

        HashMap<String, Object> endOfMonthBill = new HashMap<String, Object>();

        // OVERALL BILL FOR PAYMENT ORDER (STUDENT OR NORMAL PACKAGES)

        if (this.amount < this.accountBalance) {

            if (this.userPackage == "Student") {

                if (Account.numberOfPaymentOrderRequests <= Account.studentPackagePaymentOrderLimit) {

                    this.numberOfRequests += incrementRequestOrOccurences;

                    this.accountBalance = (this.accountBalance - (this.studentRequestsOrOccurences + this.amount));

                    Account.numberOfPaymentOrderRequests += incrementRequestOrOccurences;

                    this.debitedStudentAmount = ((this.amount * this.studentInterestRate) / this.percentage)
                            * this.numberOfRequests;

                    this.accountBalance = this.accountBalance - this.debitedStudentAmount;

                } else {

                    this.numberOfRequests += incrementRequestOrOccurences;

                    this.studentRequestsOrOccurences = this.studentRequestsOrOccurences + incrementRequestOrOccurences;

                    this.accountBalance = (this.accountBalance - (this.studentRequestsOrOccurences * this.studentPackagePaymentFee + this.amount));

                    this.debitedStudentAmount = ((this.amount * this.studentInterestRate) / this.percentage)
                            * this.numberOfRequests;

                    this.accountBalance = this.accountBalance - this.debitedStudentAmount;                   

                }

            } else if (this.userPackage == "Normal") {

                if (Account.numberOfPaymentOrderRequests <= Account.normalPackagePaymentOrderLimit) {

                    this.numberOfRequests += incrementRequestOrOccurences;

                    this.accountBalance = (this.accountBalance - (this.normalRequestsOrOccurences + this.amount));

                    Account.numberOfPaymentOrderRequests += incrementRequestOrOccurences;

                    this.debitedNormalAmount = ((this.amount * this.normalInterestRate) / this.percentage)
                            * this.numberOfRequests;

                    this.accountBalance = this.accountBalance - this.debitedNormalAmount;    
                    
                } else {

                    this.numberOfRequests += incrementRequestOrOccurences;

                    this.normalRequestsOrOccurences = this.normalRequestsOrOccurences + incrementRequestOrOccurences;

                    this.accountBalance = (this.accountBalance 
                            - (this.normalRequestsOrOccurences * this.normalPackagePaymentFee + this.amount));                    

                    this.debitedNormalAmount = ((this.amount * this.normalInterestRate) / this.percentage)
                    * this.numberOfRequests;

                    this.accountBalance = this.accountBalance - this.debitedNormalAmount;

                }

            } else {

                getContext().getLog()
                        .info("ERROR! Please Enter relevant order package!\n\n");
                
                System.out.println("\n\n\n FAILING ... \n\n\n");
            }

            System.out.println("\n");

        } else {

            getContext().getLog().info("NOT ENOUGH BALANCE!!!\n");

            System.out.println("\n\n\n FAILING ... \n\n\n");

        }


        // OVERALL BILL FOR WITHDRAWAL ORDER (STUDENT OR NORMAL PACKAGES)

        if (this.amount < this.accountBalance) {

            if (this.userPackage == "Student") {

                if (this.numberOfFeeWithdrawals <= this.studentAtmLimit) {

                    this.numberOfWithdrawRequests += incrementRequestOrOccurences;

                    this.accountBalance = this.accountBalance - (this.numberOfWithdrawRequests + this.amount);

                    this.withdrawAmount = (this.numberOfWithdrawRequests + this.amount);

                    this.numberOfFeeWithdrawals += this.incrementRequestOrOccurences;                

                } else {

                    this.numberOfWithdrawRequests += incrementRequestOrOccurences;

                    this.accountBalance = this.accountBalance - (this.numberOfWithdrawRequests * studentAtmFee + this.amount);                   

                    this.withdrawAmount = (this.numberOfWithdrawRequests * studentAtmFee + this.amount); 
                }

                System.out.println("\n");

            } else if (this.userPackage == "Normal") {

                this.accountBalance = this.accountBalance - (normalAtmFee + this.amount);   
                
                this.withdrawAmount = (normalAtmFee + this.amount); 
                
                System.out.println("\n");

            } else {

                getContext().getLog().info("ERROR! PLEASE, ENTER VALID PACKAGE NAME!\n");

                System.out.println("\n\n\n FAILING ...\n\n\n");

            }
        } else {

                getContext().getLog().info("NOT ENOUGH BALANCE TO PROCESS FURTHER!\n");

                System.out.println("\n\n\n FAILING WITHDRAWING ACCOUNT ...\n\n\n");

        }

        // OVERALL BILL FOR DEPOSIT ORDER (STUDENT OR NORMAL)

        if (this.userPackage == "Student") {

            if (this.numberOfFeeDeposits <= this.studentAtmLimit) {

                this.numberOfDepositRequests += incrementRequestOrOccurences;

                this.accountBalance = this.accountBalance + (this.amount - numberOfDepositRequests * studentAtmFee);

                this.depositAmount = (this.amount - numberOfDepositRequests * studentAtmFee);

                this.numberOfFeeDeposits += this.incrementRequestOrOccurences;           
                
            } else {

                this.numberOfDepositRequests += incrementRequestOrOccurences;

                this.accountBalance = this.accountBalance + (this.amount - numberOfDepositRequests * studentAtmFee);   
                
                this.depositAmount = (this.amount - numberOfDepositRequests * studentAtmFee);  

            }

            System.out.println("\n");

        } else if (this.userPackage == "Normal") {

            this.accountBalance = this.accountBalance + (this.amount - normalAtmFee); 

            this.depositAmount = (this.amount - normalAtmFee); 

            System.out.println("\n");

        } else {

            getContext().getLog().info("ERROR! PLEASE, ENTER VALID PACKAGE NAME!\n");

            System.out.println("\n\n\n FAILING ...\n\n\n");

        }                

        endOfMonthBill.put("MAIN COMMAND ORDER ", this.mainCommand);
        endOfMonthBill.put("CLIENT USERNAME ", this.clientName);
        endOfMonthBill.put("PAYMENT ORDER ID ", this.paymentOrderId);
        endOfMonthBill.put("WITHDRAWAL ID", this.withdrawalId);
        endOfMonthBill.put("DEPOSIT ID", this.depositId);
        endOfMonthBill.put("ACCOUNT ID ", this.accountId);
        endOfMonthBill.put("RECEIVER ACCOUNT ID ", this.externalAccountId);
        endOfMonthBill.put("USER PACKAGE ", this.userPackage);
        endOfMonthBill.put("ACCOUNT BALANCE ", this.accountBalance);
        endOfMonthBill.put("REQUESTED AMOUNT ", this.amount);
        endOfMonthBill.put("BANK ID ", this.bankId);      
        endOfMonthBill.put("NUMBER OF PAYMENT ORDER REQUESTS ", this.numberOfRequests); 
        endOfMonthBill.put("NUMBER OF WITHDRAWAL REQUESTS ", this.numberOfRequests);
        endOfMonthBill.put("NUMBER OF DEPOSIT REQUESTS ", this.numberOfRequests);
        endOfMonthBill.put("DEBITED STUDENT PACKAGE AMOUNT ", this.debitedStudentAmount);
        endOfMonthBill.put("DEBITED NORMAL PACKAGE AMOUNT ", this.debitedNormalAmount);
        endOfMonthBill.put("WITHDRAWN AMOUNT ", this.withdrawAmount);
        endOfMonthBill.put("DEPOSITED AMOUNT ", this.depositAmount);
        endOfMonthBill.put("CURRENCY ", this.currency);
        endOfMonthBill.put("DATE ", this.date);;

        Calendar cal = Calendar.getInstance();

        Date billingDate = new Date();

        LocalDate localDate = billingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        int currentMonth = localDate.getMonthValue();
        int currentDay = localDate.getDayOfMonth();

        int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);

        switch (currentMonth) {

            case 1:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n JANUARY. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }

            case 2:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n FEBRUARY. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }  
                
            case 3:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n MARCH. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }    

            case 4:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n APRIL. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }
                
            case 5:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n MAY. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }
                
            case 6:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n JUNE. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }  
                
            case 7:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n JULY. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }  
                
            case 8:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n AUGUST. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }  
                
            case 9:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n SEPTEMBER. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }  
                
            case 10:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n OCTOBER. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }  
                
            case 11:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n NOVEMBER. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {
                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");
                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }   
                
            case 12:

                if (currentDay == lastDayOfMonth) {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("END OF MONTH BILL IS CALCULATED!"));

                    System.out.println("\n\n\n DECEMBER. END OF MONTH BILL REPORT ... \n\n\n");

                    for (String i : endOfMonthBill.keySet()) {

                        System.out.println(" " + i + " : " + endOfMonthBill.get(i) + " ");

                    } 

                } else {

                    calculateMonthly.calculateBill.tell(new EndOfMonthBillCalculated("VERIFIED!"));

                    break;

                }    
                
            default:

                System.out.println("\n INVALID MONTH \n");

        }

        return this;

    }

    private Behavior<Command> onPostStop() {

        getContext().getLog().info("Billing actor is stopped\n");
        
        return Behaviors.stopped();

    }

}