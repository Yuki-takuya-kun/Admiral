package io.github.admiral.hr;

/***
 * A simple service that only contains name, ip and port.
 */
public class SimpleFile extends SoldierFile {
    public SimpleFile(String name,
                         String[] subscribes,
                         String ip,
                         int port) {
        super(name, subscribes, ip, port);
    }
}
