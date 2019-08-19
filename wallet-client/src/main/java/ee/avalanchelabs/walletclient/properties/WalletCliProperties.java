package ee.avalanchelabs.walletclient.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("wallet.cli")
@Data
public class WalletCliProperties {
    /**
     * Number of concurrent users emulated.
     */
    public Integer users = 1;
    /**
     * Number of concurrent requests a user will make.
     */
    public Integer concurrentThreadsPerUser = 1;
    /**
     * Number of rounds each thread is executing.
     */
    public Integer roundsPerThread = 1;
}
