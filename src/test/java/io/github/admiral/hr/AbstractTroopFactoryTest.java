package io.github.admiral.hr;

import io.github.admiral.hr.standalone.SimpleHR;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractTroopFactoryTest {

    SimpleHR hr = new SimpleHR(null);
    SimpleTroopFactory tf = new SimpleTroopFactory(hr);
    PersonnelMinistry ministry = new PersonnelMinistry(hr, tf);

    @Test
    void doCreateTroop() {
        String host = "0.0.0.0";
        int port = 8080;
        SoldierFile fileA = new SimpleFile("SA", new String[0], host, port);
        SoldierFile fileB = new SimpleFile("SB", new String[]{"SA"}, host, port);
        SoldierFile fileC = new SimpleFile("SC", new String[]{"SA"}, host, port);
        SoldierFile fileD = new SimpleFile("SD", new String[]{"SC"}, host, port);

        List<SoldierFile> files = new ArrayList<>();
        files.add(fileB);
        files.add(fileA);
        files.add(fileC);
        files.add(fileD);

        ministry.register(files);
        List<BaseTroop> troops = ministry.getAllTroops();
        assertEquals(4, troops.size());
    }
}