package io.github.admiral;

import io.github.admiral.hr.*;
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
    HumanResource serviceCenter = new SimpleHR();

    @Autowired
    Admiral admiral;

    @Test
    void tryInsertRemoveService() {
        Troop serviceA = new SimpleTroop("SA", new Troop[0]);
        Troop serviceB = new SimpleTroop("SB", new Troop[]{serviceA});
        Troop serviceC = new SimpleTroop("SC", new Troop[]{serviceA});
        Troop serviceD = new SimpleTroop("SD", new Troop[]{serviceB});
        Troop serviceE = new SimpleTroop("SE", new Troop[]{serviceD});
        Troop serviceF = new SimpleTroop("SF", new Troop[]{serviceB, serviceC});
//        Troop serviceLoop = new SimpleTroop("SL", new Troop[]{serviceD});
        assertFalse(admiral.tryInsertTroop(serviceB));
        assertTrue(admiral.tryInsertTroop(serviceA));
        assertTrue(admiral.tryInsertTroop(serviceB));
        assertTrue(admiral.tryInsertTroop(serviceC));
        assertTrue(admiral.tryInsertTroop(serviceD));
        assertTrue(admiral.tryInsertTroop(serviceE));
        assertTrue(admiral.tryInsertTroop(serviceF));
        //assertFalse(admiral.tryInsertTroop(serviceLoop));

        Troop serviceG = new SimpleTroop("SG", new Troop[]{serviceD});
        Troop serviceH = new SimpleTroop("SB", new Troop[]{serviceB});
        Troop serviceI = new SimpleTroop("SC", new Troop[]{serviceC});
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
        Troop serviceA = new SimpleTroop("SA", new Troop[0]);
        Troop serviceB = new SimpleTroop("SB", new Troop[]{serviceA});
        Troop serviceC = new SimpleTroop("SC", new Troop[]{serviceA, serviceB});
        Set<Troop> batch = new HashSet<Troop>();
        batch.add(serviceA);
        batch.add(serviceB);
        batch.add(serviceC);
        assertTrue(admiral.tryInsertTroopBatch(batch));
    }

    @Test
    void tryInsertBatchTroopFailure(){
        Troop serviceA = new SimpleTroop("SA", new Troop[0]);
        Troop serviceB = new SimpleTroop("SB", new Troop[]{serviceA});
        Troop serviceC = new SimpleTroop("SC", new Troop[]{serviceA, serviceB});
        Set<Troop> batch = new HashSet<>();
        batch.add(serviceB);
        batch.add(serviceC);
        assertFalse(admiral.tryInsertTroopBatch(batch));
    }
}