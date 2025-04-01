package io.github.admiral.spokesman;

import io.github.admiral.soldier.Produce;
import io.github.admiral.soldier.SoldierCreatable;
import io.github.admiral.soldier.SoldierInfo;
import io.github.admiral.soldier.SoldierInstance;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * This class will transfer the class annotated with {@link Spokesman} with two parts.
 * 1. Register methods annotated with {@link RequestMapping} into a proxy class that call the invoke to the department.
 * 2. Create a {@link SoldierInstance} which without subscribe and produces a event named with the class and method name.
 *
 * @author Jiahao Hwang
 */
@Component
@Slf4j
public class Propaganda implements SoldierCreatable {

    private final String thisFiled = "propaganda";

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    public Propaganda(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public Class<? extends Annotation> getSupportAnnotation() {
        return Spokesman.class;
    }

    @Override
    public Map<SoldierInfo, SoldierInstance> createSoldiers(Object object){
        return createRequestMappingAndSoldiers(object);
    }

    public SseEmitter invoke(String name, Object... args){
        return new SseEmitter();
    }

    /**
     * register RequestMappingInfo to RequestMappingHandlerMapping
     * @param bean
     */
    public Map<SoldierInfo, SoldierInstance> createRequestMappingAndSoldiers(Object bean){
        Map<SoldierInfo, SoldierInstance> soldiers = new HashMap<>();
        RequestMappingInfo typeInfo = createRequestMappingInfo(bean.getClass());
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass proxyClass = createProxyBean(bean); // create proxy class that expose url interface to browser
            CtClass originalClass = classPool.get(bean.getClass().getName());
            Map<String, RequestMappingInfo> nameInfoMap = new HashMap<>();
            // iterate methods with {@link RequestMapping}
            for (Method method: bean.getClass().getMethods()){
                RequestMappingInfo info = createRequestMappingInfo(method);
                if (info == null) continue;
                // create soldier
                Pair<SoldierInfo, SoldierInstance> soldier = createSoldier(bean, method);
                soldiers.put(soldier.getLeft(), soldier.getRight());
                copyMethod(proxyClass, originalClass, method);
                if (typeInfo != null) info = info.mutate().paths("", "/").build();
                nameInfoMap.put(method.getName(), info);
            }

            Class<?> clazz = proxyClass.toClass();
            Object proxyBean = clazz.getDeclaredConstructor().newInstance();
            proxyBean.getClass().getField(thisFiled).set(proxyBean, this);
            for (Method method: bean.getClass().getMethods()){
                if (!nameInfoMap.containsKey(method.getName())) continue;
                Method proxyMethod = proxyBean.getClass()
                        .getDeclaredMethod(method.getName(), method.getParameterTypes());
                requestMappingHandlerMapping.registerMapping(nameInfoMap.get(method.getName()), proxyBean, proxyMethod);
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return soldiers;
    }

    /**
     * Get all annotations in the given annotatedElement, including class or method.
     * For the sake of spring may create proxy class for the origin class, so we need to use
     * MergedAnnotations to get annotations from source classes.
     * @param annotatedElement
     * @return
     */
    private List<Annotation> getAnnotations(AnnotatedElement annotatedElement){
        return MergedAnnotations
                .from(annotatedElement, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none())
                .stream()
                .filter(MergedAnnotationPredicates.typeIn(RequestMapping.class)) // Filter class that have RequestMapping
                .filter(MergedAnnotationPredicates.firstRunOf(MergedAnnotation::getAggregateIndex)) // only reserve first annotation
                .map(MergedAnnotation::synthesize)
                .distinct().toList();
    }

    private @Nullable RequestMappingInfo createRequestMappingInfo(AnnotatedElement annotatedElement){
        List<Annotation> annotations = getAnnotations(annotatedElement);
        if (annotations.isEmpty()) return null;
        if (annotations.size() > 1)
            log.warn("Find multiply @RequestMapping annotation in the class of {}, but only the first will be apply", annotatedElement);
        return createRequestMappingInfo( (RequestMapping) annotations.getFirst());
    }

    /** Create RequestMapping from Annotation*/
    private RequestMappingInfo createRequestMappingInfo(RequestMapping requestMapping){
        return RequestMappingInfo.paths(requestMapping.path())
                .headers(requestMapping.headers())
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .produces(requestMapping.produces())
                .consumes(requestMapping.consumes())
                .build();
    }

    /**
     * Create proxy bean class that contains same filed
     * @param bean
     * @return
     */
    private @Nullable CtClass createProxyBean(Object bean){
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass("io.admiral.dynamic.%s".formatted(bean.getClass().getSimpleName()));
        try {
            CtClass propagandaClazz = classPool.get(Propaganda.class.getName());
            // add headquarter to call invoke function
            CtField headquarter = new CtField(propagandaClazz, thisFiled, ctClass);
            headquarter.setModifiers(Modifier.PUBLIC);
            ctClass.addField(headquarter);
            return ctClass;
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * Copy method in originalClass to proxyClass, reserving annotations in the original method at the same time.
     */
    private void copyMethod(CtClass proxyClass, CtClass originalClass, Method method){
        try {
            CtMethod srcMethod = originalClass.getDeclaredMethod(method.getName());
            CtClass[] parameterTypes = srcMethod.getParameterTypes();
            CtClass sseClazz = ClassPool.getDefault().get(SseEmitter.class.getName()); // set return type of sse
            CtMethod tgtMethod = new CtMethod(sseClazz, srcMethod.getName(), parameterTypes, proxyClass);
            tgtMethod.setModifiers(Modifier.PUBLIC);
            copyMethodAnnotations(srcMethod, tgtMethod);
            copyParameterAnnotations(srcMethod, tgtMethod);
            setMethodBody(srcMethod, tgtMethod);
            proxyClass.addMethod(tgtMethod);
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }

    private void copyMethodAnnotations(CtMethod srcMethod, CtMethod tgtMethod){
        AnnotationsAttribute srcAnnotations = (AnnotationsAttribute) srcMethod.getMethodInfo()
                .getAttribute(AnnotationsAttribute.visibleTag);
        if (srcAnnotations == null) return;
        ConstPool constPool = srcMethod.getMethodInfo().getConstPool();
        AnnotationsAttribute tgtAnnotations = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        // copy annotations
        for (javassist.bytecode.annotation.Annotation annotation : srcAnnotations.getAnnotations()){
            tgtAnnotations.addAnnotation(annotation);
        }
        tgtMethod.getMethodInfo().addAttribute(tgtAnnotations);
    }

    private void copyParameterAnnotations(CtMethod srcMethod, CtMethod tgtMethod) throws ClassNotFoundException {
        ParameterAnnotationsAttribute srcParamAnns = (ParameterAnnotationsAttribute) srcMethod.getMethodInfo()
                .getAttribute(ParameterAnnotationsAttribute.visibleTag);
        if (srcParamAnns == null) return;
        ConstPool constPool = srcMethod.getMethodInfo().getConstPool();;
        ParameterAnnotationsAttribute tgtParamAnns =
                new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
        javassist.bytecode.annotation.Annotation[][] srcAnnotations = srcParamAnns.getAnnotations();
        tgtParamAnns.setAnnotations(srcAnnotations);
        tgtMethod.getMethodInfo().addAttribute(tgtParamAnns);
    }

    private void setMethodBody(CtMethod srcMethod, CtMethod tgtMethod) throws Exception{
        String bodyTemplate = "return (%s) $0.%s.invoke(\"%s\", $args);";
        tgtMethod.setBody(bodyTemplate.formatted(tgtMethod.getReturnType().getName(), thisFiled, srcMethod.getName()));
    }

    private Pair<SoldierInfo, SoldierInstance> createSoldier(Object bean, Method method){
        Produce[] produces = method.getDeclaredAnnotationsByType(Produce.class);
        String produce;
        if (produces.length == 0){
            produce = bean.getClass().getName() + "$" + method.getName();
        } else {
            produce = produces[0].name();
        }
        String[] subscribes = new String[0];
        SoldierInfo soldierInfo = SoldierInfo.createSoldierInfo(method.getName(), subscribes, produce);
        SoldierInstance soldierInstance = new SoldierInstance(soldierInfo, bean, method);
        return Pair.of(soldierInfo, soldierInstance);
    }
}
