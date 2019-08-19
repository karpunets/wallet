package ee.avalanchelabs.walletserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class WalletServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletServerApplication.class, args);
    }
}
