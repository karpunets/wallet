package ee.avalanchelabs.walletserver.validator.impl;

import ee.avalanchelabs.wallet.proto.Currency;
import ee.avalanchelabs.wallet.proto.FundsRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@RunWith(SpringRunner.class)
public class FundsRequestValidatorTest {
    @Mock
    private UserDtoValidator userDtoValidator;
    @InjectMocks
    private FundsRequestValidator validator;

    @Test(expected = StatusRuntimeException.class)
    public void validateFailWhenAmountNegative() {
        FundsRequest request = FundsRequest.newBuilder()
                .setAmount("-20")
                .setCurrency(Currency.EUR)
                .setUserId(UUID.randomUUID().toString())
                .build();
        validator.validate(request);
    }

    @Test(expected = StatusRuntimeException.class)
    public void validateFailWhenUserIdValidationVail() {
        doThrow(Status.INVALID_ARGUMENT.asRuntimeException()).when(userDtoValidator).validateUserId(any());
        FundsRequest request = FundsRequest.newBuilder()
                .setAmount("20")
                .setCurrency(Currency.USD)
                .setUserId(UUID.randomUUID().toString())
                .build();
        validator.validate(request);
    }
}