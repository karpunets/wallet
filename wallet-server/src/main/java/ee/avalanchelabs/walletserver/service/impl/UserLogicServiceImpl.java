package ee.avalanchelabs.walletserver.service.impl;

import ee.avalanchelabs.wallet.proto.UserDto;
import ee.avalanchelabs.walletserver.mapper.UserMapper;
import ee.avalanchelabs.walletserver.model.entity.User;
import ee.avalanchelabs.walletserver.repository.UserRepository;
import ee.avalanchelabs.walletserver.service.UserLogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLogicServiceImpl implements UserLogicService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findUserDto(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional
    public UserDto createUser() {
        User user = userRepository.save(new User());
        return userMapper.toDto(user);
    }
}
