package io.github.admiral.hr.standalone;

import io.github.admiral.hr.AbstractHumanResourceClient;
import io.github.admiral.hr.HumanResource;
import io.github.admiral.hr.SimpleFile;
import io.github.admiral.hr.SoldierFile;
import io.github.admiral.soldier.SoldierInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Standalone mode client, directly register to simpleHR*/
@Component
public class SimpleHRClient extends AbstractHumanResourceClient {

    private SimpleHR hr;

    public SimpleHRClient() {
        super("0.0.0.0", 6553);
    }

    @Autowired
    public void setHr(HumanResource hr) {
        this.hr = (SimpleHR) hr;
    }

    public void connect(){return;}

    public void register(SoldierInfo soldierInfo){
        SoldierFile sf = new SimpleFile(soldierInfo.getName(), soldierInfo.getSubscribes(), host, port);
        hr.register(sf);
    }

}
