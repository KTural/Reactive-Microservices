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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class eWithdrawalTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    private ActorRef<Command> accountActor = testKit.spawn(Account.create(aAccountTest.accountId, aAccountTest.externalAcountId, 
    aAccountTest.accountBalance, aAccountTest.amount, aAccountTest.mainCommand, aAccountTest.userPackage, aAccountTest.paymentOrderId,
    aAccountTest.bankId, aAccountTest.currency, aAccountTest.withdrawalId, aAccountTest.depositId));

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

}