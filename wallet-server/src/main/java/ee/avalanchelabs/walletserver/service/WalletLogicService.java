package ee.avalanchelabs.walletserver.service;

import ee.avalanchelabs.wallet.proto.AccountDto;
import ee.avalanchelabs.wallet.proto.FundsRequest;
import ee.avalanchelabs.wallet.proto.TransactionDto;
import ee.avalanchelabs.wallet.proto.UserDto;
import io.grpc.StatusRuntimeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Retryable;

import java.util.stream.Stream;

public interface WalletLogicService {

    @Retryable(value = DataIntegrityViolationException.class, maxAttempts = 2)
    TransactionDto deposit(FundsRequest request) throws StatusRuntimeException;

    TransactionDto withdraw(FundsRequest request) throws StatusRuntimeException;

    Stream<AccountDto> balance(UserDto userDto) throws StatusRuntimeException;
}
