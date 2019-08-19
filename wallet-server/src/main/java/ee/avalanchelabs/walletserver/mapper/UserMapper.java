package ee.avalanchelabs.walletserver.mapper;

import ee.avalanchelabs.wallet.proto.UserDto;
import ee.avalanchelabs.walletserver.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(uses = GeneralMapper.class)
public interface UserMapper {

    UserDto toDto(User user);
}
