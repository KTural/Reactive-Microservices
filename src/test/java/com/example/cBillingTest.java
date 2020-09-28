package com.example;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

import com.example.Account.*;
import com.example.Billing.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class cBillingTest {
    static String clientName = "USER1";

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    private ActorRef<Command> billingActor = testKit
            .spawn(Billing.create(clientName, aAccountTest.accountId, aAccountTest.externalAcountId, aAccountTest.bankId,
                    aAccountTest.currency, aAccountTest.amount, aAccountTest.accountBalance, aAccountTest.userPackage,
                    aAccountTest.paymentOrderId, aAccountTest.withdrawalId, aAccountTest.depositId, aAccountTest.mainCommand));

    @Test
    public void jCalculatePaymentOrderFee() {

        TestProbe<PaymentOrderFeeCalculated> probe = testKit.createTestProbe(PaymentOrderFeeCalculated.class);

        billingActor.tell(new CalculatePaymentOrderFee(aAccountTest.accountId, aAccountTest.amount,
        aAccountTest.currency, aAccountTest.accountBalance, "P.O FEE IS CALCULATED!",
        probe.getRef()));

        PaymentOrderFeeCalculated order = probe.receiveMessage();

        assertEquals("P.O FEE IS CALCULATED!", order.replyTo);            

    }
}