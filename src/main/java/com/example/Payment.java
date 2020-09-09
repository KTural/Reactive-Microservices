package com.example;

import java.util.Optional;

import com.example.Account.*;

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
    
}