package io.github.admiral.hr;

import java.util.List;
import java.util.Random;

/** Using random select algorithm.*/
public class RandomLoadBalancer implements FileLoadBalancer {

    private Random rand = new Random();

    public SoldierFile getFileBalanced(List<SoldierFile> files){
        int idx = rand.nextInt(files.size());
        return files.get(idx);
    }
}
