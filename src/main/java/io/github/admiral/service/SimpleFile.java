package io.github.admiral.service;

/***
 * A simple service that only contains name, ip and port.
 */
public class SimpleFile extends SoldierFile {
    public SimpleFile(String name,
                         String ip,
                         int port,
                         String[] consumes,
                         String produce) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.consumes = consumes;
        this.produce = produce;
    }
}
