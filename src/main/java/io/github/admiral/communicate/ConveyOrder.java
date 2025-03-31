package io.github.admiral.communicate;

import io.github.admiral.service.HumanResource;

/** Transfer order to front line.*/
public interface ConveyOrder {
    public boolean convey(Issue issue, HumanResource humanResource);
}
