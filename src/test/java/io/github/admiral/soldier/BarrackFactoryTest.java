package io.github.admiral.soldier;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BarrackFactoryTest {

    ArtilleryBarrack artilleryBarrack = new ArtilleryBarrack();
    BarrackFactory barrackFactory = new BarrackFactory();

    @Test
    void createSoldiers() {
        Map<SoldierInfo, SoldierInstance> soldierMap = barrackFactory.createSoldiers(artilleryBarrack);
        Set<String> infos = new HashSet<>();
        Set<String> returns = new HashSet<>();
        for (SoldierInfo soldierInfo : soldierMap.keySet()) {
            infos.add(soldierInfo.toString());
            returns.add((String) soldierMap.get(soldierInfo).execute());
        }
        assertTrue(infos.contains("io.github.admiral.soldier.ArtilleryBarrack$bomb {consumes: io.github.admiral.soldier.LandForceSpokesman$landForceSpokesman, produce: artilleryBomb}"));
        assertTrue(returns.contains("artilleryBarrack"));
        assertTrue(returns.contains("Bomb!"));
    }

    @Test
    void getSupportAnnotation() {
        assertEquals(barrackFactory.getSupportAnnotation(), Barrack.class);
    }
}