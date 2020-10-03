package com.example;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import com.example.Account.*;
import com.example.Billing.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class gEndOfMonthBillTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    private ActorRef<Command> billingActor = testKit
    .spawn(Billing.create(cBillingTest.clientName, aAccountTest.accountId, aAccountTest.externalAcountId, aAccountTest.bankId,
            aAccountTest.currency, aAccountTest.amount, aAccountTest.accountBalance, aAccountTest.userPackage,
            aAccountTest.paymentOrderId, aAccountTest.withdrawalId, aAccountTest.depositId, aAccountTest.mainCommand));

    @Test
    public void oCalculateEndOfMonthBill() {

        Calendar cal = Calendar.getInstance();

        Date billingDate = new Date();

        LocalDate localDate = billingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        int currentDay = localDate.getDayOfMonth();

        int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);

        if (lastDayOfMonth == currentDay) {

            TestProbe<EndOfMonthBillCalculated> probe = testKit.createTestProbe(EndOfMonthBillCalculated.class);

            billingActor.tell(new CalculateEndOfMonthBill(aAccountTest.accountId, aAccountTest.amount, aAccountTest.currency,
            aAccountTest.paymentOrderId, aAccountTest.accountBalance, aAccountTest.withdrawalId, aAccountTest.depositId,
            aAccountTest.date, aAccountTest.userPackage, "END OF MONTH BILL IS CALCULATED!", probe.getRef()));

            EndOfMonthBillCalculated bill = probe.receiveMessage();

            assertEquals("END OF MONTH BILL IS CALCULATED!", bill.replyTo);

        } else {

            TestProbe<EndOfMonthBillCalculated> probe = testKit.createTestProbe(EndOfMonthBillCalculated.class);

            billingActor.tell(new CalculateEndOfMonthBill(aAccountTest.accountId, aAccountTest.amount, aAccountTest.currency,
            aAccountTest.paymentOrderId, aAccountTest.accountBalance, aAccountTest.withdrawalId, aAccountTest.depositId,
            aAccountTest.date, aAccountTest.userPackage, "VERIFIED!", probe.getRef()));

            EndOfMonthBillCalculated bill = probe.receiveMessage();

            assertEquals("VERIFIED!", bill.replyTo);

        }


    }

}