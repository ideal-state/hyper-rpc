package team.idealstate.hyper.rpc.example.server;

import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.api.service.ServiceManager;
import team.idealstate.hyper.rpc.api.service.spi.ServiceManagerProvider;
import team.idealstate.hyper.rpc.impl.service.JacksonInvokeInformationConverter;
import team.idealstate.hyper.rpc.impl.service.ManualRegistrationServiceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>ExampleServerServiceManagerProvider</p>
 *
 * <p>创建于 2024/2/6 17:07</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExampleServerServiceManagerProvider implements ServiceManagerProvider {

    private static volatile ServiceManager serviceManager = null;

    @Override
    public @NotNull ServiceManager get() {
        if (serviceManager == null) {
            synchronized (ExampleServerServiceManagerProvider.class) {
                if (serviceManager == null) {
                    final JacksonInvokeInformationConverter invokeInformationConverter = new JacksonInvokeInformationConverter();
                    final ExecutorService executorService = Executors.newSingleThreadExecutor();
                    serviceManager = new ManualRegistrationServiceManager(invokeInformationConverter, executorService);
                }
            }
        }
        return serviceManager;
    }
}
