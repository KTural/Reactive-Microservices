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
import com.example.Billing.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class eWithdrawalTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    private ActorRef<Command> accountActor = testKit.spawn(Account.create(aAccountTest.accountId, aAccountTest.externalAcountId, 
    aAccountTest.accountBalance, aAccountTest.amount, aAccountTest.mainCommand, aAccountTest.userPackage, aAccountTest.paymentOrderId,
    aAccountTest.bankId, aAccountTest.currency, aAccountTest.withdrawalId, aAccountTest.depositId));

    private ActorRef<Command> billingActor = testKit.spawn(Billing.create(cBillingTest.clientName, aAccountTest.accountId, aAccountTest.externalAcountId, aAccountTest.bankId,
    aAccountTest.currency, aAccountTest.amount, aAccountTest.accountBalance, aAccountTest.userPackage,
    aAccountTest.paymentOrderId, aAccountTest.withdrawalId, aAccountTest.depositId, aAccountTest.mainCommand));

    @Test 
    public void kTestSubmitWithdrawal() {

        TestProbe<WithdrawalVerified> probe = testKit.createTestProbe(WithdrawalVerified.class);
        TestProbe<WithdrawalRejected> probe_two = testKit.createTestProbe(WithdrawalRejected.class);

        if (aAccountTest.amount < aAccountTest.accountBalance) {

            accountActor.tell(new SubmitWithdrawalOrder(aAccountTest.accountBalance,aAccountTest.amount, aAccountTest.paymentOrderId,
            aAccountTest.bankId, aAccountTest.date, aAccountTest.accountId,
            "VERIFIED!", probe.getRef(), probe_two.getRef()));

            WithdrawalVerified withdrawalVerify = probe.receiveMessage();

            assertEquals("VERIFIED!", withdrawalVerify.replyTo);

        } else {

            accountActor.tell(new SubmitWithdrawalOrder(aAccountTest.accountBalance,aAccountTest.amount, aAccountTest.paymentOrderId,
            aAccountTest.bankId, aAccountTest.date, aAccountTest.accountId,
            "REJECTED!", probe.getRef(), probe_two.getRef()));

            WithdrawalRejected withdrawalReject = probe_two.receiveMessage();

            assertEquals("REJECTED!", withdrawalReject.replyTo);

        }

    }

    @Test
    public void lTestDebitWithdrawnAccount() {

        TestProbe<WithdrawalCompleted> probe = testKit.createTestProbe(WithdrawalCompleted.class);

        if (aAccountTest.amount < aAccountTest.accountBalance) {

            billingActor.tell(new DebitWithdrawnAccount(aAccountTest.withdrawalId, aAccountTest.accountBalance,
            aAccountTest.userPackage, aAccountTest.accountId, aAccountTest.date, aAccountTest.amount, aAccountTest.currency,
            "WITHDRAWN!", probe.getRef()));

            WithdrawalCompleted withdrawal = probe.receiveMessage();

            assertEquals("WITHDRAWN!", withdrawal.replyTo);

        } else {

            billingActor.tell(new DebitWithdrawnAccount(aAccountTest.withdrawalId, aAccountTest.accountBalance,
            aAccountTest.userPackage, aAccountTest.accountId, aAccountTest.date, aAccountTest.amount, aAccountTest.currency,
            "FAILED!", probe.getRef()));

            WithdrawalCompleted withdrawal = probe.receiveMessage();

            assertEquals("FAILED!", withdrawal.replyTo);            

        }

    }

}