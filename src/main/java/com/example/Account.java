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
        final ActorRef<CheckAccountBalance> amount;
        final ActorRef<CheckAccountBalance> checkPaymentOrderId;

        public SubmitPaymentOrder(final long paymentOrderId, final long date, final boolean instructAccount,
                    final long amount, final long currency, final ActorRef<CheckAccountBalance> amount, 
                    final ActorRef<CheckAccountBalance> checkPaymentOrderId) {

                        this.paymentOrderId = paymentOrderId;
                        this.date = date;
                        this.instructAccount = instructAccount;
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
    }

    public static final class PaymentOrderRejected {
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