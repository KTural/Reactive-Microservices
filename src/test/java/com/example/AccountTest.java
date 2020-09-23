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

    static String accountId = "ACCOUNT ID: 230910/0609";
    static String externalAcountId = "EXTERNAL ACCOUNT ID: 456690/3489";
    static Double accountBalance = 120950.4990;
    static Double amount = 91500.5090;
    private static String mainCommand = "Payment";
    private static String userPackage = "Student";
    static long paymentOrderId = 506809102;
    static String bankId = "BANK ID: 1209309204930125CZ";
    static String currency = "CZK";
    private static String withdrawalId = "WITHDRAWAL ID: 89298420";
    private static String depositId = "DEPOSIT ID: 04932409";
    private static boolean accountInstruction = true;

    static Date date = new Date(System.currentTimeMillis());

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    static ActorRef<Command> accountActor = testKit.spawn(Account.create(accountId, externalAcountId,
    accountBalance, amount, mainCommand, userPackage, paymentOrderId,
    bankId, currency, withdrawalId, depositId));    

    @Test
    public void aTestCheckSubmissionCommand() {

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

    @Test 
    public void eTestInstructExternalAccount() {
    
        TestProbe<ExternalAccountInstructed> probe = testKit.createTestProbe(ExternalAccountInstructed.class);

        accountActor.tell(new InstructExternalAccount(accountInstruction, accountBalance, externalAcountId,
        amount, "EXTERNAL ACCOUNT IS INSTRUCTED", probe.getRef()));

        ExternalAccountInstructed externalAcc = probe.receiveMessage();

        assertEquals("EXTERNAL ACCOUNT IS INSTRUCTED", externalAcc.replyTo);

    }

    @Test
    public void iTestCompletePaymentOrder() {

        TestProbe<PaymentOrderProcessed> probe = testKit.createTestProbe(PaymentOrderProcessed.class);

        accountActor.tell(new Account.CompletePaymentOrder(paymentOrderId, bankId, externalAcountId, accountId, 
        amount, date, accountBalance,
        "VERIFIED COMPLETION!", probe.getRef()));

        PaymentOrderProcessed process = probe.receiveMessage();

        assertEquals("VERIFIED COMPLETION!", process.replyTo);        

    }    

}