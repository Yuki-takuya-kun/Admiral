package io.github.admiral;

import io.github.admiral.hr.*;
import io.github.admiral.hr.standalone.SimpleHR;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdmiralTest {

    @MockitoBean
    HumanResource serviceCenter = new SimpleHR(null);

    @Autowired
    Admiral admiral;

    @Test
    void tryInsertRemoveService() {
        BaseTroop serviceA = new SimpleTroop("SA", new BaseTroop[0]);
        BaseTroop serviceB = new SimpleTroop("SB", new BaseTroop[]{serviceA});
        BaseTroop serviceC = new SimpleTroop("SC", new BaseTroop[]{serviceA});
        BaseTroop serviceD = new SimpleTroop("SD", new BaseTroop[]{serviceB});
        BaseTroop serviceE = new SimpleTroop("SE", new BaseTroop[]{serviceD});
        BaseTroop serviceF = new SimpleTroop("SF", new BaseTroop[]{serviceB, serviceC});
//        Troop serviceLoop = new SimpleTroop("SL", new Troop[]{serviceD});
        assertFalse(admiral.tryInsertTroop(serviceB));
        assertTrue(admiral.tryInsertTroop(serviceA));
        assertTrue(admiral.tryInsertTroop(serviceB));
        assertTrue(admiral.tryInsertTroop(serviceC));
        assertTrue(admiral.tryInsertTroop(serviceD));
        assertTrue(admiral.tryInsertTroop(serviceE));
        assertTrue(admiral.tryInsertTroop(serviceF));
        //assertFalse(admiral.tryInsertTroop(serviceLoop));

        BaseTroop serviceG = new SimpleTroop("SG", new BaseTroop[]{serviceD});
        BaseTroop serviceH = new SimpleTroop("SB", new BaseTroop[]{serviceB});
        BaseTroop serviceI = new SimpleTroop("SC", new BaseTroop[]{serviceC});
        assertFalse(admiral.tryRemoveTroop(serviceG));
        assertFalse(admiral.tryRemoveTroop(serviceH));
        assertFalse(admiral.tryRemoveTroop(serviceI));
        assertTrue(admiral.tryRemoveTroop(serviceA));
        assertTrue(admiral.tryRemoveTroop(serviceB));
        assertTrue(admiral.tryRemoveTroop(serviceC));
        assertTrue(admiral.tryRemoveTroop(serviceD));
        assertTrue(admiral.tryRemoveTroop(serviceE));
        assertTrue(admiral.tryRemoveTroop(serviceF));
    }

    @Test
    void tryInsertBatchTroopSuccess(){
        BaseTroop serviceA = new SimpleTroop("SA", new BaseTroop[0]);
        BaseTroop serviceB = new SimpleTroop("SB", new BaseTroop[]{serviceA});
        BaseTroop serviceC = new SimpleTroop("SC", new BaseTroop[]{serviceA, serviceB});
        Set<BaseTroop> batch = new HashSet<BaseTroop>();
        batch.add(serviceA);
        batch.add(serviceB);
        batch.add(serviceC);
        assertTrue(admiral.tryInsertTroopBatch(batch));
    }

    @Test
    void tryInsertBatchTroopFailure(){
        BaseTroop serviceA = new SimpleTroop("SA", new BaseTroop[0]);
        BaseTroop serviceB = new SimpleTroop("SB", new BaseTroop[]{serviceA});
        BaseTroop serviceC = new SimpleTroop("SC", new BaseTroop[]{serviceA, serviceB});
        Set<BaseTroop> batch = new HashSet<>();
        batch.add(serviceB);
        batch.add(serviceC);
        assertFalse(admiral.tryInsertTroopBatch(batch));
    }
}