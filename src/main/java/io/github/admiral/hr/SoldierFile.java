package io.github.admiral.hr;

import io.github.admiral.soldier.SoldierInfo;
import lombok.Getter;

/**
 * Abstract class of Service, the following field is needed.
 * Child class can define custom field.
 *
 * @author Jiahao Hwang
 */
@Getter
public abstract class SoldierFile extends SoldierInfo {

    protected String ip;
    protected int port;
    private final String hashStr;

    public SoldierFile(String name, String[] subscribes, String ip, int port) {
        super(name, subscribes);
        this.ip = ip;
        this.port = port;
        hashStr = name + ":" + ip + ":" + port;
    }

    @Override
    public int hashCode(){
        return hashStr.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if (obj == this) return true;
        if (obj == null || !(obj instanceof SoldierFile)) return false;
        SoldierFile other = (SoldierFile) obj;
        if (this.getName() != other.getName() || this.getIp() != other.getIp() || this.getPort() != other.getPort()){
            return false;
        }
        return true;
    }
}
