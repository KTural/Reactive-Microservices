package com.example;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

import com.example.Account.*;
import com.example.Payment.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentTest {

   // For Payment domain
   private final boolean internalAccountInstructed = true;
   private final boolean externalAccountCredited = true;
   private final boolean paymentNetworkRequest = true;
   private final boolean paymentNetworkConnected = true;    

   private ActorRef<Command> paymentActor = testKit.spawn(Payment.create(AccountTest.accountId, AccountTest.bankId,
   AccountTest.amount, AccountTest.paymentOrderId, internalAccountInstructed, externalAccountCredited, paymentNetworkConnected));

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void fTestIdentifyRouteToExternalAccount() {

        TestProbe<RouteIdentified> probe = testKit.createTestProbe(RouteIdentified.class);

        paymentActor.tell(new IdentifyRouteToExternalAccount(AccountTest.accountId,
        AccountTest.paymentOrderId, AccountTest.externalAcountId, AccountTest.amount, AccountTest.bankId, 
        "EXTERNAL ACCOUNT IS IN THE SYSTEM!", probe.getRef()));

        RouteIdentified route = probe.receiveMessage();

        assertEquals("EXTERNAL ACCOUNT IS IN THE SYSTEM!", route.replyTo);

    }

    @Test
    public void gTestInstructInternalAccount() {

        TestProbe<InternalAccountInstructed> probe = testKit.createTestProbe(InternalAccountInstructed.class);

        paymentActor.tell(new InstructInternalAccount(AccountTest.amount, AccountTest.paymentOrderId, AccountTest.accountId,
        "INTERNAL ACCOUNT IS INSTRUCTED!", probe.getRef()));

        InternalAccountInstructed internal = probe.receiveMessage();

        assertEquals("INTERNAL ACCOUNT IS INSTRUCTED!", internal.replyTo);        

    }

    @Test
    public void hTestCreditExternalAccount() {

        TestProbe<ExternalAccountCredited> probe = testKit.createTestProbe(ExternalAccountCredited.class);

        paymentActor.tell(new CreditExternalAccount(paymentNetworkRequest, AccountTest.amount, AccountTest.paymentOrderId, AccountTest.accountId,
        AccountTest.externalAcountId, "EXTERNAL ACCOUNT IS CREDITED!", probe.getRef()));

        ExternalAccountCredited external = probe.receiveMessage();

        assertEquals("EXTERNAL ACCOUNT IS CREDITED!", external.replyTo); 
        
        assertEquals(AccountTest.externalAcountId, external.externalAccountId);

    }
}