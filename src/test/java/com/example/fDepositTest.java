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
public class fDepositTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    private ActorRef<Command> accountActor = testKit.spawn(Account.create(aAccountTest.accountId, aAccountTest.externalAcountId, 
    aAccountTest.accountBalance, aAccountTest.amount, aAccountTest.mainCommand, aAccountTest.userPackage, aAccountTest.paymentOrderId,
    aAccountTest.bankId, aAccountTest.currency, aAccountTest.withdrawalId, aAccountTest.depositId));

    @Test 
    public void mTestSubmitDepositOrder() {

        TestProbe<DepositVerified> probe = testKit.createTestProbe(DepositVerified.class);

        accountActor.tell(new SubmitDepositOrder(aAccountTest.accountBalance,aAccountTest.amount, aAccountTest.paymentOrderId,
        aAccountTest.bankId, aAccountTest.date, aAccountTest.accountId,
        "VERIFIED!", probe.getRef()));

        DepositVerified deposit = probe.receiveMessage();

        assertEquals("VERIFIED!", deposit.replyTo);

    }

}