package ee.avalanchelabs.walletserver.validator.impl;

import ee.avalanchelabs.wallet.proto.Currency;
import ee.avalanchelabs.wallet.proto.FundsRequest;
import ee.avalanchelabs.walletserver.validator.MessageValidator;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class FundsRequestValidator implements MessageValidator<FundsRequest> {
    private final UserDtoValidator userDtoValidator;

    @Override
    public Class<FundsRequest> getSupportedClass() {
        return FundsRequest.class;
    }

    @Override
    public void validate(FundsRequest fundsRequest) {
        BigDecimal amount = new BigDecimal(fundsRequest.getAmount());
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw Status.INVALID_ARGUMENT.withDescription("amount must be positive").asRuntimeException();
        }
        if (fundsRequest.getCurrency() == Currency.UNRECOGNIZED) {
            throw Status.INVALID_ARGUMENT.withDescription("currency can't be unrecognized").asRuntimeException();
        }
        userDtoValidator.validateUserId(fundsRequest.getUserId());
    }
}
