package io.github.admiral.hr;

import io.github.admiral.hr.standalone.SimpleHR;
import io.github.admiral.soldier.SoldierInfo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SimpleHRTest {

    SimpleHR simpleHR = new SimpleHR(null);
    @Test
    void register() {
        String host = "0.0.0.0";
        int port = 8080;
        SoldierFile fileA = new SimpleFile("SA", new String[0], host, port);
        SoldierFile fileB = new SimpleFile("SA", new String[]{"SE"}, host, port);
        SoldierFile fileC = new SimpleFile("SC", new String[]{"SA"}, host, port);
        SoldierFile fileD = new SimpleFile("SC", new String[]{"SE"}, host, port);
        simpleHR.register(fileA);
        assertThrows(InfoConflictException.class, () -> simpleHR.register(fileB));
        simpleHR.register(fileC);
        assertThrows(InfoConflictException.class, () -> simpleHR.register(fileD));

        SoldierInfo infoA = simpleHR.getInfo("SA");
        assertEquals(infoA.getName(), "SA");
        assertTrue(Arrays.equals(infoA.getSubscribes(), fileA.getSubscribes()));

        SoldierInfo infoB = simpleHR.getInfo("SC");
        assertTrue(Arrays.equals(infoB.getSubscribes(), fileC.getSubscribes()));

        // register same service name with same host and same name will not be registered.
        SoldierFile fileE = new SimpleFile("SA", new String[0], host, port);
        simpleHR.register(fileE);
        assertEquals(1, simpleHR.getFiles("SA").size());

        // register not same service with same host and same name will be registered.
        SoldierFile fileF = new SimpleFile("SA", new String[0],"1.1.1.1", port);
        simpleHR.register(fileF);
        assertEquals(2, simpleHR.getFiles("SA").size());
    }
}