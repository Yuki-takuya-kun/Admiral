package io.github.admiral.hr;

/** Abstract Human Resource Client*/
public abstract class AbstractHumanResourceClient implements HumanResourceClient {

    protected final String host;
    protected final int port;

    public AbstractHumanResourceClient(final String host, final int port) {
        this.host = host;
        this.port = port;
    }
}
