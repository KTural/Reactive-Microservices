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
        
        final String status;

        public PaymentOrderStatus(final String status) {

                        this.status = status;

        }
    }

    public static final class CheckAccountBalance implements Command {

        protected SubmitPaymentOrder paymentOrder;
        final String replyTo;
        protected ActorRef<PaymentOrderVerified> verify;
        protected ActorRef<PaymentOrderRejected> reject;

        public CheckAccountBalance(final String replyTo) {

                        this.replyTo = replyTo;

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

        public PaymentOrderProcessed(final String replyTo) {

                        this.replyTo = replyTo;

        }
    }

    public static final class SubmitWithdrawalOrder implements Command {

        final Double balance;
        final Double amount;
        final long paymentOrderId;
        final String bankId;
        final Date date;
        final String accountId;
        protected ActorRef<WithdrawalVerified> verify;
        protected ActorRef<WithdrawalRejected> reject;

        public SubmitWithdrawalOrder(final Double balance, final Double amount, final long paymentOrderId, final String bankId, 
                    final Date date, final String accountId) {

                        this.balance = balance;
                        this.amount = amount;
                        this.paymentOrderId = paymentOrderId;
                        this.bankId = bankId;
                        this.date = date;
                        this.accountId = accountId;

        }
    }

    public static final class SubmitDepositOrder implements Command {

        final Double balance;
        final Double amount;
        final long paymentOrderId;
        final String bankId;
        final Date date;
        final String accountId;                
        protected ActorRef<DepositVerified> deposit;

        public SubmitDepositOrder(final Double balance, final Double amount, final long paymentOrderId, final String bankId, 
                    final Date date, final String accountId) {

                        this.balance = balance;
                        this.amount = amount;
                        this.paymentOrderId = paymentOrderId;
                        this.bankId = bankId;
                        this.date = date;
                        this.accountId = accountId;

        }

    }

    public static final class WithdrawalVerified {

        final String withdrawalId;
        final String accountId;
        final Date date;
        protected SubmitWithdrawalOrder checkBalance;
        final String replyTo;

        public WithdrawalVerified(final String withdrawalId, final String accountId, 
                    final Date date, final String replyTo) {

                        this.withdrawalId = withdrawalId;
                        this.accountId = accountId;
                        this.date = date;
                        this.replyTo = replyTo;

        }
    }

    public static final class WithdrawalRejected {

        final String withdrawalId;
        final String accountId;
        final Date date;
        protected SubmitWithdrawalOrder checkBalance;
        final String replyTo;

        public WithdrawalRejected(final String withdrawalId, final String accountId, 
                    final Date date, final String replyTo) {

                        this.withdrawalId = withdrawalId;
                        this.accountId = accountId;
                        this.date = date;
                        this.replyTo = replyTo;

        }
    }

    public static final class DepositVerified {

        final String depositId;
        final String accountId;
        final Date date;
        final String replyTo;
        protected SubmitDepositOrder checkBalance;

        public DepositVerified(final String depositId, final String accountId, final Date date, final String replyTo) {

                        this.depositId = depositId;
                        this.accountId = accountId;
                        this.date = date;
                        this.replyTo = replyTo;

        }
    }

    public static final class WithdrawalCompleted {

        final String withdrawalId;
        final Double balance;
        final String userPackage;
        final String accountId;
        final Date date;
        final Double amount;
        final String replyTo;
        protected DebitWithdrawnAccount debit;

        public WithdrawalCompleted(final String withdrawalId, final Double balance, final String userPackage, 
                    final String accountId, final Date date, final Double amount, final String replyTo) {

                        this.withdrawalId = withdrawalId;
                        this.balance = balance;
                        this.userPackage = userPackage;
                        this.accountId = accountId;
                        this.date = date;
                        this.amount = amount;
                        this.replyTo = replyTo;

        }
    }

    public static final class DepositCompleted {

        final String depositId;
        final Double balance;
        final String userPackage;
        final String accountId;
        final Date date;
        final Double amount;
        final String replyTo;
        protected CreditDepositedAccount credit;

        public DepositCompleted(final String depositId, final Double balance, final String userPackage, 
                    final String accountId, final Date date, final Double amount, final String replyTo) {

                        this.depositId = depositId;
                        this.balance = balance;
                        this.userPackage = userPackage;
                        this.accountId = accountId;
                        this.date = date;
                        this.amount = amount;
                        this.replyTo = replyTo;

        }
    }

    public static final class EndOfMonthBillCalculated {

        public EndOfMonthBillCalculated() {

        }

    }

    static enum Passivate implements Command {

        INSTANCE

    }

    public static Behavior<Command> create(String accountId, Double accountBalance, Double amount, 
                                        String mainCommand, String userPackage, long paymentOrderId, String bankId, String currency,
                                        String withdrawalId, String depositId) {

        return Behaviors.setup(context -> new Account(context, accountId, accountBalance, amount, mainCommand, userPackage, 
        paymentOrderId, bankId, currency, withdrawalId, depositId));

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

    private Account onCheckSubmissionCommand(final CheckSubmissionCommand checkSubmission) {

        getContext().getLog().info(
                "SUBMISSION COMMANDS : `Payment`, `Withdraw`, `Deposit`\n"
        );

        if (this.mainCommand == "Payment") {

                this.getContext().getSelf().tell(new SubmitPaymentOrder(this.paymentOrderId, this.date, 
                this.amount, this.accountId, this.bankId));

        } else if (this.mainCommand == "Withdraw") {

                this.getContext().getSelf().tell(new SubmitWithdrawalOrder(this.accountBalance, this.amount, 
                this.paymentOrderId, this.bankId, this.date, this.accountId));

        } else if (this.mainCommand == "Deposit") {

                this.getContext().getSelf().tell(new SubmitDepositOrder(this.accountBalance, this.amount,
                this.paymentOrderId, this.bankId, this.date, this.accountId));

        } else {

                getContext().getLog().info("ERROR! ENTER RELEVANT SUBMISSION COMMAND!\n");

        }

        return this;

    }

    private Account onSubmitPaymentOrder(SubmitPaymentOrder paymentOrder) {
        getContext().getLog().info(
                "Submit Payment Order id = {} command received with Account id = {} and Bank id = {} on {} \n",
                paymentOrder.paymentOrderId, paymentOrder.accountId, paymentOrder.bankId, paymentOrder.dateTime);

        paymentOrder.checkPaymentOrderId.tell(new PaymentOrderStatus("SUBMITTED"));

        this.getContext().getSelf().tell(new CheckAccountBalance("Instruct Checking Balance!\n"));
        return this;
    }

    private Account onCheckAccountBalance(CheckAccountBalance checkBalance) {

        getContext().getLog().info("Checking balance for Payment Order Id = {} \n",
                checkBalance.paymentOrder.paymentOrderId);

        if (checkBalance.paymentOrder.amount <= this.accountBalance) {

            numberOfPaymentOrderRequests += incrementRequestOrOccurence;

            checkBalance.verify.tell(new PaymentOrderVerified(checkBalance.paymentOrder.paymentOrderId,
                    numberOfPaymentOrderRequests, checkBalance.paymentOrder.amount,
                    this.accountBalance, "VERIFIED"));

            getContext().getLog().info("Balance for Payment Order Id = {}, status = {} \n",
                    checkBalance.paymentOrder.paymentOrderId, "VERIFIED");

            this.getContext().getSelf().tell(new DebitCurrentAccount(checkBalance.paymentOrder.paymentOrderId, 
            this.accountBalance, this.userPackage));        
                    
        } else {

            checkBalance.reject.tell(new PaymentOrderRejected(checkBalance.paymentOrder.paymentOrderId,
                    checkBalance.paymentOrder.amount, this.accountBalance, "REJECTED"));

            getContext().getLog().info("Balance for Payment Order Id = {}, status = {} \n",
                    checkBalance.paymentOrder.paymentOrderId, "REJECTED");

        }

        return this;

    }

    private Account onDebitCurrentAccount(final DebitCurrentAccount debitAccount) {

        getContext().getLog().info("Current Account Balance : {} {}. Offered packages below : `STUDENT` and `NORMAL` for Payment Order Id = {} \n",
        debitAccount.balance, this.currency, debitAccount.check.paymentOrder.paymentOrderId);

        if (debitAccount.userPackage == "Student") {

            if (numberOfPaymentOrderRequests <= studentPackagePaymentOrderLimit) {

                debitAccount.balance = (debitAccount.balance
                        - (studentRequestsOrOccurences + debitAccount.check.paymentOrder.amount));

                numberOfPaymentOrderRequests += incrementRequestOrOccurence;      

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.studentRequestsOrOccurences, String.format("{} {} is debited from Account", 
                this.studentRequestsOrOccurences + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with %s package and Payment Order Id = {} : status = {} \n", 
                debitAccount.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");

            } else {

                studentRequestsOrOccurences = studentRequestsOrOccurences + incrementRequestOrOccurence;

                debitAccount.balance = (debitAccount.balance 
                        - (studentRequestsOrOccurences * studentPackagePaymentFee + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.studentRequestsOrOccurences, String.format("{} {} is debited from Account",
                this.studentRequestsOrOccurences * studentPackagePaymentFee + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with {} package and Payment Order Id = {} : status = {} \n", 
                this.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");                

            }

        } else if (debitAccount.userPackage == "Normal") {

            if (numberOfPaymentOrderRequests <= normalPackagePaymentOrderLimit) {

                debitAccount.balance = (debitAccount.balance
                        - (normalRequestsOrOccurences + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.normalRequestsOrOccurences, String.format("{} {} debited from Account", 
                this.normalRequestsOrOccurences + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with {} package and Payment Order Id = {} : status = {} \n", 
                debitAccount.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");

            } else {

                normalRequestsOrOccurences = normalRequestsOrOccurences + incrementRequestOrOccurence;

                debitAccount.balance = (debitAccount.balance 
                        - (normalRequestsOrOccurences * normalPackagePaymentFee + debitAccount.check.paymentOrder.amount));

                debitAccount.recordTransactionAmount.tell(new AccountDebited(debitAccount.check.paymentOrder.paymentOrderId,
                debitAccount.balance, this.normalRequestsOrOccurences, String.format("{} {} debited from Account",
                this.normalRequestsOrOccurences * normalPackagePaymentFee + debitAccount.check.paymentOrder.amount, this.currency)));

                getContext().getLog().info("Debiting Current Account with {} package and Payment Order Id = {} : status = {} \n", 
                debitAccount.userPackage, debitAccount.check.paymentOrder.paymentOrderId, "VERIFIED");                

            }                

        }

        getContext().getLog().info("Account is Debited. Current Balance : {} {} \n", debitAccount.balance, this.currency);

        this.getContext().getSelf().tell(new InstructExternalAccount(this.externalAccountInstruction,
        debitAccount.balance, debitAccount.check.paymentOrder.amount));         

        return this;

    }
    
    private Account onInstructExternalAccount(final InstructExternalAccount instructAccount) {

        getContext().getLog().info("Transfer Request amount : {} {}. Instructing External Account with Id : {}. \n", 
        instructAccount.amount, this.currency, instructAccount.externalAccountId);

        instructAccount.instructExternalAccount.tell(new ExternalAccountInstructed(instructAccount.amount, 
        instructAccount.instruct.check.paymentOrder.paymentOrderId, instructAccount.instruct.balance, "EXTERNAL ACCOUNT IS INSTRUCTED : SUCCESS"));

        this.getContext().getSelf().tell(new IdentifyRouteToExternalAccount(instructAccount.instruct.check.paymentOrder.accountId,
        instructAccount.instruct.check.paymentOrder.paymentOrderId, instructAccount.externalAccountId, 
        instructAccount.amount, instructAccount.instruct.check.paymentOrder.bankId));

        return this;

    }
    
    private Account onCompletePaymentOrder(final CompletePaymentOrder completeOrder) {

        getContext().getLog().info("Payment Order is going to be completed, now It is Processed! and will be Calculated1\n");

        completeOrder.confirmation.tell(new PaymentOrderProcessed("Completed Payment with status: VERIFIED!"));
        
        this.getContext().getSelf().tell(new CalculatePaymentOrderFee(completeOrder.accountId, completeOrder.amount, this.currency,
        completeOrder.balance));

        return this;

    }

    private Account onSubmitWithdrawalOrder(final SubmitWithdrawalOrder withdrawOrder) {

        if (this.userPackage == "Student") {

            if (this.amount < this.accountBalance) {

                withdrawOrder.verify.tell(new WithdrawalVerified(this.withdrawalId, this.accountId, this.date, "VERIFIED!"));

                this.getContext().getSelf().tell(new DebitWithdrawnAccount(this.withdrawalId, this.accountBalance, this.userPackage, 
                this.accountId, this.date, this.amount, this.currency));

            } else {

                withdrawOrder.reject.tell(new WithdrawalRejected(this.withdrawalId, this.accountId, this.date, "REJECTED!"));

            }

        } else if (this.userPackage == "Normal") {

            if (this.amount < this.accountBalance) {

                withdrawOrder.verify.tell(new WithdrawalVerified(this.withdrawalId, this.accountId, this.date, "VERIFIED!"));

                this.getContext().getSelf().tell(new DebitWithdrawnAccount(this.withdrawalId, this.accountBalance, this.userPackage, 
                this.accountId, this.date, this.amount, this.currency));                

            } else {

                withdrawOrder.reject.tell(new WithdrawalRejected(this.withdrawalId, this.accountId, this.date, "REJECTED!"));

            }

        } else {

            getContext().getLog().info("ERROR! Please, Enter relevant package name to do withdrawal!\n");

        }

        return this;                        

    }

    private Account onSubmitDepositOrder(final SubmitDepositOrder depositOrder) {

        if (this.userPackage == "Student") {

            depositOrder.deposit.tell(new DepositVerified(this.depositId, this.accountId, this.date, "VERIFIED!"));

            this.getContext().getSelf().tell(new CreditDepositedAccount(this.depositId, this.accountBalance, this.userPackage, 
            this.accountId, this.date, this.amount, this.currency));


        } else if (this.userPackage == "Normal") {

            depositOrder.deposit.tell(new DepositVerified(this.depositId, this.accountId, this.date, "VERIFIED!"));

            this.getContext().getSelf().tell(new CreditDepositedAccount(this.depositId, this.accountBalance, this.userPackage, 
            this.accountId, this.date, this.amount, this.currency));

        } else {

            getContext().getLog().info("ERROR! Please, Enter relevant package name to do deposit!\n");

        }

        return this;                        

    }    
    
    private Behavior<Command> onPostStop() {

        getContext().getLog().info("Account actor is stopped with :: Account id - {}, balance - {}, currency - {}\n",
        accountId, accountBalance, currency);

        return Behaviors.stopped();
    }    

    private final String accountId;
    private final Double accountBalance;
    private final Double amount;

    private final String mainCommand;

    private final String userPackage;
    private final long paymentOrderId;
    private final String bankId;
    private final String currency;

    private final String withdrawalId;
    private final String depositId;

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
            final Double amount, final String mainCommand, final String userPackage, final long paymentOrderId, final String bankId, 
            final String currency, final String withdrawalId, final String depositId) {

        super(context);

        this.accountId = accountId;
        this.accountBalance = accountBalance;
        this.amount = amount;
        this.mainCommand = mainCommand;
        this.userPackage = userPackage;
        this.paymentOrderId = paymentOrderId;
        this.bankId = bankId;
        this.currency = currency;
        this.withdrawalId = withdrawalId;
        this.depositId = depositId;

        numberOfPaymentOrderRequests = 0;

        studentPackagePaymentOrderLimit = 3;
        studentRequestsOrOccurences = 0;
        studentPackagePaymentFee = 5;

        incrementRequestOrOccurence = 1;

        normalPackagePaymentOrderLimit = 5;
        normalRequestsOrOccurences = 0;
        normalPackagePaymentFee = 10;

        externalAccountInstruction = true;

        context.getLog().info("Account actor is created with :: Account Id - {}, Bank Id - {}, Balance - {}, Currency - {}, Process Name - {}, Requested amount - {}, User Package - {}\n", 
        accountId, bankId, accountBalance, currency, mainCommand, amount, userPackage);

    }    
    
}

