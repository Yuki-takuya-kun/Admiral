package io.github.admiral.soldier;

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
    private SoldierCreatable[] soldierFactories;

    private Map<SoldierInfo, SoldierInstance> soldiers = new HashMap<SoldierInfo, SoldierInstance>();

    @Autowired
    public MilitaryDepartment(ApplicationContext context) {
        this.applicationContext = context;

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
    }

}
