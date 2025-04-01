package io.github.admiral;

import io.github.admiral.hr.*;
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
        Troop serviceA = new SimpleTroop("SA", new String[0], "EA");
        Troop serviceB = new SimpleTroop("SB", new String[]{"EA"}, "EB");
        Troop serviceC = new SimpleTroop("SC", new String[]{"EA"}, "EC");
        Troop serviceD = new SimpleTroop("SD", new String[]{"EB"}, "ED");
        Troop serviceE = new SimpleTroop("SE", new String[]{"ED"}, "EC");
        Troop serviceF = new SimpleTroop("SF", new String[]{"EB", "EC"}, "EF");
        Troop serviceLoop = new SimpleTroop("SL", new String[]{"ED"}, "EB");
        assertTrue(admiral.tryInsertService(serviceB));
        assertTrue(admiral.tryInsertService(serviceA));
        assertTrue(admiral.tryInsertService(serviceC));
        assertTrue(admiral.tryInsertService(serviceD));
        assertTrue(admiral.tryInsertService(serviceE));
        assertTrue(admiral.tryInsertService(serviceF));
        assertFalse(admiral.tryInsertService(serviceLoop));

        Troop serviceG = new SimpleTroop("SG", new String[]{"ED"}, "EB");
        Troop serviceH = new SimpleTroop("SB", new String[]{"ED"}, "EB");
        Troop serviceI = new SimpleTroop("SC", new String[]{"EA"}, "EF");
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