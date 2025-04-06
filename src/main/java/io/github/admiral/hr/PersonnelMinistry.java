package io.github.admiral.hr;

import io.github.admiral.soldier.SoldierInfo;

import java.util.List;

/** Mange human resources and troop.*/
public class PersonnelMinistry {
    private HumanResource hr;
    private AbstractTroopFactory troopFactory;

    public PersonnelMinistry(HumanResource hr, AbstractTroopFactory troopFactory) {
        this.hr = hr;
        this.troopFactory = troopFactory;
    }

    /** Register SoldierFiles. However, register process should be as transaction,
     * */
    public void register(List<SoldierFile> soldierFiles){
        try {
            for (SoldierFile soldierFile : soldierFiles) {
                hr.register(soldierFile);
            }

            for (SoldierFile soldierFile : soldierFiles) {
                SoldierInfo info = hr.getInfo(soldierFile.getName());
                troopFactory.doCreateTroop(info);
            }
        } catch (HRException | TroopException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public List<BaseTroop> getAllTroops(){
        return troopFactory.getTroops();
    }
}
