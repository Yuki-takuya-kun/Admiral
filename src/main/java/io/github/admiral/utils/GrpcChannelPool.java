package io.github.admiral.utils;

import io.grpc.ManagedChannel;
import org.apache.commons.pool2.KeyedObjectPool;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Grpc channel pool.
 */
@Component
public class GrpcChannelPool {
    private KeyedObjectPool<HostPortKey, ManagedChannel> channelPool;
    
    @Autowired
    public GrpcChannelPool(@Value("${grpc.conn.pool.maxTotalPerKey}") int maxTotalPerKey,
                           @Value("${grpc.conn.pool.minIdlePerKey}") int minIdlePerKey,
                           @Value("${grpc.conn.pool.maxIdlePerKey}") int maxIdlePerKey,
                           @Value("${grpc.conn.pool.maxTotal}") int maxTotal
                           ) {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxTotalPerKey(maxTotalPerKey); // max connection per key
        config.setMinIdlePerKey(minIdlePerKey); // min connection per key
        config.setMaxIdlePerKey(maxIdlePerKey);
        config.setMaxTotal(maxTotal);
        channelPool = new GenericKeyedObjectPool<>(new ChannelFactory(), config);
    }

    /** Get grpc channel from grp channel pool.*/
    public ManagedChannel getChannel(String host, int port) throws Exception{
        return channelPool.borrowObject(new HostPortKey(host, port));
    }

    /** Close grpc channel pool.*/
    public void close(){
        channelPool.close();
    }

    /** Return grpc channel.*/
    public void returnChannel(String host, int port, ManagedChannel channel) throws Exception {
        channelPool.returnObject(new HostPortKey(host, port), channel);
    }

    /** Key for ip and host*/
    private static class HostPortKey {
        private final String host;
        private final int port;

        public HostPortKey(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HostPortKey that = (HostPortKey) o;
            return port == that.port && host.equals(that.host);
        }

        @Override
        public int hashCode() {
            return (host.hashCode() << 5) - host.hashCode() + port;
        }
    }

    /** Channel Instance */
    private static class ChannelFactory implements KeyedPooledObjectFactory<HostPortKey, ManagedChannel> {

        @Override
        public PooledObject<ManagedChannel> makeObject(HostPortKey key) throws Exception {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(key.host, key.port)
                    .usePlaintext()
                    .build();

            return new DefaultPooledObject<>(channel);
        }

        @Override
        public void destroyObject(HostPortKey key, PooledObject<ManagedChannel> p) throws Exception {
            ManagedChannel channel = p.getObject();
            if (!channel.isShutdown()) {
                channel.shutdown();
                channel.awaitTermination(5, TimeUnit.SECONDS);
            }
        }

        @Override
        public boolean validateObject(HostPortKey key, PooledObject<ManagedChannel> p) {
            ManagedChannel channel = p.getObject();
            return !channel.isShutdown() && !channel.isTerminated();
        }

        @Override
        public void activateObject(HostPortKey key, PooledObject<ManagedChannel> p) throws Exception {

        }

        @Override
        public void passivateObject(HostPortKey key, PooledObject<ManagedChannel> p) throws Exception {

        }
    }
}
