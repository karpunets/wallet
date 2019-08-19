package ee.avalanchelabs.walletclient.round;

import ee.avalanchelabs.wallet.proto.WalletGrpc;

@FunctionalInterface
public interface Round {

    void run(WalletGrpc.WalletBlockingStub blockingStub, String userId);
}
