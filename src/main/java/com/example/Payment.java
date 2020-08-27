package com.example;

import java.util.Optional;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Payment extends AbstractBehavior<Account.Command> {

    public static final class IdentifyRouteToOppositeAccount implements Account.Command {

        final long internalAccountId;
        final long externalAccountId;
        final boolean insideBankSystem;
        final long amount;
        final ActorRef<CreditInternalAccount> instructInternalAccount;
        final ActorRef<CreditExternalAccount> instructExternalAccount;

        public IdentifyRouteToOppositeAccount(final long internalAccountId, final long externalAccountId,
                    final boolean insideBankSystem, final long amount, 
                    final ActorRef<CreditInternalAccount> instructInternalAccount, 
                    final ActorRef<CreditExternalAccount> instructExternalAccount) {

                        this.internalAccountId = internalAccountId;
                        this.externalAccountId = externalAccountId;
                        this.insideBankSystem = insideBankSystem;
                        this.amount = amount;
                        this.instructInternalAccount = instructInternalAccount;
                        this.instructExternalAccount = instructExternalAccount;

                    }
    }

    public static final class CreditInternalAccount implements Account.Command {



    }

    public static final class CreditExternalAccount implements Account.Command {


        
    }
}