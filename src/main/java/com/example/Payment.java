package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Payment extends AbstractBehavior<Account.Command> {

    public static final class IdentifyRouteToOppositeAccount implements Account.Command {

        final String internalAccountId;
        final long paymentOrderId;
        final String oppositeAccountId;
        final Double amount;
        final String bankId;
        protected Account.InstructOppositeAccount identify;
        protected ActorRef<CreditInternalAccount> instructInternalAccount;
        protected ActorRef<CreditExternalAccount> instructExternalAccount;

        public IdentifyRouteToOppositeAccount(final String internalAccountId, final long paymentOrderId,
                    final String oppositeAccountId, final Double amount, final String bankId) {

                        this.internalAccountId = internalAccountId;
                        this.paymentOrderId = paymentOrderId;
                        this.oppositeAccountId = oppositeAccountId;
                        this.amount = amount;
                        this.bankId = bankId;

                    }
    }

    public static final class CreditInternalAccount implements Account.Command {



    }

    public static final class CreditExternalAccount implements Account.Command {


        
    }


    public static final class OppositeAccountCredited {



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

        return newReceiveBuilder().onMessage(CreditInternalAccount.class, this::onCreditInternalAccount)
                        .onMessage(CreditExternalAccount.class, this::onCreditExternalAccount)
                        .onMessage(Passivate.class, m -> Behaviors.stopped()).onSignal(PostStop.class, signal -> onPostStop())
                        .build();

    }

    private Behavior<Account.Command> onCreditInternalAccount(final CreditInternalAccount creditIntAccount) {

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