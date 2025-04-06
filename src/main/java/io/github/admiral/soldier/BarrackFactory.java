package io.github.admiral.soldier;

import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for create soldiers given a class that annotated by {@link Barrack}
 */
@Component
public class BarrackFactory implements SoldierCreatable{

    @Override
    public Map<SoldierInfo, SoldierInstance> createSoldiers(Object barrack){
        Map<SoldierInfo, SoldierInstance> soldiers = new HashMap<>();

        Method[] methods = barrack.getClass().getMethods();
        // get method that use {@link Soldier annotation.}
        for (Method method: methods) {
            for (Annotation annotation: method.getDeclaredAnnotations()) {
                if (annotation instanceof Soldier soldier){
                    // Default soldier name will be the full quality barrackName concat with method name.
                    String name = soldier.name();
                    String[] subscribes = soldier.subscribes();
                    SoldierInfo soldierInfo = SoldierFactory.createSoldierInfo(name, subscribes);
                    SoldierInstance soldierInstance = SoldierFactory.createSoldierInstance(soldierInfo, barrack, method);
                    soldiers.put(soldierInfo, soldierInstance);
                }
            }
        }
        return soldiers;
    }

    @Override
    public Class<? extends Annotation> getSupportAnnotation() {
        return Barrack.class;
    }


}
