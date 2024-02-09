package team.idealstate.hyper.rpc.example.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.idealstate.hyper.rpc.api.StringUtils;
import team.idealstate.hyper.rpc.api.service.Watchdog;
import team.idealstate.hyper.rpc.example.common.KeyUtils;
import team.idealstate.hyper.rpc.example.common.service.CalcService;
import team.idealstate.hyper.rpc.example.common.service.HelloService;
import team.idealstate.hyper.rpc.impl.netty.ClientStarter;
import team.idealstate.hyper.rpc.impl.service.StdServiceManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Key;
import java.util.Scanner;
import java.util.concurrent.Executors;

/**
 * <p>ExampleClient</p>
 *
 * <p>创建于 2024/2/6 16:57</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.0
 */
public final class ExampleClient {

    private static final Logger logger = LogManager.getLogger(ExampleClient.class);

    public static void main(String[] args) throws IOException {
        final Key key = KeyUtils.loadPublicKey();
        final InetSocketAddress connectAddress = new InetSocketAddress("0.0.0.0", 11454);
        final int nThreads = 2;
        final StdServiceManager serviceManager = new StdServiceManager(
                Executors.newFixedThreadPool(nThreads)
        );
        serviceManager.register(HelloService.class);
        serviceManager.register(CalcService.class);
        final ClientStarter clientStarter = new ClientStarter(connectAddress, key, serviceManager, nThreads);
        serviceManager.setServiceInvoker(clientStarter);
        final Watchdog watchdog = new Watchdog(clientStarter);
        watchdog.startup();
        final Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if ("stop".equals(line)) {
                logger.info("已接收到退出指令");
                break;
            }
            if (line.startsWith("calc ")) {
                String[] args0 = line.split(" ");
                if (args0.length == 3 && StringUtils.isIntegral(args0[1]) && StringUtils.isIntegral(args0[2])) {

                    final CalcService calcService;
                    try {
                        calcService = serviceManager.get(CalcService.class);
                        int a = Integer.parseInt(args0[1]);
                        int b = Integer.parseInt(args0[2]);
                        long sum = calcService.sum(a, b);
                        logger.info("[CalcService]: {} + {} = {}", a, b, sum);
                    } catch (Throwable e) {
                        logger.catching(e);
                    }
                }
            }
        }
        watchdog.shutdown();
    }
}
