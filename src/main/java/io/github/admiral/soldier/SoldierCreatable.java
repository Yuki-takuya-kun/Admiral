package io.github.admiral.soldier;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Interface that indicates the object can create SoldierInfos and SoldierInstances
 */
public interface SoldierCreatable {
    /** Indicates the clazz can create soldier from the class or not.*/
    Class<? extends Annotation> getSupportAnnotation();

    /** Create soldiers from the object*/
    Map<SoldierInfo, SoldierInstance> createSoldiers(Object object);

}
