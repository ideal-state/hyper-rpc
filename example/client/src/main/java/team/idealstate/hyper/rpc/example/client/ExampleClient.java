package team.idealstate.hyper.rpc.example.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.idealstate.hyper.rpc.api.service.ServiceManager;
import team.idealstate.hyper.rpc.api.service.Watchdog;
import team.idealstate.hyper.rpc.example.common.KeyUtils;
import team.idealstate.hyper.rpc.example.common.model.entity.Hello;
import team.idealstate.hyper.rpc.example.common.service.HelloService;
import team.idealstate.hyper.rpc.impl.netty.ClientStarter;
import team.idealstate.hyper.rpc.impl.service.JdkProxyServiceManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Key;
import java.time.Instant;
import java.util.Scanner;

/**
 * <p>ExampleClient</p>
 *
 * <p>创建于 2024/2/6 16:57</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ExampleClient {

    private static final Logger logger = LogManager.getLogger(ExampleClient.class);

    public static void main(String[] args) throws IOException {
        final Key key = KeyUtils.loadPrivateKey();
        final InetSocketAddress connectAddress = new InetSocketAddress("0.0.0.0", 11454);
        final ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.register(HelloService.class);
        final int nThreads = 2;
        final ClientStarter serverStarter = new ClientStarter(connectAddress, key, serviceManager, nThreads);
        ((JdkProxyServiceManager) serviceManager).setServiceInvoker(serverStarter);
        final Watchdog watchdog = new Watchdog(serverStarter);
        watchdog.startup();
        final Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if ("stop".equals(line)) {
                logger.info("已接收到退出指令");
                break;
            }
            if (line.startsWith("hello ")) {
                final HelloService helloService;
                try {
                    helloService = serviceManager.get(HelloService.class);
                    final Hello hello = helloService.hello(new Hello(line.substring(6), Instant.now()));
                    logger.info("[{}]: {}", hello.getTimestamp(), hello.getMessage());
                } catch (Throwable e) {
                    logger.catching(e);
                }
            }
        }
        watchdog.shutdown();
    }
}
