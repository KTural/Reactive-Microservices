package com.example;

import java.util.Optional;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Billing extends AbstractBehavior<Account.Command> {

    public static final class CalculatePaymentOrderFee implements Account.Command {

        final long accountId;
        final Double amount;
        final ActorRef<PaymentOrderFeeCalculated> replyTo;
  
        public CalculatePaymentOrderFee(final long accountId, final Double amount,
            final ActorRef<PaymentOrderFeeCalculated> replyTo) {

                this.accountId = accountId;
                this.amount = amount;
                this.replyTo = replyTo;

        }
    }

    public static final class PaymentOrderFeeCalculated {

        final String feeType;
        final CalculatePaymentOrderFee calculateFee;

        public PaymentOrderFeeCalculated(final String feeType, 
            final CalculatePaymentOrderFee calculateFee) {

                this.feeType = feeType;
                this.calculateFee = calculateFee;

        }
    }

    static enum Passivate implements Account.Command {
        INSTANCE;
    }

    public static Behavior<Account.Command> create(String accountId, Double accountBalance, Double amount) {

        return Behaviors.setup(context -> new Billing(context, accountId, accountBalance, amount));

    }

    private final String accountId;
    private final Double accountBalance;
    
    protected long numberOfFeeWithdrawals;
    protected long numberOfFeeDeposits;




}