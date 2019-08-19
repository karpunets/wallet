package ee.avalanchelabs.walletserver.validator.impl;

import ee.avalanchelabs.wallet.proto.UserDto;
import ee.avalanchelabs.walletserver.service.UserLogicService;
import io.grpc.StatusRuntimeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserDtoValidatorTest {
    @Mock
    private UserLogicService userLogicService;
    @InjectMocks
    private UserDtoValidator validator;

    @Test(expected = StatusRuntimeException.class)
    public void validateFailWhenUserNotFount() {
        UUID uuid = UUID.randomUUID();
        UserDto userDto = UserDto.newBuilder().setId(uuid.toString()).build();
        when(userLogicService.findUserDto(uuid)).thenReturn(Optional.empty());
        validator.validate(userDto);
    }

    @Test(expected = StatusRuntimeException.class)
    public void validateFailWhenUserIdNotUUID() {
        UserDto userDto = UserDto.newBuilder().setId("not uuid").build();
        validator.validate(userDto);
    }
}