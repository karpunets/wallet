package ee.avalanchelabs.walletserver.service.impl;

import ee.avalanchelabs.wallet.proto.*;
import ee.avalanchelabs.walletserver.mapper.WalletMapper;
import ee.avalanchelabs.walletserver.model.AccountCurrency;
import ee.avalanchelabs.walletserver.model.entity.Account;
import ee.avalanchelabs.walletserver.model.entity.User;
import ee.avalanchelabs.walletserver.repository.AccountRepository;
import ee.avalanchelabs.walletserver.repository.TransactionRepository;
import ee.avalanchelabs.walletserver.repository.UserRepository;
import ee.avalanchelabs.walletserver.service.WalletLogicService;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static ee.avalanchelabs.walletserver.model.OperationType.DEPOSIT;
import static ee.avalanchelabs.walletserver.model.OperationType.WITHDRAWAL;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletLogicServiceImpl implements WalletLogicService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;

    @Override
    @Transactional
    public TransactionDto deposit(FundsRequest request) {
        AccountCurrency currency = AccountCurrency.valueOf(request.getCurrency().name());
        User user = findUser(request.getUserId()).orElseThrow(Status.NOT_FOUND::asRuntimeException);
        BigDecimal depositAmount = walletMapper.amountToBigDecimal(request);
        Account account = accountRepository.findByUserAndCurrency(user, currency)
                .map(existedAccount -> {
                    existedAccount.add(depositAmount);
                    accountRepository.save(existedAccount);
                    return existedAccount;
                }).orElseGet(() -> accountRepository.save(user.newAccount(currency, depositAmount)));
        return walletMapper.toDto(transactionRepository.save(account.newTransaction(DEPOSIT, depositAmount)));
    }

    @Override
    @Transactional
    public TransactionDto withdraw(FundsRequest request) {
        AccountCurrency currency = AccountCurrency.valueOf(request.getCurrency().name());
        User user = findUser(request.getUserId()).orElseThrow(Status.NOT_FOUND::asRuntimeException);
        Account account = accountRepository.findByUserAndCurrency(user, currency)
                .orElseThrow(Status.FAILED_PRECONDITION::asRuntimeException);
        BigDecimal withdrawAmount = walletMapper.amountToBigDecimal(request);
        if (account.getAmount().subtract(withdrawAmount).compareTo(BigDecimal.ZERO) < 0) {
            throw Status.FAILED_PRECONDITION.asRuntimeException();
        }
        account.subtract(withdrawAmount);
        accountRepository.save(account);
        return walletMapper.toDto(transactionRepository.save(account.newTransaction(WITHDRAWAL, withdrawAmount)));
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<AccountDto> balance(UserDto userDto) {
        User user = findUser(userDto.getId()).orElseThrow(Status.NOT_FOUND::asRuntimeException);
        return accountRepository.findAllByUser(user).stream()
                .map(account -> AccountDto.newBuilder()
                        .setCurrency(Currency.valueOf(account.getCurrency().name()))
                        .setAmount(account.getAmount().toString())
                        .build());
    }

    private Optional<User> findUser(String id) {
        UUID userId = UUID.fromString(id);
        return userRepository.findById(userId);
    }
}
