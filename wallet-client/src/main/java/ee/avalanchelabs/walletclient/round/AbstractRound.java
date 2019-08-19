package ee.avalanchelabs.walletclient.round;

import ee.avalanchelabs.wallet.proto.Currency;
import ee.avalanchelabs.wallet.proto.FundsRequest;
import ee.avalanchelabs.wallet.proto.UserDto;
import ee.avalanchelabs.wallet.proto.WalletGrpc;
import ee.avalanchelabs.walletclient.metric.RoundMetric;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@RequiredArgsConstructor
public abstract class AbstractRound implements Round {
    private final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
    private RoundMetric roundMetric;

    @Autowired
    public void setRoundMetric(RoundMetric roundMetric) {
        this.roundMetric = roundMetric;
    }

    protected void withdraw(WalletGrpc.WalletBlockingStub blockingStub, String userId, double amount, Currency currency) {
        FundsRequest request = FundsRequest.newBuilder()
                .setUserId(userId)
                .setAmount(new BigDecimal(amount).toString())
                .setCurrency(currency)
                .build();
        try {
            blockingStub.withdraw(request);
            log.info("User {} withdraw {} {} - OK", userId, currency, request.getAmount());
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            if (code == Status.Code.FAILED_PRECONDITION) {
                log.info("User {} withdraw {} {} - {}", userId, currency, request.getAmount(), "insufficient_funds");
            } else {
                log.info("User {} withdraw {} {} - {}", userId, currency, request.getAmount(), code);
                roundMetric.error();
            }
        }
        roundMetric.request(this.getClass());
    }

    protected void deposit(WalletGrpc.WalletBlockingStub blockingStub, String userId, double amount, Currency currency) {
        FundsRequest request = FundsRequest.newBuilder()
                .setUserId(userId)
                .setAmount(new BigDecimal(amount).toString())
                .setCurrency(currency)
                .build();
        try {
            blockingStub.deposit(request);
            log.info("User {} deposit {} {} - OK", userId, request.getCurrency(), request.getAmount());
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            log.info("User {} deposit {} {} - {}", userId, request.getCurrency(), request.getAmount(), code);
            roundMetric.error();
        }
        roundMetric.request(this.getClass());
    }

    protected void balance(WalletGrpc.WalletBlockingStub blockingStub, String userId) {
        UserDto userDto = UserDto.newBuilder()
                .setId(userId)
                .build();
        StringBuilder stringBuilder = new StringBuilder();
        blockingStub.balance(userDto).forEachRemaining(account -> stringBuilder
                .append(" ")
                .append(account.getCurrency())
                .append("=")
                .append(account.getAmount()));
        log.info("User {} balance [{} ]", userId, stringBuilder);
        roundMetric.request(this.getClass());
    }
}
