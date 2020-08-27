package com.example;

import java.util.Optional;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Account extends AbstractBehavior<Account.Command> {

    public interface Command {}

    public static final class SubmitPaymentOrder implements Command {

        final long paymentOrderId;
        final long date;
        final boolean instructAccount;
        final long currency;
        final long accountId;
        final long bankId;
        final ActorRef<CheckAccountBalance> amount;
        final ActorRef<CheckAccountBalance> checkPaymentOrderId;

        public SubmitPaymentOrder(final long paymentOrderId, final long date, final boolean instructAccount,
                    final long accountId, final long bankId,
                    final long currency, final ActorRef<CheckAccountBalance> amount, 
                    final ActorRef<CheckAccountBalance> checkPaymentOrderId) {

                        this.paymentOrderId = paymentOrderId;
                        this.date = date;
                        this.instructAccount = instructAccount;
                        this.accountId = accountId;
                        this.bankId = bankId;
                        this.currency = currency;
                        this.amount = amount;
                        this.checkPaymentOrderId = checkPaymentOrderId;

                    }
    }

    public static final class CheckAccountBalance implements Command {

        final long paymentOrderId;
        final long balance;
        final Optional<Double> value;
        final ActorRef<PaymentOrderVerified> verify;
        final ActorRef<PaymentOrderRejected> reject;

        public CheckAccountBalance(final long paymentOrderId, final long balance, 
                    final Optional<Double> value, final ActorRef<PaymentOrderVerified> verify,
                    final ActorRef<PaymentOrderRejected> reject) {
                        
                        this.paymentOrderId = paymentOrderId;
                        this.balance = balance;
                        this.value = value;
                        this.verify = verify;
                        this.reject = reject;

                    }
    }

    public static final class PaymentOrderVerified {
        
        final long paymentOrderId;
        final boolean paymentOrderCheck;
        final long paymentOrderRequests;
        final long numberOfATMFeeWithdrawals;
        final long numberOfATMFeeDeposits;
        final Optional<Double> amount;
        final Optional<Double> balance;
        
        public PaymentOrderVerified(final long paymentOrderId, final boolean paymentOrderCheck,
                    final long paymentOrderRequests, final long numberOfATMFeeWithdrawals,
                    final long numberOfATMFeeDeposits,
                    final Optional<Double> amount, final Optional<Double> balance) {

                        this.paymentOrderId = paymentOrderId;
                        this.paymentOrderCheck = paymentOrderCheck;
                        this.paymentOrderRequests = paymentOrderRequests;
                        this.numberOfATMFeeWithdrawals = numberOfATMFeeWithdrawals;
                        this.numberOfATMFeeDeposits = numberOfATMFeeDeposits;
                        this.amount = amount;
                        this.balance = balance;

                    }
    }

    public static final class PaymentOrderRejected {

        final long paymentOrderId;
        final boolean paymentOrderCheck;
        final Optional<Double> amount;
        final Optional<Double> balance;

        public PaymentOrderRejected(final long paymentOrderId, final boolean paymentOrderCheck,
                    final Optional<Double> amount, final Optional<Double> balance) {

                        this.paymentOrderId = paymentOrderId;
                        this.paymentOrderCheck = paymentOrderCheck;
                        this.amount = amount;
                        this.balance = balance;

                    }        
    }

    public static final class DebitCurrentAccount implements Command {

        final long paymentOrderId;
        final long balance;
        final String userPackage;
        final ActorRef<AccountDebited> recordTransactionAmount;

        public DebitCurrentAccount(final long paymentOrderId, final long balance,
                    final String userPackage, 
                    final ActorRef<AccountDebited> recordTransactionAmount) {

                        this.paymentOrderId = paymentOrderId;
                        this.balance = balance;
                        this.userPackage = userPackage;
                        this.recordTransactionAmount = recordTransactionAmount;

                    }
    }

    public static final class AccountDebited {

        final long paymentOrderId;
        final long balance;
        final Optional<Double> amount;

        public AccountDebited(final long paymentOrderId, final long balance, 
                    final Optional<Double> amount) {

                        this.paymentOrderId = paymentOrderId;
                        this.balance = balance;
                        this.amount = amount;

                    }
    }

    public static final class InstructOppositeAccount implements Command {

        final long paymentOrderId;
        final boolean instructAccount;
        final long currency;
        final long balance;
        final ActorRef<Payment.IdentifyRouteToOppositeAccount> amount;
        final ActorRef<Payment.IdentifyRouteToOppositeAccount> receiveBankId;
        final ActorRef<Payment.IdentifyRouteToOppositeAccount> receiveAccountId;

        public InstructOppositeAccount(final long paymentOrderId, final boolean instructAccount, 
                    final long currency, final long balance, 
                    final ActorRef<Payment.IdentifyRouteToOppositeAccount> amount, 
                    final ActorRef<Payment.IdentifyRouteToOppositeAccount> receiveBankId,
                    final ActorRef<Payment.IdentifyRouteToOppositeAccount> receiveAccountId) {

                        this.paymentOrderId = paymentOrderId;
                        this.instructAccount = instructAccount;
                        this.currency = currency;
                        this.balance = balance;
                        this.amount = amount;
                        this.receiveBankId = receiveBankId;
                        this.receiveAccountId = receiveAccountId;

                    }
    }

    public static final class CompletePaymentOrder implements Command {



    }

    public static final class PaymentOrderProcessed {


        
    }


    private final long accountPaymentOrderId;

    private final long accountAmount;

    private Account(final ActorContext<Command> context, 
                    final long accountPaymentOrderId, final long accountAmount) {

        super(context);
        this.accountPaymentOrderId = accountPaymentOrderId;
        this.accountAmount = accountAmount;

        context.getLog().info("Account actor entered with id {} and amount {} ", 
                                accountPaymentOrderId, accountAmount);
    }
}