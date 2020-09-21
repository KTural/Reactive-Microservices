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

    private static String accountId = "ACCOUNT ID: 230910/0609";
    private static Double accountBalance = 120950.4990;
    private static Double amount = 91500.5090;
    private static String mainCommand = "Payment";
    private static String userPackage = "Student";
    private static long paymentOrderId = 506809102;
    private static String bankId = "BANK ID: 1209309204930125CZ";
    private static String currency = "CZK";
    private static String withdrawalId = "WITHDRAWAL ID: 89298420";
    private static String depositId = "DEPOSIT ID: 04932409";

    ActorRef<Command> accountActor = testKit.spawn(Account.create(accountId, 
    accountBalance, amount, mainCommand, userPackage, paymentOrderId,
    bankId, currency, withdrawalId, depositId));

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
        accountActor.tell(new SubmitPaymentOrder(paymentOrderId, date, amount, accountId,
        bankId, "SUBMIT", probe.getRef()));
        PaymentOrderStatus paymentStatus = probe.receiveMessage();

        assertEquals("SUBMIT", paymentStatus.status);

        assertNotEquals("FAIL", paymentStatus.status);

    }

    @Test
    public void cTestCheckAccountBalance() {

        TestProbe<PaymentOrderVerified> probe = testKit.createTestProbe(PaymentOrderVerified.class);
        TestProbe<PaymentOrderRejected> probe_two = testKit.createTestProbe(PaymentOrderRejected.class);

        if (amount < accountBalance) {

        accountActor.tell(new CheckAccountBalance("VERIFIED!", probe.getRef(), probe_two.getRef()));

        PaymentOrderVerified accountBalanceVerified = probe.receiveMessage();
        
        assertEquals("VERIFIED!", accountBalanceVerified.orderStatus);

        } else {
        
        accountActor.tell(new CheckAccountBalance("REJECTED!", probe.getRef(), probe_two.getRef()));

        PaymentOrderRejected accountBalanceRejected = probe_two.receiveMessage();

        assertEquals("REJECTED!", accountBalanceRejected.orderStatus);

        }

    }

    @Test
    public void dTestDebitCurrentAccount() {

        TestProbe<AccountDebited> probe = testKit.createTestProbe(AccountDebited.class);

        accountActor.tell(new DebitCurrentAccount(paymentOrderId, accountBalance, userPackage, "ACCOUNT IS DEBITED!",
        probe.getRef()));

        AccountDebited debitedAccount = probe.receiveMessage();

        assertEquals("ACCOUNT IS DEBITED!", debitedAccount.message);

    }
    

}