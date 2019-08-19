package ee.avalanchelabs.walletclient;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ee.avalanchelabs.wallet.proto.Empty;
import ee.avalanchelabs.wallet.proto.UserGrpc;
import ee.avalanchelabs.wallet.proto.WalletGrpc;
import ee.avalanchelabs.walletclient.properties.WalletCliProperties;
import ee.avalanchelabs.walletclient.round.Round;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletClientRunner implements ApplicationRunner {
    private final WalletCliProperties properties;
    private final ManagedChannel managedChannel;
    private final Supplier<Round> roundSupplier;

    @Override
    public void run(ApplicationArguments args) {
        UserGrpc.UserBlockingStub userBlockingStub = UserGrpc.newBlockingStub(managedChannel);
        Stream.generate(this::blockingStub)
                .limit(properties.getUsers())
                .parallel()
                .forEach(blockingStub -> {
                    String userId = userBlockingStub.create(Empty.newBuilder().build()).getId();
                    ExecutorService executor = executor(userId);
                    Stream.generate(roundSupplier)
                            .limit(properties.getRoundsPerThread())
                            .forEach(round -> executor.execute(() -> round.run(blockingStub, userId)));
                    executor.shutdown();
                });
    }

    private ExecutorService executor(String userId) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(false)
                .setNameFormat("user-" + userId + "-thread-%d")
                .setUncaughtExceptionHandler((t, e) -> log.error(t.getName(), e))
                .build();
        return Executors.newFixedThreadPool(properties.getConcurrentThreadsPerUser(), threadFactory);
    }

    private WalletGrpc.WalletBlockingStub blockingStub() {
        return WalletGrpc.newBlockingStub(managedChannel);
    }
}
