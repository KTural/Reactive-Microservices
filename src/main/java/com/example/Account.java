package com.example;

import com.example.Payment.*;

import java.util.Date;

import com.example.Billing.*;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Account extends AbstractBehavior<Account.Command> {

    public interface Command {}


    public static final class CheckSubmissionCommand implements Command {

        protected SubmitPaymentOrder paymentOrder;
        protected SubmitWithdrawalOrder withdrawOrder;
        protected SubmitDepositOrder depositOrder;
        final String replyTo;

        public CheckSubmissionCommand(final String replyTo) {

            this.replyTo = replyTo;

        }

    }

    public static final class SubmitPaymentOrder implements Command {

        final long paymentOrderId;
        final Date dateTime;
        final Double amount;
        final String accountId;
        final String bankId;
        protected ActorRef<PaymentOrderStatus> checkPaymentOrderId;

        public SubmitPaymentOrder(final long paymentOrderId, final Date dateTime, final Double amount,
                final String accountId, final String bankId) {

                        this.paymentOrderId = paymentOrderId;
                        this.dateTime = dateTime;
                        this.amount = amount;
                        this.accountId = accountId;
                        this.bankId = bankId;

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
        final long numberOfPaymentOrderRequests;
        final Double amount;
        final Double accountBalance;
        final String orderStatus;
        
        public PaymentOrderVerified(final long paymentOrderId,
                    final long numberOfPaymentOrderRequests, 
                    final Double amount,
                    final Double accountBalance, final String orderStatus) {

                        this.paymentOrderId = paymentOrderId;
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
                    final Double amount, final Double balance, final String orderStatus) {

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

    public static final class InstructExternalAccount implements Command {

        protected DebitCurrentAccount instruct;
        final boolean accountInstruction;
        final Double balance;
        final Double amount;
        protected String externalAccountId;
        protected ActorRef<ExternalAccountInstructed> instructExternalAccount;

        public InstructExternalAccount(final boolean accountInstruction, final Double balance, 
                    final Double amount) {

                        this.accountInstruction = accountInstruction;
                        this.balance = instruct.balance;
                        this.amount = instruct.check.paymentOrder.amount;

        }
    }

    public static final class ExternalAccountInstructed {

        protected InstructExternalAccount externalAccount;
        final Double amount;
        final long paymentOrderId;
        final Double balance;
        final String replyTo;

        public ExternalAccountInstructed(final Double amount, final long paymentOrderId,
                    final Double balance, final String replyTo) {

                        this.amount = amount;
                        this.paymentOrderId = paymentOrderId;
                        this.balance = balance;
                        this.replyTo = replyTo;

        }
    }

    public static final class InternalAccountInstructed {

        final Double amount;
        final String internalAccountId;
        final String externalAccountId;
        final String bankId;
        final long paymentOrderId;
        final String replyTo;
        protected Payment.InstructInternalAccount accountInstructed;

        public InternalAccountInstructed(final Double amount, final String internalAccountId, 
                    final String externalAccountId, final String bankId, final long paymentOrderId, 
                    final String replyTo) {

                        this.amount = amount;
                        this.internalAccountId = internalAccountId;
                        this.externalAccountId = externalAccountId;
                        this.bankId = bankId;
                        this.paymentOrderId = paymentOrderId;
                        this.replyTo = replyTo;

        }
    }

    public static final class CompletePaymentOrder implements Command {

        final long paymentOrderId;
        final String bankId;
        final String externalAccountId;
        final String accountId;
        final Double amount;
        final Date date;
        final Double balance;
        protected Payment.CreditExternalAccount creditExternal;
        protected ActorRef<PaymentOrderProcessed> confirmation;

        public CompletePaymentOrder(final long paymentOrderId, final String bankId, final String externalAccountId,
                    final String accountId, final Double amount, final Date date, final Double balance) {

                        this.paymentOrderId = creditExternal.paymentOrderId;
                        this.bankId = creditExternal.creditAccount.bankId;
                        this.externalAccountId = creditExternal.externalAccountId;
                        this.accountId = creditExternal.creditAccount.internalAccountId;
                        this.amount = creditExternal.amount;
                        this.date = creditExternal.creditAccount.identify.instruct.check.paymentOrder.dateTime;
                        this.balance = creditExternal.creditAccount.identify.balance;

        }
    }

    public static final class PaymentOrderProcessed {
       
        final String replyTo;
        protected CompletePaymentOrder ProcessOrder;

        public PaymentOrderProcessed(final String replyTo, long paymentOrderId, String accountId, String bankId,
                String externalAccountId, Date date, Double amount, Double balance) {

                        this.replyTo = replyTo;

        }
    }

    public static final class SubmitWithdrawalOrder implements Command {

        final Double balance;
        final Double amount;
        protected ActorRef<WithdrawalVerified> verify;
        protected ActorRef<WithdrawalRejected> reject;

        public SubmitWithdrawalOrder(final Double balance, final Double amount) {

                        this.balance = balance;
                        this.amount = amount;

        }
    }

    public static final class SubmitDepositOrder implements Command {

        final Double balance;
        final Double amount;
        protected ActorRef<DepositVerified> deposit;

        public SubmitDepositOrder(final Double balance, final Double amount) {

                        this.balance = balance;
                        this.amount = amount;

        }

    }

    public static final class WithdrawalVerified {

        final long withdrawProcessId;
        final long numberOfATMFeeWithdrawals;
        protected SubmitWithdrawalOrder checkBalance;

        public WithdrawalVerified(final long withdrawProcessId, final long numberOfATMFeeWithdrawals) {

                        this.withdrawProcessId = withdrawProcessId;
                        this.numberOfATMFeeWithdrawals = numberOfATMFeeWithdrawals;

        }
    }

    public static final class WithdrawalRejected {

        final long withdrawProcessId;
        protected SubmitWithdrawalOrder checkBalance;

        public WithdrawalRejected(final long withdrawProcessId) {

                        this.withdrawProcessId = withdrawProcessId;

        }
    }

    public static final class DepositVerified {

        final long depositProcessId;
        final long numberOfDeposits;
        final Double amount;
        protected SubmitDepositOrder checkBalance;

        public DepositVerified(final long depositProcessId, final long numberOfDeposits, final Double amount) {

                        this.depositProcessId = depositProcessId;
                        this.numberOfDeposits = numberOfDeposits;
                        this.amount = amount;

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

    public static final class EndOfMonthBillCalculated {

    }

    static enum Passivate implements Command {

        INSTANCE

    }

    public static Behavior<Command> create(String accountId, Double accountBalance, Double amount, 
                                        String mainCommand, String userPackage, long paymentOrderId, String bankId) {

        return Behaviors.setup(context -> new Account(context, accountId, accountBalance, amount, mainCommand, userPackage, 
        paymentOrderId, bankId));

    }

    private final String accountId;
    private final Double accountBalance;
    private final Double amount;

    private final String mainCommand;

    private final String userPackage;
    private final long paymentOrderId;
    private final String bankId;

    private final String currency;

    protected long numberOfPaymentOrderRequests;

    protected long studentPackagePaymentOrderLimit;
    protected long studentRequestsOrOccurences;
    protected long studentPackagePaymentFee;

    protected long incrementRequestOrOccurence;

    protected long normalPackagePaymentOrderLimit;
    protected long normalRequestsOrOccurences;
    protected long normalPackagePaymentFee;

    protected boolean externalAccountInstruction;

    Date date = new Date(System.currentTimeMillis());

    private Account(final ActorContext<Command> context, final String accountId, final Double accountBalance,
            final Double amount, final String mainCommand, final String userPackage, final long paymentOrderId, String bankId) {

        super(context);

        this.accountId = accountId;
        this.accountBalance = accountBalance;
        this.amount = amount;
        this.mainCommand = mainCommand;
        this.userPackage = userPackage;
        this.paymentOrderId = paymentOrderId;
        this.bankId = bankId;

        this.currency = "CZK";

        numberOfPaymentOrderRequests = 0;

        studentPackagePaymentOrderLimit = 3;
        studentRequestsOrOccurences = 0;
        studentPackagePaymentFee = 5;

        incrementRequestOrOccurence = 1;

        normalPackagePaymentOrderLimit = 5;
        normalRequestsOrOccurences = 0;
        normalPackagePaymentFee = 10;

        externalAccountInstruction = true;

        context.getLog().info("\nAccount actor is created with :: Account Id - %s, Balance - %.2f, Currency - %s, Process Name - %s, Requested amount - %.2f, User Package - %s\n", 
        accountId, accountBalance, currency, mainCommand, amount, userPackage);

    }

    @Override
    public Receive<Command> createReceive() {

        return newReceiveBuilder().onMessage(CheckSubmissionCommand.class, this::onCheckSubmissionCommand)
                .onMessage(SubmitPaymentOrder.class, this::onSubmitPaymentOrder)
                .onMessage(CheckAccountBalance.class, this::onCheckAccountBalance)
                .onMessage(DebitCurrentAccount.class, this::onDebitCurrentAccount)
                .onMessage(InstructExternalAccount.class, this::onInstructExternalAccount)
                .onMessage(CompletePaymentOrder.class, this::onCompletePaymentOrder)
                .onMessage(SubmitWithdrawalOrder.class, this::onSubmitWithdrawalOrder)
                .onMessage(SubmitDepositOrder.class, this::onSubmitDepositOrder)
                .onMessage(Passivate.class, m -> Behaviors.stopped())
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();

    }

    private Behavior<Command> onCheckSubmissionCommand(final CheckSubmissionCommand checkSubmission) {

        getContext().getLog().info(
                "SUBMISSION COMMANDS : `Payment`, `Withdraw`, `Deposit`\n"
        );

        if (this.mainCommand == "Payment") {

                this.getContext().getSelf().tell(new SubmitPaymentOrder(this.paymentOrderId, this.date, 
                this.amount, this.accountId, this.bankId));

        } else if (this.mainCommand == "Withdraw") {

                this.getContext().getSelf().tell(new SubmitWithdrawalOrder(this.accountBalance, this.amount));

        } else if (this.mainCommand == "Deposit") {

                this.getContext().getSelf().tell(new SubmitDepositOrder(this.accountBalance, this.amount));

        } else {

                getContext().getLog().info("ERROR! ENTER RELEVANT SUBMISSION COMMAND!\n");

        }

        return this;

    }

    private Behavior<Command> onSubmitPaymentOrder(final SubmitPaymentOrder paymentOrder) {
        getContext().getLog().info(
                "Submit Payment Order id = %d command received with Account id = %s and Bank id = %d on %s \n",
                paymentOrder.paymentOrderId, paymentOrder.accountId, paymentOrder.bankId, paymentOrder.dateTime);

        paymentOrder.checkPaymentOrderId.tell(new PaymentOrderStatus(paymentOrder.paymentOrderId, "SUBMITTED"));

        this.getContext().getSelf().tell(new CheckAccountBalance(paymentOrder));

        return this;
    }

    private Behavior<Command> onCheckAccountBalance(final CheckAccountBalance checkBalance) {

        getContext().getLog().info("Checking balance for Payment Order Id = %d \n",
                checkBalance.paymentOrder.paymentOrderId);

        if (checkBalance.paymentOrder.amount <= this.accountBalance) {

            numberOfPaymentOrderRequests += incrementRequestOrOccurence;

            checkBalance.verify.tell(new PaymentOrderVerified(checkBalance.paymentOrder.paymentOrderId,
                    numberOfPaymentOrderRequests, checkBalance.paymentOrder.amount,
                    this.accountBalance, "VERIFIED"));

            getContext().getLog().info("Balance for Payment Order Id = %d, status = %s \n",
                    checkBalance.paymentOrder.paymentOrderId, "VERIFIED");

            this.getContext().getSelf().tell(new DebitCurrentAccount(checkBalance.paymentOrder.paymentOrderId, 
            this.accountBalance, this.userPackage));        
                    
        } else {

            checkBalance.reject.tell(new PaymentOrderRejected(checkBalance.paymentOrder.paymentOrderId,
                    checkBalance.paymentOrder.amount, this.accountBalance, "REJECTED"));

            getContext().getLog().info("Balance for Payment Order Id = %d, status = %s \n",
                    checkBalance.paymentOrder.paymentOrderId, "REJECTED");

        }

        return this;

    }

    private Behavior<Command> onDebitCurrentAccount(final DebitCurrentAccount debitAccount) {

        getContext().getLog().info("Current Account Balance : %.2f %s. Offered packages below : `STUDENT` and `NORMAL` for Payment Order Id = %d \n",
        debitAccount.balance, this.currency, debitAccount.check.paymentOrder.paymentOrderId);

        if (debitAccount.userPackage == "Student") {

            if (numberOfPaymentOrderRequests <= studentPackagePaymentOrderLimit) {

                debitAccount.balance = (debitAccount.balance
                        - (studentRequestsOrOccurences + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.studentRequestsOrOccurences, String.format("%.2f %s debited from Account", 
                this.studentRequestsOrOccurences + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with %s package and Payment Order Id = %d : status = %s \n", 
                debitAccount.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");

            } else {

                studentRequestsOrOccurences = studentRequestsOrOccurences + incrementRequestOrOccurence;

                debitAccount.balance = (debitAccount.balance 
                        - (studentRequestsOrOccurences * studentPackagePaymentFee + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.studentRequestsOrOccurences, String.format("%.2f %s debited from Account",
                this.studentRequestsOrOccurences * studentPackagePaymentFee + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with %s package and Payment Order Id = %d : status = %s \n", 
                this.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");                

            }

        } else if (debitAccount.userPackage == "Normal") {

            if (numberOfPaymentOrderRequests <= normalPackagePaymentOrderLimit) {

                debitAccount.balance = (debitAccount.balance
                        - (normalRequestsOrOccurences + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.normalRequestsOrOccurences, String.format("%.2f %s debited from Account", 
                this.normalRequestsOrOccurences + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with %s package and Payment Order Id = %d : status = %s \n", 
                debitAccount.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");

            } else {

                normalRequestsOrOccurences = normalRequestsOrOccurences + incrementRequestOrOccurence;

                debitAccount.balance = (debitAccount.balance 
                        - (normalRequestsOrOccurences * normalPackagePaymentFee + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.normalRequestsOrOccurences, String.format("%.2f %s debited from Account",
                this.normalRequestsOrOccurences * normalPackagePaymentFee + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with %s package and Payment Order Id = %d : status = %s \n", 
                debitAccount.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");                

            }                

        }

        getContext().getLog().info("Account is Debited. Current Balance : %.2f %s \n", debitAccount.balance, this.currency);

        this.getContext().getSelf().tell(new InstructExternalAccount(this.externalAccountInstruction,
        debitAccount.balance, debitAccount.check.paymentOrder.amount));         

        return this;

    }
    
    private Behavior<Command> onInstructExternalAccount(final InstructExternalAccount instructAccount) {

        getContext().getLog().info("Transfer Request amount : %.2f %s. Instructing External Account with Id : %s. \n", 
        instructAccount.amount, this.currency, instructAccount.externalAccountId);

        instructAccount.instructExternalAccount.tell(new ExternalAccountInstructed(instructAccount.amount, 
        instructAccount.instruct.check.paymentOrder.paymentOrderId, instructAccount.instruct.balance, "EXTERNAL ACCOUNT IS INSTRUCTED : SUCCESS"));

        this.getContext().getSelf().tell(new IdentifyRouteToExternalAccount(instructAccount.instruct.check.paymentOrder.accountId,
        instructAccount.instruct.check.paymentOrder.paymentOrderId, instructAccount.externalAccountId, 
        instructAccount.amount, instructAccount.instruct.check.paymentOrder.bankId));

        return this;

    }
    
    private Behavior<Command> onCompletePaymentOrder(final CompletePaymentOrder completeOrder) {

        getContext().getLog().info("Payment Order is Completed, now It is Processed! \n");

        completeOrder.confirmation.tell(new PaymentOrderProcessed("\nTRANSACTION LOG: *PAYMENT ORDER ID* - %o | *CLIENT ACCOUNT ID* - %s | *BANK ID* - %s | *RECEIVER ACCOUNT ID*  - %s |  | *DATE* - %s | *AMOUNT* - %f | *BALANCE* - %.2f\n",
        completeOrder.paymentOrderId, completeOrder.accountId, 
        completeOrder.bankId, completeOrder.externalAccountId, 
        completeOrder.date, completeOrder.amount, completeOrder.balance));
        
        this.getContext().getSelf().tell(new CalculatePaymentOrderFee(completeOrder.accountId, completeOrder.amount,
        completeOrder.balance));

        return this;

    }

    private Behavior<Command> onSubmitWithdrawalOrder(final SubmitWithdrawalOrder withdrawOrder) {

        return this;                        

    }

    private Behavior<Command> onSubmitDepositOrder(final SubmitDepositOrder depositOrder) {

        return this;                        

    }    
    
    private Behavior<Command> onPostStop() {
        getContext().getLog().info("Account actor is stopped with :: Account id - %s, balance - %.2f, currency - %s\n",
        accountId, accountBalance, currency);
        return Behaviors.stopped();
    }    
    
}