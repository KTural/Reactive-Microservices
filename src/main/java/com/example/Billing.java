package com.example;

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

        final long withdrawProcessId;
        final Double balance;
        protected WithdrawalVerified withdraw;
        final String userPackage;
        protected ActorRef<WithdrawalCompleted> replyTo;

        public DebitWithdrawnAccount(final long withdrawProcessId, final Double balance, final String userPackage) {

                        this.withdrawProcessId = withdrawProcessId;
                        this.balance = balance;
                        this.userPackage = userPackage;

        }

    }

    public static final class CreditDepositedAccount implements Account.Command {

        final long depositProcessId;
        final Double balance;
        protected DepositVerified deposit;
        final String userPackage;
        protected ActorRef<DepositCompleted> replyTo;

        public CreditDepositedAccount(final long depositProcessId, final Double balance, final String userPackage) {

                        this.depositProcessId = depositProcessId;
                        this.balance = balance;
                        this.userPackage = userPackage;

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


    private Billing(final ActorContext<Command> context) {

        super(context);
        
        this.numberOfFeeWithdrawals = 1;
        this.numberOfFeeDeposits = 1;
        this.numberOfRequests = 1;

        this.studentInterestRate = 1.5;
        this.normalInterestRate = 0.5;

        this.percentage = 100.00;

        context.getLog().info("\nBilling actor is created with\n");

    }

    @Override
    public Receive<Command> createReceive() {

        return newReceiveBuilder().onMessage(CalculatePaymentOrderFee.class, this::onCalculatePaymentOrderFee)
            .onMessage(DebitWithdrawnAccount.class, this::onDebitWithdrawnAccount)
            .onMessage(CreditDepositedAccount.class, this::onCreditDepositedAccount)
            .onMessage(CalculateEndOfMonthBill.class, this::onCalculateEndOfMonthBill)
            .onMessage(Passivate.class, m -> Behaviors.stopped())
            .onSignal(PostStop.class, signal -> onPostStop())
            .build();

    }

    private Behavior<Command> onCalculatePaymentOrderFee(final CalculatePaymentOrderFee calculateFee) {

        getContext().getLog().info("Payment Order is now calculated. Interest Rate will be calculated for each P.O requests!\n");

        if (calculateFee.billingComplete.creditExternal.creditAccount.identify.instruct.userPackage == "Student") {

            double debitedStudentAmount = ((calculateFee.amount * this.studentInterestRate) / this.percentage) * this.numberOfRequests;

            calculateFee.balance = calculateFee.balance - debitedStudentAmount;

            calculateFee.replyTo.tell(new PaymentOrderFeeCalculated("%.2f %s is debited from Account due to Interest Rate for Student package!", 
            debitedStudentAmount, calculateFee.currency));

        } else if (calculateFee.billingComplete.creditExternal.creditAccount.identify.instruct.userPackage == "Normal") {

            double debitedNormalAmount = ((calculateFee.amount * this.normalInterestRate) / this.percentage) * this.numberOfRequests;

            calculateFee.balance = calculateFee.balance - debitedNormalAmount;

            calculateFee.replyTo.tell(new PaymentOrderFeeCalculated("%.2f %s is debited from Account due to Interest Rate for Normal package!", 
            debitedNormalAmount, calculateFee.currency));            

        } else {

            getContext().getLog().info("\n\nERROR! Payment Order could not be calculated! Please Enter relevant order package!\n\n");

        }

        getContext().getLog().info("Current Balance : %.2f", calculateFee.balance);

        getContext().getLog().info("\nTRANSACTION LOG: *PAYMENT ORDER ID* - %o | *CLIENT ACCOUNT ID* - %s | *BANK ID* - %s | *RECEIVER ACCOUNT ID*  - %s |  | *DATE* - %s | *AMOUNT* - %f | *BALANCE* - %.2f %s\n",
        calculateFee.billingComplete.paymentOrderId, calculateFee.billingComplete.accountId, calculateFee.billingComplete.bankId,
        calculateFee.billingComplete.externalAccountId, calculateFee.billingComplete.date, calculateFee.amount, 
        calculateFee.balance, calculateFee.currency);

        return this;

    }

    private Behavior<Command> onDebitWithdrawnAccount(final DebitWithdrawnAccount debitAccount) {



        return this;

    }

    private Behavior<Command> onCreditDepositedAccount(final CreditDepositedAccount creditAccount) {

        

        return this;

    }

    private Behavior<Command> onCalculateEndOfMonthBill(final CalculateEndOfMonthBill calculateMonthly) {

        // to be done
        return this;

    }

    private Behavior<Command> onPostStop() {

        getContext().getLog().info("Billing actor is stopped \n");
        
        return Behaviors.stopped();

    }

}