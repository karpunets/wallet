package ee.avalanchelabs.walletclient;

import ee.avalanchelabs.walletclient.properties.WalletCliProperties;
import ee.avalanchelabs.walletclient.properties.WalletServerProperties;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({WalletServerProperties.class, WalletCliProperties.class})
public class WalletClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletClientApplication.class, args);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel channel(WalletServerProperties properties) {
        return ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort())
                .usePlaintext()
                .build();
    }
}
