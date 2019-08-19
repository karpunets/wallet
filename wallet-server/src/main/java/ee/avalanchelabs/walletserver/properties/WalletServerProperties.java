package ee.avalanchelabs.walletserver.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("wallet.server")
@Data
public class WalletServerProperties {
    public Boolean active = true;
    public Integer port = 9090;
}
