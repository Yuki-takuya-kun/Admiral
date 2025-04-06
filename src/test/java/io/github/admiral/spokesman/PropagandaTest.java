package io.github.admiral.spokesman;

import io.github.admiral.MilitaryDepartment;
import io.github.admiral.soldier.SoldierInfo;
import io.github.admiral.soldier.SoldierInstance;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PropagandaTest {

    @MockitoBean
    private MilitaryDepartment militaryDepartment;

    @MockitoBean
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private Propaganda propaganda;

    @Test
    void getSupportAnnotation() {
        assertEquals(propaganda.getSupportAnnotation(), Spokesman.class);
    }

    @Test
    void createSoldiers() {
        Map<String, Triple<RequestMappingInfo, Object, Method>> handlerMethods = new HashMap<>();
        Mockito.doAnswer(invocation -> {
            handlerMethods.put(invocation.getArgument(0).toString(),
                    Triple.of(invocation.getArgument(0),
                    invocation.getArgument(1), invocation.getArgument(2)));
            return null;
        }).when(requestMappingHandlerMapping).registerMapping(Mockito.any(RequestMappingInfo.class),
                Mockito.any(Object.class), Mockito.any(Method.class));

        LandForceSpokesman spokesman = new LandForceSpokesman();
        Map<SoldierInfo, SoldierInstance> soldierMap = propaganda.createSoldiers(spokesman);
        //System.out.println(soldierMap.keySet());
        // assert if the method and parameters annotations is copied correctly or not.
        Triple<RequestMappingInfo, Object, Method> handlerMethod = handlerMethods.get("{GET [/announce/{name}]}");
        Method method = handlerMethod.getRight();
        Annotation[] methodAnnSet = method.getAnnotations();
        assertTrue(methodAnnSet[0] instanceof GetMapping);
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        assertTrue(parameterAnnotations[0][0] instanceof PathVariable);
        assertTrue(parameterAnnotations[1][0] instanceof RequestParam);

        // assert if the soldierInfo is copy correctly or not.

        for (SoldierInfo soldierInfo : soldierMap.keySet()) {
            if (soldierInfo.getName().equals("io.github.admiral.spokesman.LandForceSpokesman$landForceSpokesman")) {
                assertEquals(soldierInfo.toString(), "io.github.admiral.spokesman.LandForceSpokesman$landForceSpokesman {subscribes: }");
                SoldierInstance soldierInstance = soldierMap.get(soldierInfo);
                String response = (String) soldierInstance.execute("Alice", 12);
                assertEquals(response, "I'am land force spokesman, my name is Alice and age is 12");
            }

            else if (soldierInfo.getName().equals("io.github.admiral.spokesman.LandForceSpokesman$answer")){
                assertEquals(soldierInfo.getName(), "answer");
            }
        }


    }

    @Test
    void invoke() {
    }

}