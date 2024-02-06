package team.idealstate.hyper.rpc.example.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.idealstate.hyper.rpc.api.service.ServiceManager;
import team.idealstate.hyper.rpc.api.service.Watchdog;
import team.idealstate.hyper.rpc.example.common.KeyUtils;
import team.idealstate.hyper.rpc.example.common.service.HelloService;
import team.idealstate.hyper.rpc.example.server.service.HelloServiceImpl;
import team.idealstate.hyper.rpc.impl.netty.ServerStarter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Key;
import java.util.Scanner;

/**
 * <p>ExampleServer</p>
 *
 * <p>创建于 2024/2/6 16:57</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ExampleServer {

    private static final Logger logger = LogManager.getLogger(ExampleServer.class);

    public static void main(String[] args) throws IOException {
        final Key key = KeyUtils.loadPublicKey();
        final InetSocketAddress bindAddress = new InetSocketAddress("0.0.0.0", 11454);
        final ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.register(HelloService.class, new HelloServiceImpl());
        final int nThreads = 2;
        final ServerStarter serverStarter = new ServerStarter(bindAddress, key, serviceManager, nThreads);
        final Watchdog watchdog = new Watchdog(serverStarter);
        watchdog.startup();
        final Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if ("stop".equals(line)) {
                logger.info("已接收到退出指令");
                break;
            }
        }
        watchdog.shutdown();
    }
}
