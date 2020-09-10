package com.example;

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
        protected ActorRef<InstructInternalAccount> instructInternalAccount;
        protected ActorRef<CreditExternalAccount> creditExternalAccount;

        public IdentifyRouteToExternalAccount(final String internalAccountId, final long paymentOrderId,
                    final String externalAccountId, final Double amount, final String bankId) {

                        this.internalAccountId = identify.instruct.check.paymentOrder.accountId;
                        this.paymentOrderId = identify.instruct.check.paymentOrder.paymentOrderId;
                        this.externalAccountId = identify.externalAccountId;
                        this.amount = identify.amount;
                        this.bankId = identify.bankId;

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
                        boolean externalAccountInstructed, boolean externalAccountCredited,
                        boolean paymentNetworkConnected) {

                            return Behaviors.setup(context -> new Payment(context, accountId, bankId, externalAccountInstructed,
                            externalAccountCredited, paymentNetworkConnected));

    }

    private final String accountId;
    private final String bankId;
    private final boolean externalAccountInstructed;
    private final boolean externalAccountCredited;
    private final boolean paymentNetworkConnected;


    private Payment(final ActorContext<Account.Command> context, final String accountId, final String bankId, 
                        final boolean externalAccountInstructed, final boolean externalAccountCredited,
                        final boolean paymentNetworkConnected) {

                            super(context);
                            this.accountId = accountId;
                            this.bankId = bankId;
                            this.externalAccountInstructed = externalAccountInstructed;
                            this.externalAccountCredited = externalAccountCredited;
                            this.paymentNetworkConnected = paymentNetworkConnected;

                            context.getLog().info(" Payment actor is created with :: Account Id - %s, Bank Id - %s ", accountId, bankId);

                            context.getLog().info(" Payment Network is connected - STATUS: %b ", paymentNetworkConnected);

                            context.getLog().info(" Account Domain instructed Payment Domain. External Account Instructed - STATUS: %b ",
                            externalAccountInstructed);


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

                        return this;

    }

    private Behavior<Account.Command> onInstructInternalAccount(final InstructInternalAccount instructIntAccount) {

                        getContext().getLog().info(" External Account Instruction is SUCCESSFULL! - STATUS: %b ", externalAccountInstructed);

                        return this;

    }

    private Behavior<Account.Command> onCreditExternalAccount(final CreditExternalAccount creditIntAccount) {

                        return this;

    }    

    private Behavior<Account.Command> onPostStop() {

                        getContext().getLog().info("Payment actor is stopped with :: Account Id - %s, Bank Id - %s ", accountId, bankId);

                        getContext().getLog().info(" Payment Network is disconnected - STATUS: %b ", paymentNetworkConnected);

                        getContext().getLog().info(" Crediting External Account failed - STATUS: %b ", externalAccountCredited);
                        
                        return Behaviors.stopped();

    }

}