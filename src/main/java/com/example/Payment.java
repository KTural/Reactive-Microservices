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
        protected ActorRef<Account.InternalAccountInstructed> instructInternalAccount;

        public InstructInternalAccount(final Double amount, final long paymentOrderId, final String internalAccountId) {

                        this.amount = instructAccount.amount;
                        this.paymentOrderId = instructAccount.paymentOrderId;
                        this.internalAccountId = instructAccount.internalAccountId;

        }

    }

    public static final class CreditExternalAccount implements Account.Command {

        final boolean paymentNetworkRequest;
        final Double amount;
        final long paymentOrderId;
        final String internalAccountId;
        final String externalAccountId;
        protected IdentifyRouteToExternalAccount creditAccount;
        protected ActorRef<ExternalAccountCredited> creditExternalAccount;

        public CreditExternalAccount(final boolean paymentNetworkRequest, final Double amount, final long paymentOrderId, 
                        final String internalAccountId, final String externalAccountId) {

                            this.paymentNetworkRequest = paymentNetworkRequest;
                            this.amount = creditAccount.amount;
                            this.paymentOrderId = creditAccount.paymentOrderId;
                            this.internalAccountId = creditAccount.internalAccountId;
                            this.externalAccountId = creditAccount.externalAccountId;

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

    public static Behavior<Account.Command> create(String accountId, String bankId,
                        boolean internalAccountInstructed, boolean externalAccountCredited,
                        boolean paymentNetworkConnected) {

                            return Behaviors.setup(context -> new Payment(context, accountId, bankId,
                            internalAccountInstructed,
                            externalAccountCredited, paymentNetworkConnected));

    }

    private final String accountId;
    private final String bankId;
    private final boolean internalAccountInstructed;
    private final boolean externalAccountCredited;
    private final boolean paymentNetworkConnected;
    protected boolean paymentNetworkRequest;


    private Payment(final ActorContext<Account.Command> context, final String accountId, final String bankId, 
                        final boolean internalAccountInstructed, final boolean externalAccountCredited,
                        final boolean paymentNetworkConnected) {

                            super(context);
                            this.accountId = accountId;
                            this.bankId = bankId;
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

                        instructIntAccount.instructInternalAccount.tell(new InternalAccountInstructed(instructIntAccount.amount,
                        instructIntAccount.internalAccountId, instructIntAccount.instructAccount.externalAccountId,
                        instructIntAccount.instructAccount.bankId, instructIntAccount.paymentOrderId, "VERIFIED!"));

                        return this;

    }

    private Behavior<Account.Command> onCreditExternalAccount(final CreditExternalAccount creditExtAccount) {

                        getContext().getLog().info(" External Account is Credited SUCCESSFULLY! - STATUS: {} \n", externalAccountCredited);

                        creditExtAccount.creditExternalAccount.tell(new ExternalAccountCredited(creditExtAccount.amount,
                        creditExtAccount.internalAccountId, creditExtAccount.creditAccount.externalAccountId, 
                        creditExtAccount.creditAccount.bankId, creditExtAccount.paymentOrderId, "VERIFIED!"));

                        this.getContext().getSelf().tell(new CompletePaymentOrder(creditExtAccount.paymentOrderId, 
                        creditExtAccount.creditAccount.bankId, creditExtAccount.externalAccountId, 
                        creditExtAccount.creditAccount.internalAccountId, creditExtAccount.amount,
                        creditExtAccount.creditAccount.identify.instruct.check.paymentOrder.dateTime,
                        creditExtAccount.creditAccount.identify.balance));

                        return this;

    }    

    private Behavior<Account.Command> onPostStop() {

                        getContext().getLog().info(String.format(" Payment actor is stopped with :: Account Id - %s, Bank Id - %s\n", accountId, bankId));

                        getContext().getLog().info(" Payment Network is disconnected - STATUS: {} \n", paymentNetworkConnected);

                        getContext().getLog().info(" Crediting External Account is stopped - STATUS: {} \n", externalAccountCredited);
                        
                        return Behaviors.stopped();

    }

}