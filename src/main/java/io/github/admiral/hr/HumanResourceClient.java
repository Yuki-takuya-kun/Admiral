package io.github.admiral.hr;

import io.github.admiral.soldier.LocalSoldier;
import io.github.admiral.soldier.SoldierInfo;

/** Interface for human resource client.*/
public interface HumanResourceClient {

    /** Register the human resource*/
    void register(SoldierInfo soldierInfo);

    /** Connect to human resource server.*/
    void connect();
}
