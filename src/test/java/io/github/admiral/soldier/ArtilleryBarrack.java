package io.github.admiral.soldier;

@Barrack
public class ArtilleryBarrack {

    @Soldier(subscribes = "io.github.admiral.soldier.LandForceSpokesman$landForceSpokesman",
    name = "artilleryForward")
    public String forward(){
        return "artilleryBarrack";
    }

    @Soldier(subscribes = "io.github.admiral.soldier.LandForceSpokesman$landForceSpokesman",
            name = "artilleryBomb")
    public String bomb(){
        return "Bomb!";
    }
}
