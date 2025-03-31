package io.github.admiral;

import io.github.admiral.service.HumanResource;
import io.github.admiral.soldier.SoldierCreatable;
import io.github.admiral.soldier.SoldierInfo;
import io.github.admiral.soldier.SoldierInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j(topic="SoldierLogger")
public class MilitaryDepartment {

    private final ApplicationContext applicationContext;
    private final SoldierCreatable[] soldierFactories;
    private final HumanResource humanResource;

    private Map<SoldierInfo, SoldierInstance> soldiers = new HashMap<SoldierInfo, SoldierInstance>();

    @Autowired
    public MilitaryDepartment(ApplicationContext context,
                              HumanResource humanResource) {
        this.applicationContext = context;
        this.humanResource = humanResource;
        soldierFactories = applicationContext.getBeansOfType(SoldierCreatable.class).values().toArray(new SoldierCreatable[0]);
    }


    /** Scan all components that annotated by class that implements soldierCreatable interface .
     * Then register all soldiers to MilitaryDepartment*/
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for (SoldierCreatable soldierFactory : soldierFactories) {
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(soldierFactory.getSupportAnnotation());
            // merge all beans to the soldierFactory
            for (Object bean : beans.values()) {
                soldiers.putAll(soldierFactory.createSoldiers(bean));
            }
        }
        registerSoldiers();
    }

    /** Register all soldiers to service center.*/
    public void registerSoldiers(){
        for (SoldierInfo soldierInfo : soldiers.keySet()) {
            if (!humanResource.register(soldierInfo)){
                log.error("Soldier " + soldierInfo + "registered fail.");
            };
        }
    }

}
