package ee.avalanchelabs.walletserver.config;

import ee.avalanchelabs.walletserver.properties.WalletServerProperties;
import ee.avalanchelabs.walletserver.validator.GrpcValidationInterceptor;
import ee.avalanchelabs.walletserver.validator.MessageValidator;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@EnableConfigurationProperties(WalletServerProperties.class)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "wallet.server.active", matchIfMissing = true)
@Slf4j
public class WalletServerConfiguration {

    @Bean
    public Server server(WalletServerProperties properties, List<BindableService> bindableServices,
                         List<MessageValidator<?>> validators) {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(properties.getPort());
        bindableServices.forEach(serverBuilder::addService);
        serverBuilder.intercept(new GrpcValidationInterceptor(validators));
        Server server = serverBuilder.build();
        log.info("Wallet server created.");
        return server;
    }

    @Bean
    public ApplicationRunner serverRunner(Server server) {
        return args -> {
            server.start();
            log.info("Wallet server started and listening on port {}.", server.getPort());
            startDaemonAwaitThread(server);
        };
    }

    @Bean
    public DisposableBean serverDisposable(Server server) {
        return () -> {
            server.shutdown();
            log.info("Wallet server stopped.");
        };
    }

    private void startDaemonAwaitThread(Server server) {
        Thread awaitThread = new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                log.error("Wallet server stopped.", e);
            }
        });
        awaitThread.setDaemon(false);
        awaitThread.start();
    }
}
