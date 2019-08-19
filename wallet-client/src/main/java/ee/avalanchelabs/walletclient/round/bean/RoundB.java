package ee.avalanchelabs.walletclient.round.bean;

import ee.avalanchelabs.wallet.proto.Currency;
import ee.avalanchelabs.wallet.proto.WalletGrpc;
import ee.avalanchelabs.walletclient.round.AbstractRound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoundB extends AbstractRound {

    @Override
    public void run(WalletGrpc.WalletBlockingStub blockingStub, String userId) {
        withdraw(blockingStub, userId, 100, Currency.GBP);
        deposit(blockingStub, userId, 300, Currency.GBP);
        withdraw(blockingStub, userId, 100, Currency.GBP);
        withdraw(blockingStub, userId, 100, Currency.GBP);
        withdraw(blockingStub, userId, 100, Currency.GBP);
    }
}
