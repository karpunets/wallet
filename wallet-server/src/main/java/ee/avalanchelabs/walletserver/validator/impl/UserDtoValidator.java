package ee.avalanchelabs.walletserver.validator.impl;

import ee.avalanchelabs.wallet.proto.UserDto;
import ee.avalanchelabs.walletserver.service.UserLogicService;
import ee.avalanchelabs.walletserver.validator.MessageValidator;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDtoValidator implements MessageValidator<UserDto> {
    private final UserLogicService userLogicService;

    @Override
    public Class<UserDto> getSupportedClass() {
        return UserDto.class;
    }

    @Override
    public void validate(UserDto userDto) {
        validateUserId(userDto.getId());
    }

    void validateUserId(String idStr) {
        UUID id;
        try {
            id = UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            throw Status.INVALID_ARGUMENT.withDescription("user id must be as uuid").asRuntimeException();
        }
        if (!userLogicService.findUserDto(id).isPresent()) {
            throw Status.NOT_FOUND.withDescription("user not found").asRuntimeException();
        }
    }
}
