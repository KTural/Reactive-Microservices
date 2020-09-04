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
        final Double amount;
        final String accountId;
        final long bankId;
        final ActorRef<PaymentOrderStatus> checkPaymentOrderId;

        public SubmitPaymentOrder(final long paymentOrderId, final long date,
                    final Double amount, final String accountId,
                    final long bankId,
                    final ActorRef<PaymentOrderStatus> checkPaymentOrderId) {

                        this.paymentOrderId = paymentOrderId;
                        this.date = date;
                        this.amount = amount;
                        this.accountId = accountId;
                        this.bankId = bankId;
                        this.checkPaymentOrderId = checkPaymentOrderId;

                    }
    }

    public static final class PaymentOrderStatus {
        
        final long paymentOrderId;
        final String status;

        public PaymentOrderStatus(final long paymentOrderId, final String status) {

                        this.paymentOrderId = paymentOrderId;
                        this.status = status;

        }
    }

    public static final class CheckAccountBalance implements Command {

        final SubmitPaymentOrder paymentOrder;
        protected ActorRef<PaymentOrderVerified> verify;
        protected ActorRef<PaymentOrderRejected> reject;

        public CheckAccountBalance(final SubmitPaymentOrder paymentOrder) {

                        this.paymentOrder = paymentOrder;

        }
    }

    public static final class PaymentOrderVerified {
        
        final long paymentOrderId;
        final String transferStatus;
        final long numberOfPaymentOrderRequests;
        final Double amount;
        final Double accountBalance;
        final String orderStatus;
        
        public PaymentOrderVerified(final long paymentOrderId,
                    final String transferStatus,
                    final long numberOfPaymentOrderRequests, 
                    final Double amount,
                    final Double accountBalance,
                    final String orderStatus) {

                        this.paymentOrderId = paymentOrderId;
                        this.transferStatus = transferStatus;
                        this.numberOfPaymentOrderRequests = numberOfPaymentOrderRequests;
                        this.amount = amount;
                        this.accountBalance = accountBalance;
                        this.orderStatus = orderStatus;

                    }
    }

    public static final class PaymentOrderRejected {

        final long paymentOrderId;
        final Double amount;
        final Double balance;
        final String orderStatus;

        public PaymentOrderRejected(final long paymentOrderId,
                    final Double amount, final Double balance,
                    final String orderStatus) {

                        this.paymentOrderId = paymentOrderId;
                        this.amount = amount;
                        this.balance = balance;
                        this.orderStatus = orderStatus;

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
        final boolean accountInstruction;
        final long currency;
        final long balance;
        final ActorRef<Payment.IdentifyRouteToOppositeAccount> amount;
        final ActorRef<Payment.IdentifyRouteToOppositeAccount> receiveBankId;
        final ActorRef<Payment.IdentifyRouteToOppositeAccount> receiveAccountId;

        public InstructOppositeAccount(final long paymentOrderId, final boolean accountInstruction, 
                    final long currency, final long balance, 
                    final ActorRef<Payment.IdentifyRouteToOppositeAccount> amount, 
                    final ActorRef<Payment.IdentifyRouteToOppositeAccount> receiveBankId,
                    final ActorRef<Payment.IdentifyRouteToOppositeAccount> receiveAccountId) {

                        this.paymentOrderId = paymentOrderId;
                        this.accountInstruction = accountInstruction;
                        this.currency = currency;
                        this.balance = balance;
                        this.amount = amount;
                        this.receiveBankId = receiveBankId;
                        this.receiveAccountId = receiveAccountId;

                    }
    }

    public static final class CompletePaymentOrder implements Command {

        final long paymentOrderId;
        final String accountId;
        final long amount;
        final ActorRef<PaymentOrderProcessed> confirmation;

        public CompletePaymentOrder(final long paymentOrderId, final String accountId, 
                    final long amount, final ActorRef<PaymentOrderProcessed> confirmation) {

                        this.paymentOrderId = paymentOrderId;
                        this.accountId = accountId;
                        this.amount = amount;
                        this.confirmation = confirmation;

                    }
    }

    public static final class PaymentOrderProcessed {

        final long paymentOrderId;
        final boolean paymentProcessed;
        final ActorRef<Billing.CalculatePaymentOrderFee> amount;
        final ActorRef<Billing.CalculatePaymentOrderFee> accountId;
        final ActorRef<Billing.CalculatePaymentOrderFee> numberOfPaymentOrderRequests;

        public PaymentOrderProcessed(final long paymentOrderId, final boolean paymentProcessed,
                    final ActorRef<Billing.CalculatePaymentOrderFee> amount, 
                    final ActorRef<Billing.CalculatePaymentOrderFee> accountId, 
                    final ActorRef<Billing.CalculatePaymentOrderFee> numberOfPaymentOrderRequests) {

                        this.paymentOrderId = paymentOrderId;
                        this.paymentProcessed = paymentProcessed;
                        this.amount = amount;
                        this.accountId = accountId;
                        this.numberOfPaymentOrderRequests = numberOfPaymentOrderRequests;

                    }
    }

    static enum Passivate implements Command {
        INSTANCE
    }

    public static Behavior<Command> create(String accountId, Double accountBalance, Double amount) {

        return Behaviors.setup(context -> new Account(context, accountId, accountBalance, amount));

    }

    private final String accountId;
    private final Double accountBalance;
    private final String currency;

    private final String transferStatus;
    protected long numberOfPaymentOrderRequests;
    protected long numberOfATMFeeWithdrawals;
    protected long numberOfATMFeeDeposits;

    private Account(final ActorContext<Command> context, final String accountId, final Double accountBalance,
            final Double amount) {

        super(context);
        this.accountId = accountId;
        this.accountBalance = accountBalance;
        this.currency = "CZK";

        transferStatus = "Withdraw";
        numberOfPaymentOrderRequests = 0;
        numberOfATMFeeWithdrawals = 0;
        numberOfATMFeeDeposits = 0;

        context.getLog().info("Account actor is created with :: id {}, balance {}, currency {} ", accountId, accountBalance, currency);

    }

    @Override
    public Receive<Command> createReceive() {

        return newReceiveBuilder()
            .onMessage(SubmitPaymentOrder.class, this::onSubmitPaymentOrder)
            .onMessage(CheckAccountBalance.class, this::onCheckAccountBalance)
            .onMessage(DebitCurrentAccount.class, this::onDebitCurrentAccount)
            .onMessage(InstructOppositeAccount.class, this::onInstructOppositeAccount)
            .onMessage(CompletePaymentOrder.class, this::onCompletePaymentOrder)
            .onMessage(Passivate.class, m -> Behaviors.stopped())
            .onSignal(PostStop.class, signal -> onPostStop())
            .build();

    }

    private Behavior<Command> onSubmitPaymentOrder(final SubmitPaymentOrder paymentOrder) {
        getContext().getLog().info("Submit Payment Order id={} command received with Account id={} and Bank id={} on {} ", 
            paymentOrder.paymentOrderId, paymentOrder.accountId, paymentOrder.bankId, paymentOrder.date);

        paymentOrder.checkPaymentOrderId.tell(new PaymentOrderStatus(paymentOrder.paymentOrderId, "SUBMITTED"));

        this.getContext().getSelf().tell(new CheckAccountBalance(paymentOrder));
        
        return this;
    }

    private Behavior<Command> onCheckAccountBalance(final CheckAccountBalance checkBalance) {

        getContext().getLog().info("Checking balance for Payment Order Id = {} ", checkBalance.paymentOrder.paymentOrderId);
        if (checkBalance.paymentOrder.amount <= this.accountBalance) {

            if (transferStatus == "Withdraw") {

                numberOfATMFeeWithdrawals += 1;                

            } else if (transferStatus == "Deposit") {

                numberOfATMFeeDeposits += 1;

            }

            numberOfPaymentOrderRequests += 1;


            checkBalance.verify.tell(new PaymentOrderVerified(checkBalance.paymentOrder.paymentOrderId, 
            transferStatus, numberOfPaymentOrderRequests, checkBalance.paymentOrder.amount, this.accountBalance, "VERIFIED"));

            getContext().getLog().info("Balance for Payment Order Id = {}, status = {} ", checkBalance.paymentOrder.paymentOrderId,
            "VERIFIED");

        } else {

            checkBalance.reject.tell(new PaymentOrderRejected(checkBalance.paymentOrder.paymentOrderId, 
            checkBalance.paymentOrder.amount, this.accountBalance, "REJECTED"));

            getContext().getLog().info("Balance for Payment Order Id = {}, status = {} ", checkBalance.paymentOrder.paymentOrderId,
            "REJECTED");

        }


        return this;

    }
    
    private Behavior<Command> onDebitCurrentAccount(final DebitCurrentAccount debitAccount) {

        return this;

    }
    
    private Behavior<Command> onInstructOppositeAccount(final InstructOppositeAccount instructAccount) {

        return this;

    }
    
    private Behavior<Command> onCompletePaymentOrder(final CompletePaymentOrder completeOrder) {

        return this;

    }
    
    private Behavior<Command> onPostStop() {
        getContext().getLog().info("Account actor is stopped with :: id {}, balance {}, currency {} ", accountId, accountBalance, currency);
        return Behaviors.stopped();
    }    
    
}