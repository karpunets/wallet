package ee.avalanchelabs.walletserver.service;

import ee.avalanchelabs.wallet.proto.UserDto;
import io.grpc.StatusRuntimeException;

import java.util.Optional;
import java.util.UUID;

public interface UserLogicService {

    Optional<UserDto> findUserDto(UUID id);

    UserDto createUser() throws StatusRuntimeException;
}
