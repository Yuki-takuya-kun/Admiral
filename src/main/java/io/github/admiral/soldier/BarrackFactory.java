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
                    String name = barrack.getClass().getName() + "$" + method.getName();
                    String[] subscribes = soldier.subscribes();
                    String produce = soldier.produce();
                    SoldierInfo soldierInfo = SoldierInfo.createSoldierInfo(name, subscribes, produce);
                    SoldierInstance soldierInstance = new SoldierInstance(soldierInfo, barrack, method);
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
