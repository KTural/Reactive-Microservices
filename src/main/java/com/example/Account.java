package com.example;

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
        final ActorRef<SubmissionCommandsChecked> checkedSubmit;

        public CheckSubmissionCommand(final String replyTo, ActorRef<SubmissionCommandsChecked> checkedSubmit) {

            this.replyTo = replyTo;
            this.checkedSubmit = checkedSubmit;

        }

    }

    public static final class SubmissionCommandsChecked {

        final String status;

        public SubmissionCommandsChecked(final String status) {

                        this.status = status;

        }

    }

    public static final class SubmitPaymentOrder implements Command {

        final long paymentOrderId;
        final Date dateTime;
        final Double amount;
        final String accountId;
        final String bankId;
        final String orderStatus;
        final ActorRef<PaymentOrderStatus> checkPaymentOrderId;

        public SubmitPaymentOrder(final long paymentOrderId, final Date dateTime, final Double amount,
                final String accountId, final String bankId, final String orderStatus,
                final ActorRef<PaymentOrderStatus> checkPaymentOrderId) {

                        this.paymentOrderId = paymentOrderId;
                        this.dateTime = dateTime;
                        this.amount = amount;
                        this.accountId = accountId;
                        this.bankId = bankId;
                        this.orderStatus = orderStatus;
                        this.checkPaymentOrderId = checkPaymentOrderId;

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
        final ActorRef<PaymentOrderVerified> verify;
        final ActorRef<PaymentOrderRejected> reject;

        public CheckAccountBalance(final String replyTo, final ActorRef<PaymentOrderVerified> verify,
                final ActorRef<PaymentOrderRejected> reject) {

                        this.replyTo = replyTo;
                        this.verify = verify;
                        this.reject = reject;
        

        }
    }

    public static final class PaymentOrderVerified {
        
        final String orderStatus;
        
        public PaymentOrderVerified(final String orderStatus) {

                        this.orderStatus = orderStatus;

                    }
    }

    public static final class PaymentOrderRejected {

        final String orderStatus;

        public PaymentOrderRejected(final String orderStatus) {

                        this.orderStatus = orderStatus;

                    }        
    }

    public static final class DebitCurrentAccount implements Command {

        final long paymentOrderId;
        protected CheckAccountBalance check;
        protected Double balance;
        final String userPackage;
        final String replyTo;
        final ActorRef<AccountDebited> recordTransactionAmount;

        public DebitCurrentAccount(final long paymentOrderId, Double balance, final String userPackage,
                    final String replyTo, final ActorRef<AccountDebited> recordTransactionAmount) {

                        this.paymentOrderId = paymentOrderId;
                        this.balance = balance;
                        this.userPackage = userPackage;
                        this.replyTo = replyTo;
                        this.recordTransactionAmount = recordTransactionAmount;

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
        final String externalAccountId;
        final String replyTo;
        final ActorRef<ExternalAccountInstructed> instructExternalAccount;

        public InstructExternalAccount(final boolean accountInstruction, final Double balance, 
                    final String externalAccountId,
                    final Double amount, final String replyTo, 
                    final ActorRef<ExternalAccountInstructed> instructExternalAccount) {

                        this.accountInstruction = accountInstruction;
                        this.balance = balance;
                        this.externalAccountId = externalAccountId;
                        this.amount = amount;
                        this.replyTo = replyTo;
                        this.instructExternalAccount = instructExternalAccount;

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
        final String message;
        protected Payment.CreditExternalAccount creditExternal;
        final ActorRef<PaymentOrderProcessed> confirmation;

        public CompletePaymentOrder(final long paymentOrderId, final String bankId, final String externalAccountId,
                    final String accountId, final Double amount, final Date date, final Double balance,
                    final String message, final ActorRef<PaymentOrderProcessed> confirmation) {

                        this.paymentOrderId = paymentOrderId;
                        this.bankId = bankId;
                        this.externalAccountId = externalAccountId;
                        this.accountId = accountId;
                        this.amount = amount;
                        this.date = date;
                        this.balance = balance;
                        this.message = message;
                        this.confirmation = confirmation;

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
        final String message;
        final ActorRef<WithdrawalVerified> verify;
        final ActorRef<WithdrawalRejected> reject;

        public SubmitWithdrawalOrder(final Double balance, final Double amount, final long paymentOrderId, final String bankId, 
                    final Date date, final String accountId, final String message, final ActorRef<WithdrawalVerified> verify,
                    final ActorRef<WithdrawalRejected> reject) {

                        this.balance = balance;
                        this.amount = amount;
                        this.paymentOrderId = paymentOrderId;
                        this.bankId = bankId;
                        this.date = date;
                        this.accountId = accountId;
                        this.message = message;
                        this.verify = verify;
                        this.reject = reject;

        }
    }

    public static final class SubmitDepositOrder implements Command {

        final Double balance;
        final Double amount;
        final long paymentOrderId;
        final String bankId;
        final Date date;
        final String accountId;             
        final String message;   
        final ActorRef<DepositVerified> deposit;

        public SubmitDepositOrder(final Double balance, final Double amount, final long paymentOrderId, final String bankId, 
                    final Date date, final String accountId, final String message,
                    final ActorRef<DepositVerified> deposit) {

                        this.balance = balance;
                        this.amount = amount;
                        this.paymentOrderId = paymentOrderId;
                        this.bankId = bankId;
                        this.date = date;
                        this.accountId = accountId;
                        this.message = message;
                        this.deposit = deposit;

        }

    }

    public static final class WithdrawalVerified {

        final String replyTo;

        public WithdrawalVerified(final String replyTo) {

                        this.replyTo = replyTo;

        }
    }

    public static final class WithdrawalRejected {

        final String replyTo;

        public WithdrawalRejected(final String replyTo) {

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

        final String replyTo;

        public EndOfMonthBillCalculated(final String replyTo) {

                        this.replyTo = replyTo;

        }

    }

    static enum Passivate implements Command {

        INSTANCE

    }

    public static Behavior<Command> create(String accountId, String externalAccountId, Double accountBalance, Double amount, 
                                        String mainCommand, String userPackage, long paymentOrderId, String bankId, String currency,
                                        String withdrawalId, String depositId) {

        return Behaviors.setup(context -> new Account(context, accountId, externalAccountId, accountBalance, amount, mainCommand, userPackage, 
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

        System.out.println("\n\n\n  CHECKING SUBMISSION ... \n\n\n");

        checkSubmission.checkedSubmit.tell(new SubmissionCommandsChecked("CHECK SUBMISSION!"));

        getContext().getLog().info("*** {} Order *** Submission is selected!", this.mainCommand);

        return this;

    }

    private Account onSubmitPaymentOrder(SubmitPaymentOrder paymentOrder) {
        getContext().getLog().info(
                "Submit Payment Order id = {} command received with Account id = {} and Bank id = {} on {} \n",
                this.paymentOrderId, this.accountId, this.bankId, this.date);

        paymentOrder.checkPaymentOrderId.tell(new PaymentOrderStatus("SUBMIT"));

        System.out.println("\n\n\n SUBMITTING PAYMENT ORDER ... \n\n\n");

        getContext().getLog().info("Payment Order is SUBMITTED!");

        return this;
    }

    private Account onCheckAccountBalance(CheckAccountBalance checkBalance) {

        getContext().getLog().info("Checking balance for Payment Order Id = {} \n",
                this.paymentOrderId);

        if (this.amount <= this.accountBalance) {

            numberOfPaymentOrderRequests += incrementRequestOrOccurence;

            checkBalance.verify.tell(new PaymentOrderVerified("VERIFIED!"));

            System.out.println("\n\n\n CHECKING BALANCE ... \n\n\n");

            getContext().getLog().info(String.format("%.2f %s Balance for Payment Order Id -> %o, status -> %s , number of Payment Order Requests -> %o\n",
                    this.accountBalance, this.currency,
                    this.paymentOrderId, "VERIFIED!", numberOfPaymentOrderRequests));
                  
                    
        } else {

            checkBalance.reject.tell(new PaymentOrderRejected("REJECTED!"));

            System.out.println("\n\n\n CHECKING BALANCE ... \n\n\n");

            getContext().getLog().info(String.format("Balance for Payment Order Id = %o is %.2f %s, status = %s \n",
                    this.paymentOrderId, this.accountBalance, this.currency, "REJECTED!"));

            getContext().getLog().info("PAYMENT ORDER IS REJECTED! NOT ENOUGH BALANCE TO PROCESS DESIRED AMOUNT!!!\n");     

        } 

        return this;

    }

    private Account onDebitCurrentAccount(final DebitCurrentAccount debitAccount) {

        getContext().getLog().info(String.format("Current Account Balance : %.2f %s. Offered packages below : `STUDENT` and `NORMAL` for Payment Order Id = %o \n",
        this.accountBalance, this.currency, this.paymentOrderId));

        if (this.amount < this.accountBalance) {

            if (this.userPackage == "Student") {

                if (numberOfPaymentOrderRequests <= studentPackagePaymentOrderLimit) {

                    this.accountBalance = (this.accountBalance
                            - (studentRequestsOrOccurences + this.amount));

                    numberOfPaymentOrderRequests += incrementRequestOrOccurence;      

                    debitAccount.recordTransactionAmount.tell(new AccountDebited(this.paymentOrderId,
                    this.accountBalance, studentRequestsOrOccurences, "ACCOUNT IS DEBITED!"));

                    System.out.println("\n\n\n DEBITING ACCOUNT ... \n\n\n");

                    getContext().getLog().info(String.format("%.2f %s is Debited from Current Account with %s package and Payment Order Id = %o : status = %s \n", 
                    this.studentRequestsOrOccurences + this.amount , this.currency, this.userPackage,
                    this.paymentOrderId, "VERIFIED"));

                } else {

                    studentRequestsOrOccurences = studentRequestsOrOccurences + incrementRequestOrOccurence;

                    this.accountBalance = (this.accountBalance 
                            - (studentRequestsOrOccurences * studentPackagePaymentFee + this.amount));

                    debitAccount.recordTransactionAmount.tell(new AccountDebited(this.paymentOrderId,
                    this.accountBalance, studentRequestsOrOccurences, "ACCOUNT IS DEBITED!"));
                    
                    System.out.println("\n\n\n DEBITING ACCOUNT ... \n\n\n");

                    getContext().getLog().info(String.format("%.2f %s is Debited from Current Account with %s package and Payment Order Id = %o : status = %s \n", 
                    this.studentRequestsOrOccurences * studentPackagePaymentFee + this.amount, this.currency, this.userPackage, 
                    this.paymentOrderId, "VERIFIED"));                

                }

            } else if (this.userPackage == "Normal") {

                if (numberOfPaymentOrderRequests <= normalPackagePaymentOrderLimit) {

                    this.accountBalance = (this.accountBalance
                            - (normalRequestsOrOccurences + this.amount));

                    numberOfPaymentOrderRequests += incrementRequestOrOccurence;

                    debitAccount.recordTransactionAmount.tell(new AccountDebited(this.paymentOrderId,
                    this.accountBalance, normalRequestsOrOccurences, "ACCOUNT IS DEBITED!"));

                    System.out.println("\n\n\n DEBITING ACCOUNT ... \n\n\n");

                    getContext().getLog().info(String.format("%.2f %s is Debited from Current Account with %s package and Payment Order Id = %o : status = %s \n", 
                    normalRequestsOrOccurences + this.amount, this.currency, this.userPackage,
                    this.paymentOrderId, "VERIFIED"));

                } else {

                    normalRequestsOrOccurences = normalRequestsOrOccurences + incrementRequestOrOccurence;

                    this.accountBalance = (this.accountBalance 
                            - (normalRequestsOrOccurences * normalPackagePaymentFee + this.amount));

                    debitAccount.recordTransactionAmount.tell(new AccountDebited(this.paymentOrderId,
                    this.accountBalance, normalRequestsOrOccurences, "ACCOUNT IS DEBITED!"));

                    System.out.println("\n\n\n DEBITING ACCOUNT ... \n\n\n");

                    getContext().getLog().info(String.format("%.2f %s is Debited from Current Account with %s package and Payment Order Id = %o : status = %s \n", 
                    this.normalRequestsOrOccurences * normalPackagePaymentFee + this.amount, this.userPackage, this.userPackage,
                    this.paymentOrderId, "VERIFIED"));                

                }                

            }

            getContext().getLog().info(String.format("Account is Debited. Current Balance : %.2f %s \n", this.accountBalance, this.currency));

        } else {

            getContext().getLog().info("Account failed to be debited! NOT ENOUGH BALANCE! \n");

        }

        return this;

    }
    
    private Account onInstructExternalAccount(final InstructExternalAccount instructAccount) {

        getContext().getLog().info(String.format("Transfer Request amount : %.2f %s. Instructing External Account with Id : %s. \n", 
        this.amount, this.currency, Account.externalAccountId));

        instructAccount.instructExternalAccount.tell(new ExternalAccountInstructed(this.amount, 
        this.paymentOrderId, this.accountBalance, "EXTERNAL ACCOUNT IS INSTRUCTED"));

        System.out.println("\n\n\n INSTRUCTING EXTERNAL ACCOUNT ... \n\n\n");

        getContext().getLog().info(" EXTERNAL ACCOUNT STATUS: INSTRUCTED! \n\n\n");

        return this;

    }
    
    private Account onCompletePaymentOrder(final CompletePaymentOrder completeOrder) {

        getContext().getLog().info("Payment Order is going to be completed, now It is Processed! and will be Calculated\n");

        completeOrder.confirmation.tell(new PaymentOrderProcessed("VERIFIED COMPLETION!"));
        
        System.out.println("\n\n\n PROCESSING PAYMENT ORDER ... \n\n\n");

        return this;

    }

    private Account onSubmitWithdrawalOrder(final SubmitWithdrawalOrder withdrawOrder) {

        if (this.userPackage == "Student") {

            if (this.amount < this.accountBalance) {

                getContext().getLog().info("Submit Withdrawal Order id = {} command received with Account id = {}, Bank id = {} and Balance = {} {} on {} ",
                this.withdrawalId, this.accountId, this.bankId, this.accountBalance, this.currency, this.date);

                withdrawOrder.verify.tell(new WithdrawalVerified("VERIFIED!"));

                System.out.println("\n\n\n SUBMITTING WITHDRAWAL ORDER FOR STUDENT PACKAGE OPTION ... \n\n\n");

            } else {
                
                getContext().getLog().info("WITHDRAWAL ORDER IS REJECTED. NOT PROCESSED!");

                withdrawOrder.reject.tell(new WithdrawalRejected("REJECTED!"));

                System.out.println("\n\n\n REJECTING WITHDRAWAL SUBMISSION DUE TO INSUFFICIENT BALANCE ... \n\n\n");

            }

        } else if (this.userPackage == "Normal") {

            if (this.amount < this.accountBalance) {

                getContext().getLog().info("Submit Withdrawal Order id = {} command received with Account id = {}, Bank id = {} and Balance = {} {} on {} ",
                this.withdrawalId, this.accountId, this.bankId, this.accountBalance, this.currency, this.date);                

                withdrawOrder.verify.tell(new WithdrawalVerified("VERIFIED!"));  
                
                System.out.println("\n\n\n SUBMITTING WITHDRAWAL ORDER FOR NORMAL PACKAGE OPTION ... \n\n\n");                

            } else {

                getContext().getLog().info("WITHDRAWAL ORDER IS REJECTED. NOT PROCESSED!");                

                withdrawOrder.reject.tell(new WithdrawalRejected("REJECTED!"));
 
                System.out.println("\n\n\n REJECTING WITHDRAWAL SUBMISSION DUE TO INSUFFICIENT BALANCE ... \n\n\n");

            }

        } else {

            getContext().getLog().info("ERROR! Please, Enter relevant package name to do withdrawal!\n");

        }

        return this;                        

    }

    private Account onSubmitDepositOrder(final SubmitDepositOrder depositOrder) {

        if (this.userPackage == "Student") {

            getContext().getLog().info("Submit Deposit Order id = {} command with package `{}` received with Account id = {}, Bank id = {} and Balance = {} {} on {} ",
            this.depositId, this.userPackage, this.accountId, this.bankId, this.accountBalance, this.currency, this.date);

            depositOrder.deposit.tell(new DepositVerified(this.depositId, this.accountId, this.date, "VERIFIED!"));

            System.out.println("\n\n\n SUBMITTING DEPOSIT ORDER FOR STUDENT PACKAGE OPTION ... \n\n\n");

        } else if (this.userPackage == "Normal") {

            getContext().getLog().info("Submit Deposit Order id = {} command with package `{}` received with Account id = {}, Bank id = {} and Balance = {} {} on {} ",
            this.depositId, this.userPackage, this.accountId, this.bankId, this.accountBalance, this.currency, this.date);            

            depositOrder.deposit.tell(new DepositVerified(this.depositId, this.accountId, this.date, "VERIFIED!"));

            System.out.println("\n\n\n SUBMITTING DEPOSIT ORDER FOR NORMAL PACKAGE OPTION ... \n\n\n");            

        } else {

            getContext().getLog().info("ERROR! Please, Enter relevant package name to do deposit!\n");

        }

        return this;                        

    }    
    
    private Behavior<Command> onPostStop() {

        getContext().getLog().info(String.format("Account actor is stopped with :: Account id - %s, External Account id - %s, Main Command Order - %s, Amount to Process - %.2f %s, Package - %s\n",
        this.accountId, Account.externalAccountId, this.mainCommand, this.amount, this.currency, this.userPackage));

        return Behaviors.stopped();
    }  

    private final String accountId;
    static String externalAccountId;
    protected Double accountBalance;
    private final Double amount;

    private final String mainCommand;

    private final String userPackage;
    private final long paymentOrderId;
    private final String bankId;
    private final String currency;

    private final String withdrawalId;
    private final String depositId;

    static long numberOfPaymentOrderRequests;

    static long studentPackagePaymentOrderLimit;
    protected long studentRequestsOrOccurences;
    protected long studentPackagePaymentFee;

    protected long incrementRequestOrOccurence;

    static long normalPackagePaymentOrderLimit;
    protected long normalRequestsOrOccurences;
    protected long normalPackagePaymentFee;

    protected boolean externalAccountInstruction;

    Date date = new Date(System.currentTimeMillis());

    private Account(final ActorContext<Command> context, final String accountId, final String externalAccountId,
            final Double accountBalance, final Double amount, final String mainCommand, final String userPackage,
            final long paymentOrderId, final String bankId, final String currency, final String withdrawalId,
            final String depositId) {

        super(context);

        this.accountId = accountId;
        Account.externalAccountId = externalAccountId;
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

        context.getLog().info(String.format("Account actor is created with :: Account Id - %s, Bank Id - %s, Balance - %.2f, Currency - %s, Process Name - %s, Requested amount - %.2f, User Package - %s\n", 
        accountId, bankId, this.accountBalance, currency, mainCommand, amount, userPackage));

    }    
    
}

