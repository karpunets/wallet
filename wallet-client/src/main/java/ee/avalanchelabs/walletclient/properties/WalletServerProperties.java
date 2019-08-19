package ee.avalanchelabs.walletclient.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("wallet.server")
@Data
public class WalletServerProperties {
    public String host = "localhost";
    public Integer port = 9090;
}
