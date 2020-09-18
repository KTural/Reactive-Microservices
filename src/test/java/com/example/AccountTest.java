package com.example;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.ClassRule;
import org.junit.Test;
import java.util.Date;

import static org.junit.Assert.assertEquals;

import com.example.Account.*;

public class AccountTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void testSubmittingPaymentOrder() {

        Date date = new Date(System.currentTimeMillis());

        TestProbe<Account.PaymentOrderStatus> probe = testKit.createTestProbe(Account.PaymentOrderStatus.class);
        ActorRef<Account.Command> accountActor = testKit.spawn(Account.create("ACCOUNT ID: 230910/0609",
        20980.90, 1500.50, "Payment", "Student", 506809102,
        "BANK ID: 1209309204930125CZ", "CZK", "WITHDRAWAL ID: 89298420", "DEPOSIT ID: 04932409")); 
        accountActor.tell(new SubmitPaymentOrder(506809102, date, 1500.50, "ACCOUNT ID: 230910/0609",
        "BANK ID: 1209309204930125CZ", "SUBMIT", probe.getRef()));
        PaymentOrderStatus paymentStatus = probe.receiveMessage();
        assertEquals("SUBMIT", paymentStatus.status);

    }
}