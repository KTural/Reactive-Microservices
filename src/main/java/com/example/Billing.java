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
        final Double balance;
        protected CompletePaymentOrder billingComplete;
        protected ActorRef<PaymentOrderFeeCalculated> replyTo;
  
        public CalculatePaymentOrderFee(final String accountId, final Double amount, 
                    final Double balance) {

                        this.accountId = billingComplete.accountId;
                        this.amount = billingComplete.amount;
                        this.balance = billingComplete.balance;

        }
    }

    public static final class PaymentOrderFeeCalculated {

        final String feeType;
        protected CalculatePaymentOrderFee calculateFee;

        public PaymentOrderFeeCalculated(final String feeType) {

                        this.feeType = feeType;

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


    private Billing(final ActorContext<Command> context) {

        super(context);

        context.getLog().info("Billing actor is created with");

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

        return this;

    }

    private Behavior<Command> onDebitWithdrawnAccount(final DebitWithdrawnAccount debitAccount) {

        return this;

    }

    private Behavior<Command> onCreditDepositedAccount(final CreditDepositedAccount creditAccount) {

        return this;

    }

    private Behavior<Command> onCalculateEndOfMonthBill(final CalculateEndOfMonthBill calculateMonthly) {

        return this;

    }

    private Behavior<Command> onPostStop() {

        getContext().getLog().info("Billing actor is stopped \n");
        
        return Behaviors.stopped();

    }

}