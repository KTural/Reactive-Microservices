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
import com.example.BankManager.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class dBankManagerTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    private ActorRef<Command> bankManagerActor = testKit
    .spawn(BankManager.create());

    @Test
    public void TestInstructingBankManager() {

        TestProbe<BankManagerInstructed> probe = testKit.createTestProbe(BankManagerInstructed.class);

        bankManagerActor.tell(new InstructBankManager("VERIFIED!", probe.getRef()));

        BankManagerInstructed bankManager = probe.receiveMessage();

        assertEquals("VERIFIED!", bankManager.replyTo);

    }

}