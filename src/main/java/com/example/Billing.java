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

    public static final class CheckBillingBalance implements Account.Command {

        final Double balance;
        protected ActorRef<WithdrawVerified> verify;
        protected ActorRef<WithdrawRejected> reject;
        protected ActorRef<DepositVerified> deposit;

        public CheckBillingBalance(final Double balance) {

                this.balance = balance;

        }

    }

    public static final class WithdrawVerified {
        
        final long withdrawProcessId;
        final long numberOfATMFeeWithdrawals;
        final Double amount;

        public WithdrawVerified(final long withdrawProcessId, 
            final long numberOfATMFeeWithdrawals, final Double amount) {

                this.withdrawProcessId = withdrawProcessId;
                this.numberOfATMFeeWithdrawals = numberOfATMFeeWithdrawals;
                this.amount = amount;

            }

    }

    public static final class WithdrawRejected {

        final long withdrawProcessId;
        protected CheckBillingBalance checkBalance;

        public WithdrawRejected(final long withdrawProcessId) {

                this.withdrawProcessId = withdrawProcessId;

            }

    }

    public static final class DepositVerified {

        final long depositProcessId;
        final long numberOfDeposits;
        final Double amount;
        protected CheckBillingBalance checkBalance;

        public DepositVerified(final long depositProcessId, final long numberOfDeposits, 
            final Double amount) {

                this.depositProcessId = depositProcessId;
                this.numberOfDeposits = numberOfDeposits;
                this.amount = amount;

            }

    }

    public static final class DebitWithdrawnAccount implements Account.Command {

        final long withdrawProcessId;
        final Double balance;
        protected WithdrawVerified withdraw;
        final String userPackage;
        protected ActorRef<WithdrawalCompleted> replyTo;

        public DebitWithdrawnAccount(final long withdrawProcessId, final Double balance,
            final String userPackage) {

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

        public CreditDepositedAccount(final long depositProcessId, final Double balance, 
            final String userPackage) {

                this.depositProcessId = depositProcessId;
                this.balance = balance;
                this.userPackage = userPackage;

            }

    }

    public static final class WithdrawalCompleted {

        final long withdrawProcessId;
        protected DebitWithdrawnAccount debit;

        public WithdrawalCompleted(final long withdrawProcessId) {

            this.withdrawProcessId = withdrawProcessId;

        }

    }

    public static final class DepositCompleted {

        final long depositProcessId;
        protected CreditDepositedAccount credit;

        public DepositCompleted(final long depositProcessId) {

            this.depositProcessId = depositProcessId;

        }
    }


}