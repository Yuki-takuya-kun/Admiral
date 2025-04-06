package io.github.admiral;

import com.google.protobuf.Empty;
import io.github.admiral.communicate.CommandSignalCorp;
import io.github.admiral.communicate.TaskAssignGrpc;
import io.github.admiral.communicate.exchange.ExchangeDataGrpc;
import io.github.admiral.communicate.exchange.ExchangeSignalCorp;
import io.github.admiral.communicate.report.ReportSignalCorp;
import io.github.admiral.communicate.report.TaskReportGrpc;
import io.github.admiral.department.AbstractMilitaryDepartment;
import io.github.admiral.department.ExchangeData;
import io.github.admiral.department.ExchangeRequest;
import io.github.admiral.hr.HumanResourceClient;
import io.github.admiral.soldier.SoldierCreatable;
import io.github.admiral.soldier.SoldierInfo;
import io.github.admiral.soldier.SoldierInstance;
import io.github.admiral.utils.GrpcChannelPool;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j(topic="SoldierLogger")
public class MilitaryDepartment extends AbstractMilitaryDepartment {

    private final ApplicationContext applicationContext;
    private final SoldierCreatable[] soldierFactories;
    private final HumanResourceClient humanResourceClient;
    private final GrpcChannelPool grpcChannelPool;

    private Map<SoldierInfo, SoldierInstance> soldiers = new HashMap<SoldierInfo, SoldierInstance>();

    @Autowired
    public MilitaryDepartment(ApplicationContext context,
                              HumanResourceClient humanResourceClient,
                              GrpcChannelPool grpcChannelPool) {
        this.applicationContext = context;
        this.humanResourceClient = humanResourceClient;
        this.grpcChannelPool = grpcChannelPool;
        soldierFactories = applicationContext.getBeansOfType(SoldierCreatable.class).values().toArray(new SoldierCreatable[0]);

    }


    /** Scan all components that annotated by class that implements soldierCreatable interface .
     * Then register all soldiers to MilitaryDepartment*/
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for (SoldierCreatable soldierFactory : soldierFactories) {
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(soldierFactory.getSupportAnnotation());
            // merge all beans to the soldierFactory
            for (Object bean : beans.values()) {
                soldiers.putAll(soldierFactory.createSoldiers(bean));
            }
        }
        registerSoldiers();
    }

    /** Register all soldiers to service center and admiral.*/
    public void registerSoldiers(){
        for (SoldierInfo soldierInfo : soldiers.keySet()) {
            humanResourceClient.register(soldierInfo);
            if (soldierInfo.getSubscribes().length == 0){
//                RegisterMessage registerMessage = new RegisterMessage(soldierInfo.getName(), new String[]{"all"});
//                admiralSignalCorp.send(registerMessage);
            }
        }
    }

    @Override
    public ExchangeData getData(ExchangeRequest request){
        return null;
    }

    public void report(String host, int port, ReportSignalCorp.ReportMessage reportMessage){
        ManagedChannel channel = null;
        try {
            channel = grpcChannelPool.getChannel(host, port);
            TaskReportGrpc.TaskReportStub stub = TaskReportGrpc.newStub(channel);
            // do nothing
            stub.report(reportMessage, new StreamObserver<Empty>() {
                @Override
                public void onNext(Empty empty) {}

                @Override
                public void onError(Throwable throwable) {}

                @Override
                public void onCompleted() {}
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (channel != null) {
                try {
                    grpcChannelPool.returnChannel(host, port, channel);
                } catch (Exception e) {
                    log.error("return channel failed. \n{}", e.getMessage());
                }
            }
        }
    }


}
