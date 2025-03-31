package io.github.admiral.spokesman;

import io.github.admiral.soldier.Produce;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Spokesman
public class LandForceSpokesman {

    @GetMapping("announce/{name}")
    public String landForceSpokesman(@PathVariable String name, @RequestParam int age) {
        return "I'am land force spokesman, my name is " + name + " and age is " + age;
    }

    @GetMapping("answer")
    @Produce(name= "answer")
    public String answer(){
        return "I have answer your question";
    }
}
