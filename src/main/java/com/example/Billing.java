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

public class Billing extends AbstractBehavior<Account.Command> {

    public static final class CalculatePaymentOrderFee implements Account.Command {

        final String accountId;
        final Double amount;
        final String currency;
        protected Double balance;
        protected CompletePaymentOrder billingComplete;
        protected ActorRef<PaymentOrderFeeCalculated> replyTo;
  
        public CalculatePaymentOrderFee(final String accountId, final Double amount, final String currency,
                    Double balance) {

                        this.accountId = billingComplete.accountId;
                        this.amount = billingComplete.amount;
                        this.currency = currency;
                        this.balance = billingComplete.balance;

        }
    }

    public static final class PaymentOrderFeeCalculated {

        final String replyTo;
        protected CalculatePaymentOrderFee calculateFee;

        public PaymentOrderFeeCalculated(final String replyTo, Double balance, String currency) {

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
        protected ActorRef<WithdrawalCompleted> replyTo;

        public DebitWithdrawnAccount(final String withdrawalId, Double balance, final String userPackage,
                final String accountId, final Date date, final Double amount, final String currency) {

            this.withdrawalId = withdrawalId;
            this.balance = balance;
            this.userPackage = userPackage;
            this.accountId = accountId;
            this.date = date;
            this.amount = amount;
            this.currency = currency;

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
        protected ActorRef<DepositCompleted> replyTo;

        public CreditDepositedAccount(final String depositId, Double balance, final String userPackage,
                final String accountId, final Date date, final Double amount, final String currency) {

            this.depositId = depositId;
            this.balance = balance;
            this.userPackage = userPackage;
            this.accountId = accountId;
            this.date = date;
            this.amount = amount;
            this.currency = currency;

        }
    }

    public static final class CalculateEndOfMonthBill implements Account.Command {

        protected ActorRef<EndOfMonthBillCalculated> calculateBill;

    }

    static enum Passivate implements Account.Command {
        INSTANCE;
    }

    public static Behavior<Account.Command> create() {

        return Behaviors.setup(context -> new Billing(context));

    }

    protected long numberOfFeeWithdrawals;
    protected long numberOfFeeDeposits;
    protected long numberOfRequests;

    protected double studentInterestRate;
    protected double normalInterestRate;
    protected double percentage;

    protected long studentAtmLimit;
    protected long studentAtmFee;
    protected long incrementRequestOrOccurences;

    protected double normalAtmFee;

    private Billing(final ActorContext<Command> context) {

        super(context);

        this.numberOfFeeWithdrawals = 0;
        this.numberOfFeeDeposits = 0;
        this.numberOfRequests = 0;

        this.studentInterestRate = 1.5;
        this.normalInterestRate = 0.5;

        this.studentAtmLimit = 3;
        this.studentAtmFee = 40;
        this.incrementRequestOrOccurences = 1;

        this.normalAtmFee = 0;

        this.percentage = 100.00;

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

        getContext().getLog()
                .info("Payment Order is now calculated. Interest Rate will be calculated for each P.O requests!\n");

        if (calculateFee.billingComplete.creditExternal.creditAccount.identify.instruct.userPackage == "Student") {

            double debitedStudentAmount = ((calculateFee.amount * this.studentInterestRate) / this.percentage)
                    * this.numberOfRequests;

            calculateFee.balance = calculateFee.balance - debitedStudentAmount;

            calculateFee.replyTo.tell(new PaymentOrderFeeCalculated(
                    "{} {} is debited from Account due to Interest Rate for Student package!", debitedStudentAmount,
                    calculateFee.currency));

        } else if (calculateFee.billingComplete.creditExternal.creditAccount.identify.instruct.userPackage == "Normal") {

            double debitedNormalAmount = ((calculateFee.amount * this.normalInterestRate) / this.percentage)
                    * this.numberOfRequests;

            calculateFee.balance = calculateFee.balance - debitedNormalAmount;

            calculateFee.replyTo.tell(new PaymentOrderFeeCalculated(
                    "{} {} is debited from Account due to Interest Rate for Normal package!", debitedNormalAmount,
                    calculateFee.currency));

        } else {

            getContext().getLog()
                    .info("ERROR! Payment Order could not be calculated! Please Enter relevant order package!\n\n");

        }

        getContext().getLog().info("Current Balance : {}\n", calculateFee.balance);

        getContext().getLog().info(
                "TRANSACTION LOG: *PAYMENT ORDER ID* - {} | *CLIENT ACCOUNT ID* - {} | *BANK ID* - {} | *RECEIVER ACCOUNT ID*  - {} |  | *DATE* - {} | *AMOUNT* - {} | *BALANCE* - {} {}\n",
                calculateFee.billingComplete.paymentOrderId, calculateFee.billingComplete.accountId,
                calculateFee.billingComplete.bankId, calculateFee.billingComplete.externalAccountId,
                calculateFee.billingComplete.date, calculateFee.amount, calculateFee.balance, calculateFee.currency);

        return this;

    }

    private Behavior<Command> onDebitWithdrawnAccount(final DebitWithdrawnAccount debitAccount) {

        getContext().getLog().info("Current Account Balance : {} {}. Debitting Withdrawn Account\n",
        debitAccount.balance, debitAccount.currency);

        if (debitAccount.userPackage == "Student") {

            if (this.numberOfFeeWithdrawals <= this.studentAtmLimit) {

                debitAccount.balance = debitAccount.balance - (numberOfRequests + debitAccount.amount);

                this.numberOfFeeWithdrawals += this.incrementRequestOrOccurences;

                debitAccount.replyTo.tell(new WithdrawalCompleted(debitAccount.withdrawalId, debitAccount.balance, debitAccount.userPackage,
                debitAccount.accountId, debitAccount.date, debitAccount.amount, "VERIFIED!"));

                getContext().getLog().info("Account with Id - `{}` is debited with withdrawal Id - `{}` for package - `{}` with amount - `{}` on {}",
                debitAccount.accountId, debitAccount.withdrawalId, debitAccount.userPackage, debitAccount.amount, debitAccount.date);

                getContext().getLog().info("{} {} is withdrawn. Final Account Balance : {}. STATUS : {}", 
                this.numberOfRequests + debitAccount.amount, debitAccount.currency, debitAccount.balance, "PROCESSED!");

            } else {

                this.numberOfRequests += incrementRequestOrOccurences;

                debitAccount.balance = debitAccount.balance - (this.numberOfRequests * studentAtmFee + debitAccount.amount);

                debitAccount.replyTo.tell(new WithdrawalCompleted(debitAccount.withdrawalId, debitAccount.balance, debitAccount.userPackage,
                debitAccount.accountId, debitAccount.date, debitAccount.amount, "VERIFIED!"));

                getContext().getLog().info("Account with Id - `{}` is debited with withdrawal Id - `{}` for package - `{}` with amount - `{}` on {}",
                debitAccount.accountId, debitAccount.withdrawalId, debitAccount.userPackage, debitAccount.amount, debitAccount.date);

                getContext().getLog().info("{} {} is withdrawn. Final Account Balance : {}. STATUS : {}", 
                this.numberOfRequests * studentAtmFee + debitAccount.amount,debitAccount.currency,debitAccount.balance, "PROCESSED!");            

            }

        } else if (debitAccount.userPackage == "Normal") {

            debitAccount.balance = debitAccount.balance - (normalAtmFee + debitAccount.amount);

            debitAccount.replyTo.tell(new WithdrawalCompleted(debitAccount.withdrawalId, debitAccount.balance, debitAccount.userPackage,
            debitAccount.accountId, debitAccount.date, debitAccount.amount, "VERIFIED!"));

            getContext().getLog().info("Account with Id - `{}` is debited with withdrawal Id - `{}` for package - `{}` with amount - `{}` on {}",
            debitAccount.accountId, debitAccount.withdrawalId, debitAccount.userPackage, debitAccount.amount, debitAccount.date);

            getContext().getLog().info("{} {} is withdrawn. Final Account Balance : {}. STATUS : {}", 
            this.normalAtmFee + debitAccount.amount,debitAccount.currency,debitAccount.balance, "PROCESSED!");            

        } else {

            getContext().getLog().info("ERROR! Please, enter valid package name!\n");

        }

        return this;

    }

    private Behavior<Command> onCreditDepositedAccount(final CreditDepositedAccount creditAccount) {

        getContext().getLog().info("Current Account Balance : {} {}. Crediting Deposited Account\n",
        creditAccount.balance, creditAccount.currency);

        if (creditAccount.userPackage == "Student") {

            if (this.numberOfFeeDeposits <= this.studentAtmLimit) {

                creditAccount.balance = creditAccount.balance + (creditAccount.amount - numberOfRequests * studentAtmFee);

                this.numberOfFeeDeposits += this.incrementRequestOrOccurences;

                creditAccount.replyTo.tell(new DepositCompleted(creditAccount.depositId, creditAccount.balance, creditAccount.userPackage,
                creditAccount.accountId, creditAccount.date, creditAccount.amount, "VERIFIED!"));

                getContext().getLog().info("Account with Id - `{}` is credited with deposit Id - `{}` for package - `{}` with amount - `{}` on {}",
                creditAccount.accountId, creditAccount.deposit, creditAccount.userPackage, creditAccount.amount, creditAccount.date);

                getContext().getLog().info("{} {} is credited. Final Account Balance : {}. STATUS : {}", 
                (creditAccount.amount - numberOfRequests * studentAtmFee), creditAccount.currency, 
                creditAccount.balance, "PROCESSED!");

            } else {

                this.numberOfRequests += incrementRequestOrOccurences;

                creditAccount.balance = creditAccount.balance + (creditAccount.amount - numberOfRequests * studentAtmFee);

                creditAccount.replyTo.tell(new DepositCompleted(creditAccount.depositId, creditAccount.balance, creditAccount.userPackage,
                creditAccount.accountId, creditAccount.date, creditAccount.amount, "VERIFIED!"));

                getContext().getLog().info("Account with Id - `{}` is credited with deposit Id - `{}` for package - `{}` with amount - `{}` on {}",
                creditAccount.accountId, creditAccount.deposit, creditAccount.userPackage, creditAccount.amount, creditAccount.date);

                getContext().getLog().info("{} {} is credited. Final Account Balance : {}. STATUS : {}", 
                (creditAccount.amount - numberOfRequests * studentAtmFee), creditAccount.currency, 
                creditAccount.balance, "PROCESSED!");         

            }

        } else if (creditAccount.userPackage == "Normal") {

            creditAccount.balance = creditAccount.balance + (creditAccount.amount - normalAtmFee);

            creditAccount.replyTo.tell(new DepositCompleted(creditAccount.depositId, creditAccount.balance, creditAccount.userPackage,
            creditAccount.accountId, creditAccount.date, creditAccount.amount, "VERIFIED!"));

            getContext().getLog().info("Account with Id - `{}` is credited with deposit Id - `{}` for package - `{}` with amount - `{}` on {}",
            creditAccount.accountId, creditAccount.deposit, creditAccount.userPackage, creditAccount.amount, creditAccount.date);

            getContext().getLog().info("{} {} is credited. Final Account Balance : {}. STATUS : {}", 
            (creditAccount.amount - numberOfRequests * studentAtmFee), creditAccount.currency, 
            creditAccount.balance, "PROCESSED!");           

        } else {

            getContext().getLog().info("ERROR! Please, enter valid package name!\n");

        }        

        return this;

    }

    private Behavior<Command> onCalculateEndOfMonthBill(final CalculateEndOfMonthBill calculateMonthly) {

        // to be done
        return this;

    }

    private Behavior<Command> onPostStop() {

        getContext().getLog().info("Billing actor is stopped\n");
        
        return Behaviors.stopped();

    }

}