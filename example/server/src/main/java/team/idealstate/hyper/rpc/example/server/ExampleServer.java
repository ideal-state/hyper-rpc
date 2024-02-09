package team.idealstate.hyper.rpc.example.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.idealstate.hyper.rpc.api.service.Watchdog;
import team.idealstate.hyper.rpc.example.common.KeyUtils;
import team.idealstate.hyper.rpc.example.common.model.entity.Hello;
import team.idealstate.hyper.rpc.example.common.service.CalcService;
import team.idealstate.hyper.rpc.example.common.service.HelloService;
import team.idealstate.hyper.rpc.impl.netty.ServerStarter;
import team.idealstate.hyper.rpc.impl.service.StdServiceManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Key;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.Executors;

/**
 * <p>ExampleServer</p>
 *
 * <p>创建于 2024/2/6 16:57</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.0
 */
public final class ExampleServer {

    private static final Logger logger = LogManager.getLogger(ExampleServer.class);

    public static void main(String[] args) throws IOException {
        final Key key = KeyUtils.loadPrivateKey();
        final InetSocketAddress bindAddress = new InetSocketAddress("0.0.0.0", 11454);
        final int nThreads = 2;
        final StdServiceManager serviceManager = new StdServiceManager(
                Executors.newFixedThreadPool(nThreads)
        );
        serviceManager.register(HelloService.class);
        serviceManager.register(CalcService.class);
        final ServerStarter serverStarter = new ServerStarter(bindAddress, key, serviceManager, nThreads);
        serviceManager.setServiceInvoker(serverStarter);
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
