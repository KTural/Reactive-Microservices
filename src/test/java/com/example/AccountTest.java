package com.example;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.example.Account.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountTest {

    ActorRef<Command> accountActor = testKit.spawn(Account.create("ACCOUNT ID: 230910/0609",
    20980.90, 1500.50, "Payment", "Student", 506809102,
    "BANK ID: 1209309204930125CZ", "CZK", "WITHDRAWAL ID: 89298420", "DEPOSIT ID: 04932409"));

    Date date = new Date(System.currentTimeMillis());

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void  aTestCheckSubmissionCommand() {

        TestProbe<SubmissionCommandsChecked> probe = testKit.createTestProbe(SubmissionCommandsChecked.class);
        accountActor.tell(new CheckSubmissionCommand("CHECK SUBMISSION!", probe.getRef()));
        SubmissionCommandsChecked submissionCheck = probe.receiveMessage();

        assertEquals("CHECK SUBMISSION!", submissionCheck.status);

        assertNotEquals("FAILED SUBMISSION", submissionCheck.status);

    }

    @Test
    public void bTestSubmitPaymentOrder() {

        TestProbe<PaymentOrderStatus> probe = testKit.createTestProbe(PaymentOrderStatus.class); 
        accountActor.tell(new SubmitPaymentOrder(506809102, date, 1500.50, "ACCOUNT ID: 230910/0609",
        "BANK ID: 1209309204930125CZ", "SUBMIT", probe.getRef()));
        PaymentOrderStatus paymentStatus = probe.receiveMessage();

        assertEquals("SUBMIT", paymentStatus.status);

        assertNotEquals("FAIL", paymentStatus.status);

    }
    

}