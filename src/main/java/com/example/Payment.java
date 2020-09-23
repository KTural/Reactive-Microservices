package com.example;

import com.example.Account.*;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Payment extends AbstractBehavior<Account.Command> {

    public static final class IdentifyRouteToExternalAccount implements Account.Command {

        final String internalAccountId;
        final long paymentOrderId;
        final String externalAccountId;
        final Double amount;
        final String bankId;
        protected Account.InstructExternalAccount identify;
        final String message;
        final ActorRef<RouteIdentified> routeIdentify;

        public IdentifyRouteToExternalAccount(final String internalAccountId, final long paymentOrderId,
                    final String externalAccountId, final Double amount, final String bankId,
                    final String message, final ActorRef<RouteIdentified> routeIdentify) {

                        this.internalAccountId = internalAccountId;
                        this.paymentOrderId = paymentOrderId;
                        this.externalAccountId = externalAccountId;
                        this.amount = amount;
                        this.bankId = bankId;
                        this.message = message;
                        this.routeIdentify = routeIdentify;

        }
    }

    public static final class RouteIdentified {

        final String replyTo;

        public RouteIdentified(final String replyTo) {

                        this.replyTo = replyTo;

        }

    }

    public static final class InstructInternalAccount implements Account.Command {

        final Double amount;
        final long paymentOrderId;
        final String internalAccountId;
        protected IdentifyRouteToExternalAccount instructAccount;
        final String message;
        final ActorRef<Account.InternalAccountInstructed> instructInternalAccount;

        public InstructInternalAccount(final Double amount, final long paymentOrderId, final String internalAccountId,
                        final String message, final ActorRef<Account.InternalAccountInstructed> instructInternalAccount) {

                        this.amount = amount;
                        this.paymentOrderId = paymentOrderId;
                        this.internalAccountId = internalAccountId;
                        this.message = message;
                        this.instructInternalAccount = instructInternalAccount;

        }

    }

    public static final class CreditExternalAccount implements Account.Command {

        final boolean paymentNetworkRequest;
        final Double amount;
        final long paymentOrderId;
        final String internalAccountId;
        final String externalAccountId;
        final String message;
        protected IdentifyRouteToExternalAccount creditAccount;
        final ActorRef<ExternalAccountCredited> creditExternalAccount;

        public CreditExternalAccount(final boolean paymentNetworkRequest, final Double amount, final long paymentOrderId, 
                        final String internalAccountId, final String externalAccountId, final String message,
                        final ActorRef<ExternalAccountCredited> creditExternalAccount) {

                            this.paymentNetworkRequest = paymentNetworkRequest;
                            this.amount = amount;
                            this.paymentOrderId = paymentOrderId;
                            this.internalAccountId = internalAccountId;
                            this.externalAccountId = externalAccountId;
                            this.message = message;
                            this.creditExternalAccount = creditExternalAccount;

        }
        
    }


    public static final class ExternalAccountCredited {

        final Double amount;
        final String internalAccountId;
        final String externalAccountId;
        final String bankId;
        final long paymentOrderId;
        final String replyTo;
        protected CreditExternalAccount externalAccount;

        public ExternalAccountCredited(final Double amount, final String internalAccountId, final String externalAccountId, 
                        final String bankId, final long paymentOrderId, final String replyTo) {

                            this.amount = amount;
                            this.internalAccountId = internalAccountId;
                            this.externalAccountId = externalAccountId;
                            this.bankId = bankId;
                            this.paymentOrderId = paymentOrderId;
                            this.replyTo = replyTo;

        }
    }

    static enum Passivate implements Account.Command {

        INSTANCE

    }

    public static Behavior<Account.Command> create(String accountId, String bankId, Double amount, long paymentOrderId,
                        boolean internalAccountInstructed, boolean externalAccountCredited,
                        boolean paymentNetworkConnected) {

                            return Behaviors.setup(context -> new Payment(context, accountId, bankId, amount,
                            paymentOrderId, internalAccountInstructed,
                            externalAccountCredited, paymentNetworkConnected));

    }

    private final String accountId;
    private final String bankId;
    private final Double amount;
    private final long paymentOrderId;
    private final boolean internalAccountInstructed;
    private final boolean externalAccountCredited;
    private final boolean paymentNetworkConnected;
    protected boolean paymentNetworkRequest;


    private Payment(final ActorContext<Account.Command> context, final String accountId, final String bankId, final Double amount,
                        final long paymentOrderId, final boolean internalAccountInstructed, final boolean externalAccountCredited,
                        final boolean paymentNetworkConnected) {

                            super(context);
                            this.accountId = accountId;
                            this.bankId = bankId;
                            this.amount = amount;
                            this.paymentOrderId = paymentOrderId;
                            this.internalAccountInstructed = internalAccountInstructed;
                            this.externalAccountCredited = externalAccountCredited;
                            this.paymentNetworkConnected = paymentNetworkConnected;

                            this.paymentNetworkRequest = false;

                            context.getLog().info(" Payment actor is created with :: Account Id - {}, Bank Id - {} \n", accountId, bankId);

                            context.getLog().info(" Payment Network is connected - STATUS: {} \n", paymentNetworkConnected);

                            context.getLog().info(" Account Domain instructed Payment Domain. External Account Instructed - STATUS: {} \n",
                            internalAccountInstructed);


    }

    @Override
    public Receive<Account.Command> createReceive() {

        return newReceiveBuilder().onMessage(IdentifyRouteToExternalAccount.class, this::onIdentifyRouteToExternalAccount)
                        .onMessage(InstructInternalAccount.class, this::onInstructInternalAccount)
                        .onMessage(CreditExternalAccount.class, this::onCreditExternalAccount)
                        .onMessage(Passivate.class, m -> Behaviors.stopped()).onSignal(PostStop.class, signal -> onPostStop())
                        .build();

    }

    private Behavior<Account.Command> onIdentifyRouteToExternalAccount(final IdentifyRouteToExternalAccount identifyRoute) {

                        getContext().getLog().info(" External Account is within Bank's system with Id - {} \n", Account.externalAccountId);
                        
                        identifyRoute.routeIdentify.tell(new RouteIdentified("EXTERNAL ACCOUNT IS IN THE SYSTEM!"));

                        System.out.println("\n\n\n IDENTIFYING ROUTE TO EXTERNAL ACCOUNT ... \n\n\n");

                        getContext().getLog().info("EXTERNAL ACCOUNT IS IN THE SYSTEM! \n");

                        return this;

    }

    private Behavior<Account.Command> onInstructInternalAccount(final InstructInternalAccount instructIntAccount) {

                        getContext().getLog().info(" Internal Account Instruction is SUCCESSFULL! - STATUS: {} \n", internalAccountInstructed);

                        instructIntAccount.instructInternalAccount.tell(new InternalAccountInstructed(this.amount,
                        this.accountId, Account.externalAccountId,
                        this.bankId, this.paymentOrderId, "INTERNAL ACCOUNT IS INSTRUCTED!"));

                        System.out.println("\n\n\n INSTRUCTING INTERNAL ACCOUNT ... \n\n\n");

                        return this;

    }

    private Behavior<Account.Command> onCreditExternalAccount(final CreditExternalAccount creditExtAccount) {

                        getContext().getLog().info(" External Account is Credited SUCCESSFULLY! - STATUS: {} \n", externalAccountCredited);

                        creditExtAccount.creditExternalAccount.tell(new ExternalAccountCredited(this.amount,
                        this.accountId, Account.externalAccountId, 
                        this.bankId, this.paymentOrderId, "EXTERNAL ACCOUNT IS CREDITED!"));

                        System.out.println("\n\n\n CREDITING EXTERNAL ACCOUNT ... \n\n\n");

                        return this;

    }    

    private Behavior<Account.Command> onPostStop() {

                        getContext().getLog().info(String.format(" Payment actor is stopped with :: Account Id - %s, Bank Id - %s\n", accountId, bankId));

                        getContext().getLog().info(" Payment Network is disconnected - STATUS: {} \n", paymentNetworkConnected);

                        getContext().getLog().info(" Crediting External Account is stopped - STATUS: {} \n", externalAccountCredited);
                        
                        return Behaviors.stopped();

    }

}