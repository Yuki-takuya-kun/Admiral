package io.github.admiral.hr;

import java.util.List;
import java.util.Map;

/**LoadBalancer to get file object.*/
public interface FileLoadBalancer {

    /** Method that balancing get file.*/
    SoldierFile getFileBalanced(List<SoldierFile> files);
}
