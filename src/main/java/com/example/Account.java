package com.example;

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
        final String date;
        final Double amount;
        final String accountId;
        final long bankId;
        final ActorRef<PaymentOrderStatus> checkPaymentOrderId;

        public SubmitPaymentOrder(final long paymentOrderId, final String date,
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
        protected CheckAccountBalance check;
        protected Double balance;
        final String userPackage;
        protected ActorRef<AccountDebited> recordTransactionAmount;

        public DebitCurrentAccount(final long paymentOrderId, Double balance, final String userPackage) {

                        this.paymentOrderId = paymentOrderId;
                        this.balance = balance;
                        this.userPackage = userPackage;

        }
    }

    public static final class AccountDebited {

        final long paymentOrderId;
        final Double balance;
        final Long amount;
        final String message;

        public AccountDebited(final long paymentOrderId, final Double balance, final Long amount, final String message) {

                        this.paymentOrderId = paymentOrderId;
                        this.balance = balance;
                        this.amount = amount;
                        this.message = message;

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

        public InstructOppositeAccount(final long paymentOrderId, final boolean accountInstruction, final long currency,
                    final long balance, final ActorRef<Payment.IdentifyRouteToOppositeAccount> amount,
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

        public CompletePaymentOrder(final long paymentOrderId, final String accountId, final long amount,
                    final ActorRef<PaymentOrderProcessed> confirmation) {

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

    public static final class SubmitWithdrawalOrDepositOrder implements Command {

        final Double balance;
        final Double amount;
        protected ActorRef<WithdrawalVerified> verify;
        protected ActorRef<WithdrawalRejected> reject;
        protected ActorRef<DepositVerified> deposit;

        public SubmitWithdrawalOrDepositOrder(final Double balance, final Double amount) {

                        this.balance = balance;
                        this.amount = amount;

        }
    }

    public static final class WithdrawalVerified {

        final long withdrawProcessId;
        final long numberOfATMFeeWithdrawals;
        protected SubmitWithdrawalOrDepositOrder checkBalance;

        public WithdrawalVerified(final long withdrawProcessId, final long numberOfATMFeeWithdrawals) {

                        this.withdrawProcessId = withdrawProcessId;
                        this.numberOfATMFeeWithdrawals = numberOfATMFeeWithdrawals;

        }
    }

    public static final class WithdrawalRejected {

        final long withdrawProcessId;
        protected SubmitWithdrawalOrDepositOrder checkBalance;

        public WithdrawalRejected(final long withdrawProcessId) {

                        this.withdrawProcessId = withdrawProcessId;

        }
    }

    public static final class DepositVerified {

        final long depositProcessId;
        final long numberOfDeposits;
        final Double amount;
        protected SubmitWithdrawalOrDepositOrder checkBalance;

        public DepositVerified(final long depositProcessId, final long numberOfDeposits, final Double amount) {

                        this.depositProcessId = depositProcessId;
                        this.numberOfDeposits = numberOfDeposits;
                        this.amount = amount;

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

    public static final class WithdrawalCompleted {

        final long withdrawProcessId;
        protected DebitWithdrawnAccount debit;

        public WithdrawalCompleted(final long withdrawProcessId) {

                        this.withdrawProcessId = withdrawProcessId;

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

    public static final class DepositCompleted {

        final long depositProcessId;
        protected CreditDepositedAccount credit;

        public DepositCompleted(final long depositProcessId) {

                        this.depositProcessId = depositProcessId;

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

    protected String userPackage;

    private final String withdrawalOrDepositStatus;
    protected long numberOfPaymentOrderRequests;

    protected long studentPackagePaymentOrderLimit;
    protected long studentRequestsOrOccurences;
    protected long studentPackagePaymentFee;

    protected long incrementRequestOrOccurence;

    protected long normalPackagePaymentOrderLimit;
    protected long normalRequestsOrOccurences;
    protected long normalPackagePaymentFee;

    private Account(final ActorContext<Command> context, final String accountId, final Double accountBalance,
            final Double amount) {

        super(context);
        this.accountId = accountId;
        this.accountBalance = accountBalance;
        this.currency = "CZK";

        userPackage = "Student";

        withdrawalOrDepositStatus = "Withdraw";
        numberOfPaymentOrderRequests = 0;

        studentPackagePaymentOrderLimit = 3;
        studentRequestsOrOccurences = 0;
        studentPackagePaymentFee = 5;

        incrementRequestOrOccurence = 1;

        normalPackagePaymentOrderLimit = 5;
        normalRequestsOrOccurences = 0;
        normalPackagePaymentFee = 10;

        context.getLog().info("Account actor is created with :: id - %s, balance - %.2f, currency %s ", accountId,
                accountBalance, currency);

    }

    @Override
    public Receive<Command> createReceive() {

        return newReceiveBuilder().onMessage(SubmitPaymentOrder.class, this::onSubmitPaymentOrder)
                .onMessage(CheckAccountBalance.class, this::onCheckAccountBalance)
                .onMessage(DebitCurrentAccount.class, this::onDebitCurrentAccount)
                .onMessage(InstructOppositeAccount.class, this::onInstructOppositeAccount)
                .onMessage(CompletePaymentOrder.class, this::onCompletePaymentOrder)
                .onMessage(SubmitWithdrawalOrDepositOrder.class, this::onSubmitWithdrawalOrDepositOrder)
                .onMessage(DebitWithdrawnAccount.class, this::onDebitWithdrawnAccount)
                .onMessage(CreditDepositedAccount.class, this::onCreditDepositedAccount)
                .onMessage(Passivate.class, m -> Behaviors.stopped()).onSignal(PostStop.class, signal -> onPostStop())
                .build();

    }

    private Behavior<Command> onSubmitPaymentOrder(final SubmitPaymentOrder paymentOrder) {
        getContext().getLog().info(
                "Submit Payment Order id = %d command received with Account id = %s and Bank id = %d on %s ",
                paymentOrder.paymentOrderId, paymentOrder.accountId, paymentOrder.bankId, paymentOrder.date);

        paymentOrder.checkPaymentOrderId.tell(new PaymentOrderStatus(paymentOrder.paymentOrderId, "SUBMITTED"));

        this.getContext().getSelf().tell(new CheckAccountBalance(paymentOrder));

        return this;
    }

    private Behavior<Command> onCheckAccountBalance(final CheckAccountBalance checkBalance) {

        getContext().getLog().info("Checking balance for Payment Order Id = %d ",
                checkBalance.paymentOrder.paymentOrderId);

        if (checkBalance.paymentOrder.amount <= this.accountBalance) {

            numberOfPaymentOrderRequests += incrementRequestOrOccurence;

            checkBalance.verify.tell(new PaymentOrderVerified(checkBalance.paymentOrder.paymentOrderId,
                    withdrawalOrDepositStatus, numberOfPaymentOrderRequests, checkBalance.paymentOrder.amount,
                    this.accountBalance, "VERIFIED"));

            getContext().getLog().info("Balance for Payment Order Id = %d, status = %s ",
                    checkBalance.paymentOrder.paymentOrderId, "VERIFIED");

            this.getContext().getSelf().tell(new DebitCurrentAccount(checkBalance.paymentOrder.paymentOrderId, 
            this.accountBalance, this.userPackage));        
                    
        } else {

            checkBalance.reject.tell(new PaymentOrderRejected(checkBalance.paymentOrder.paymentOrderId,
                    checkBalance.paymentOrder.amount, this.accountBalance, "REJECTED"));

            getContext().getLog().info("Balance for Payment Order Id = %d, status = %s ",
                    checkBalance.paymentOrder.paymentOrderId, "REJECTED");

        }

        return this;

    }

    private Behavior<Command> onDebitCurrentAccount(final DebitCurrentAccount debitAccount) {

        getContext().getLog().info("Current Account Balance : %.2f %s. Offered packages below : `STUDENT` and `NORMAL` for Payment Order Id = %d ",
        debitAccount.balance, this.currency, debitAccount.check.paymentOrder.paymentOrderId);

        if (debitAccount.userPackage == "Student") {

            if (numberOfPaymentOrderRequests <= studentPackagePaymentOrderLimit) {

                debitAccount.balance = (debitAccount.balance
                        - (studentRequestsOrOccurences + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.studentRequestsOrOccurences, String.format("%.2f %s debited from Account", 
                this.studentRequestsOrOccurences + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with %s package and Payment Order Id = %d : status = %s", 
                debitAccount.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");

            } else {

                studentRequestsOrOccurences = studentRequestsOrOccurences + incrementRequestOrOccurence;

                debitAccount.balance = (debitAccount.balance 
                        - (studentRequestsOrOccurences * studentPackagePaymentFee + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.studentRequestsOrOccurences, String.format("%.2f %s debited from Account",
                this.studentRequestsOrOccurences * studentPackagePaymentFee + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with %s package and Payment Order Id = %d : status = {}", 
                debitAccount.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");                

            }

        } else if (debitAccount.userPackage == "Normal") {

            if (numberOfPaymentOrderRequests <= normalPackagePaymentOrderLimit) {

                debitAccount.balance = (debitAccount.balance
                        - (normalRequestsOrOccurences + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.normalRequestsOrOccurences, String.format("%.2f %s debited from Account", 
                this.normalRequestsOrOccurences + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with %s package and Payment Order Id = %d : status = %s", 
                debitAccount.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");

            } else {

                normalRequestsOrOccurences = normalRequestsOrOccurences + incrementRequestOrOccurence;

                debitAccount.balance = (debitAccount.balance 
                        - (normalRequestsOrOccurences * normalPackagePaymentFee + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.normalRequestsOrOccurences, String.format("%.2f %s debited from Account",
                this.normalRequestsOrOccurences * normalPackagePaymentFee + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with %s package and Payment Order Id = %d : status = %s", 
                debitAccount.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");                

            }                

        }

        getContext().getLog().info("Account is Debited. Current Balance : %.2f %s ", debitAccount.balance, this.currency);

        return this;

    }
    
    private Behavior<Command> onInstructOppositeAccount(final InstructOppositeAccount instructAccount) {

        return this;

    }
    
    private Behavior<Command> onCompletePaymentOrder(final CompletePaymentOrder completeOrder) {

        return this;

    }

    private Behavior<Command> onSubmitWithdrawalOrDepositOrder(
                            final SubmitWithdrawalOrDepositOrder order) {

        return this;                        

    }

    private Behavior<Command> onDebitWithdrawnAccount(final DebitWithdrawnAccount debitAccount) {

        return this;

    }

    private Behavior<Command> onCreditDepositedAccount(final CreditDepositedAccount creditAccount) {

        return this;

    }
    
    private Behavior<Command> onPostStop() {
        getContext().getLog().info("Account actor is stopped with :: id - %s, balance - %.2f, currency - %s",
        accountId, accountBalance, currency);
        return Behaviors.stopped();
    }    
    
}