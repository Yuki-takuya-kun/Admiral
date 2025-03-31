package io.github.admiral;

import io.github.admiral.service.HumanResource;
import io.github.admiral.service.SimpleFile;
import io.github.admiral.service.SimpleHR;
import io.github.admiral.service.SoldierFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdmiralTest {

    @MockitoBean
    HumanResource serviceCenter = new SimpleHR();

    @Autowired
    Admiral admiral;

    @Test
    void tryInsertRemoveService() {
        String ip = "_";
        int port = 10;
        SoldierFile serviceA = new SimpleFile("SA", ip, port, new String[0], "EA");
        SoldierFile serviceB = new SimpleFile("SB", ip, port, new String[]{"EA"}, "EB");
        SoldierFile serviceC = new SimpleFile("SC", ip, port, new String[]{"EA"}, "EC");
        SoldierFile serviceD = new SimpleFile("SD", ip, port, new String[]{"EB"}, "ED");
        SoldierFile serviceE = new SimpleFile("SE", ip, port, new String[]{"ED"}, "EC");
        SoldierFile serviceF = new SimpleFile("SF", ip, port, new String[]{"EB", "EC"}, "EF");
        SoldierFile serviceLoop = new SimpleFile("SL", ip, port, new String[]{"ED"}, "EB");
        assertTrue(admiral.tryInsertService(serviceB));
        assertTrue(admiral.tryInsertService(serviceA));
        assertTrue(admiral.tryInsertService(serviceC));
        assertTrue(admiral.tryInsertService(serviceD));
        assertTrue(admiral.tryInsertService(serviceE));
        assertTrue(admiral.tryInsertService(serviceF));
        assertFalse(admiral.tryInsertService(serviceLoop));

        SoldierFile serviceG = new SimpleFile("SG", ip, port, new String[]{"ED"}, "EB");
        SoldierFile serviceH = new SimpleFile("SB", ip, port, new String[]{"ED"}, "EB");
        SoldierFile serviceI = new SimpleFile("SC", ip, port, new String[]{"EA"}, "EF");
        assertFalse(admiral.tryRemoveService(serviceG));
        assertFalse(admiral.tryRemoveService(serviceH));
        assertFalse(admiral.tryRemoveService(serviceI));
        assertTrue(admiral.tryRemoveService(serviceA));
        assertTrue(admiral.tryRemoveService(serviceB));
        assertTrue(admiral.tryRemoveService(serviceC));
        assertTrue(admiral.tryRemoveService(serviceD));
        assertTrue(admiral.tryRemoveService(serviceE));
        assertTrue(admiral.tryRemoveService(serviceF));
    }
}