package io.github.admiral.hr;

/***
 * A simple service that only contains name, ip and port.
 */
public class SimpleFile extends SoldierFile {
    public SimpleFile(Troop troop,
                         String ip,
                         int port) {
        this.troop = troop;
        this.ip = ip;
        this.port = port;
    }
}
