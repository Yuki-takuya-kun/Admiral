package io.github.admiral.admiral;

import io.github.admiral.common.RequestInfo;
import io.github.admiral.hr.SimpleTroop;
import io.github.admiral.hr.BaseTroop;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AdmiralChiefOfStaffTest {

    @Test
    void isEnd() {
    }

    @Test
    void testSchedule(){

        AdmiralChiefOfStaff staff;
        Map<BaseTroop, Set<BaseTroop>> childs = new HashMap<>();
        Map<BaseTroop, Set<BaseTroop>> parents = new HashMap<>();
        Set<BaseTroop> roots = new HashSet<>();

        BaseTroop serviceA = new SimpleTroop("SA", new BaseTroop[0]);
        BaseTroop serviceB = new SimpleTroop("SB", new BaseTroop[]{serviceA});
        BaseTroop serviceC = new SimpleTroop("SC", new BaseTroop[]{serviceA});
        BaseTroop serviceD = new SimpleTroop("SD", new BaseTroop[]{serviceB});
        BaseTroop serviceE = new SimpleTroop("SE", new BaseTroop[]{serviceD});
        BaseTroop serviceF = new SimpleTroop("SF", new BaseTroop[]{serviceB, serviceC});
        childs.put(serviceA, Set.of(serviceB, serviceC));
        childs.put(serviceB, Set.of(serviceF, serviceD));
        childs.put(serviceC, Set.of(serviceF));
        childs.put(serviceD, Set.of(serviceE));
        childs.put(serviceE, new HashSet<>());
        childs.put(serviceF, new HashSet<>());

        parents.put(serviceA, new HashSet<>());
        parents.put(serviceB, Set.of(serviceA));
        parents.put(serviceC, Set.of(serviceA));
        parents.put(serviceD, Set.of(serviceB));
        parents.put(serviceE, Set.of(serviceD));
        parents.put(serviceF, Set.of(serviceB, serviceC));

        roots.add(serviceA);
        List<BaseTroop> troopSet = new ArrayList<>();
        troopSet.add(serviceA);
        troopSet.add(serviceB);
        troopSet.add(serviceC);
        troopSet.add(serviceD);
        troopSet.add(serviceE);
        troopSet.add(serviceF);


        staff = new AdmiralChiefOfStaff(childs, parents, roots);

        RequestInfo requestInfo = new RequestInfo("1", System.currentTimeMillis());
        Set<BaseTroop> scheduledTroops = new HashSet<>();

        List<BaseTroop> nextTroops;
        try {
            nextTroops = staff.nextTroop(requestInfo, serviceB);
        } catch (IllegalTroopException e){
            assertInstanceOf(IllegalTroopException.class, e);
        }

        requestInfo = new RequestInfo("uuid", System.currentTimeMillis());
        try {
            nextTroops = staff.nextTroop(requestInfo, serviceA);
            scheduledTroops.add(serviceA);
            while (!nextTroops.isEmpty()){
                BaseTroop nextTroop = nextTroops.remove(0);
                scheduledTroops.add(nextTroop);
                nextTroops.addAll(staff.nextTroop(requestInfo, nextTroop));
            }
        } catch (Exception e){
            e.printStackTrace();
            assertFalse(true);

        }
        String scheduledTroopsString = scheduledTroops.toString();
        String troopSetString = troopSet.toString();
        assertEquals(scheduledTroops.size(), troopSet.size(),
                "Scheduled Troops size is not equals to troop set size.\n"+
                "Scheduled Troops: " + scheduledTroopsString + "\n" +
                "Troop Set: " + troopSetString);
        scheduledTroops.forEach(t->{assertTrue(troopSet.contains(t));});


    }

    @Test
    void troopFailed() {
    }
}