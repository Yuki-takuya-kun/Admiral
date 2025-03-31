package io.github.admiral.soldier;

@Barrack
public class ArtilleryBarrack {

    @Soldier(subscribes = "io.github.admiral.soldier.LandForceSpokesman$landForceSpokesman",
    produce = "artilleryForward")
    public String forward(){
        return "artilleryBarrack";
    }

    @Soldier(subscribes = "io.github.admiral.soldier.LandForceSpokesman$landForceSpokesman",
            produce = "artilleryBomb")
    public String bomb(){
        return "Bomb!";
    }
}
