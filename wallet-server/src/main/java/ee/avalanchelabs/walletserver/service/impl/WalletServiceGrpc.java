package ee.avalanchelabs.walletserver.service.impl;

import ee.avalanchelabs.wallet.proto.*;
import ee.avalanchelabs.walletserver.service.ServiceGrpc;
import ee.avalanchelabs.walletserver.service.WalletLogicService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceGrpc extends WalletGrpc.WalletImplBase implements ServiceGrpc {
    private final WalletLogicService walletLogicService;

    @Override
    public void deposit(FundsRequest request, StreamObserver<TransactionDto> responseObserver) {
        handleException(responseObserver, () -> {
            TransactionDto transactionDto = walletLogicService.deposit(request);
            log.debug("success deposit {} {} for {}", request.getCurrency(), request.getAmount(), request.getUserId());
            responseObserver.onNext(transactionDto);
            responseObserver.onCompleted();
        });
    }

    @Override
    public void withdraw(FundsRequest request, StreamObserver<TransactionDto> responseObserver) {
        handleException(responseObserver, () -> {
            TransactionDto transactionDto = walletLogicService.withdraw(request);
            log.debug("success withdraw {} {} for {}", request.getCurrency(), request.getAmount(), request.getUserId());
            responseObserver.onNext(transactionDto);
            responseObserver.onCompleted();
        });
    }

    @Override
    public void balance(UserDto request, StreamObserver<AccountDto> responseObserver) {
        handleException(responseObserver, () -> {
            walletLogicService.balance(request).forEach(responseObserver::onNext);
            log.debug("user with id {} get balance", request.getId());
            responseObserver.onCompleted();
        });
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
