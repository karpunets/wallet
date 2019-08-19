package ee.avalanchelabs.walletclient.round.bean;

import ee.avalanchelabs.wallet.proto.Currency;
import ee.avalanchelabs.wallet.proto.WalletGrpc;
import ee.avalanchelabs.walletclient.round.AbstractRound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoundC extends AbstractRound {

    @Override
    public void run(WalletGrpc.WalletBlockingStub blockingStub, String userId) {
        balance(blockingStub, userId);
        deposit(blockingStub, userId, 100, Currency.USD);
        deposit(blockingStub, userId, 100, Currency.USD);
        withdraw(blockingStub, userId, 100, Currency.USD);
        deposit(blockingStub, userId, 100, Currency.USD);
        balance(blockingStub, userId);
        withdraw(blockingStub, userId, 200, Currency.USD);
    }
}
