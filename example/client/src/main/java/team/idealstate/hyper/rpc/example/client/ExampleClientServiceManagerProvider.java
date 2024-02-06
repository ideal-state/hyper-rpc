package team.idealstate.hyper.rpc.example.client;

import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.api.service.ServiceManager;
import team.idealstate.hyper.rpc.api.service.spi.ServiceManagerProvider;
import team.idealstate.hyper.rpc.impl.service.JacksonInvokeInformationConverter;
import team.idealstate.hyper.rpc.impl.service.JdkProxyServiceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>ExampleClientServiceManagerProvider</p>
 *
 * <p>创建于 2024/2/6 17:07</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExampleClientServiceManagerProvider implements ServiceManagerProvider {

    private static volatile ServiceManager serviceManager = null;

    @Override
    public @NotNull ServiceManager get() {
        if (serviceManager == null) {
            synchronized (ExampleClientServiceManagerProvider.class) {
                if (serviceManager == null) {
                    final JacksonInvokeInformationConverter invokeInformationConverter = new JacksonInvokeInformationConverter();
                    final ExecutorService executorService = Executors.newSingleThreadExecutor();
                    serviceManager = new JdkProxyServiceManager(invokeInformationConverter, executorService);
                }
            }
        }
        return serviceManager;
    }
}
