package ee.avalanchelabs.walletserver.service.impl;

import ee.avalanchelabs.wallet.proto.Empty;
import ee.avalanchelabs.wallet.proto.UserDto;
import ee.avalanchelabs.wallet.proto.UserGrpc;
import ee.avalanchelabs.walletserver.service.ServiceGrpc;
import ee.avalanchelabs.walletserver.service.UserLogicService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceGrpc extends UserGrpc.UserImplBase implements ServiceGrpc {
    private final UserLogicService userLogicService;

    @Override
    public void create(Empty request, StreamObserver<UserDto> responseObserver) {
        handleException(responseObserver, () -> {
            UserDto user = userLogicService.createUser();
            responseObserver.onNext(user);
            log.info("new user created {}", user);
            responseObserver.onCompleted();
        });
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
