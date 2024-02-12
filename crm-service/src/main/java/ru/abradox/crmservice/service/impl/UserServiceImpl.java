package ru.abradox.crmservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abradox.crmservice.entity.UserEntity;
import ru.abradox.crmservice.repository.UserRepository;
import ru.abradox.crmservice.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<Integer> getBlockedUserIds() {
        return userRepository.findAllByBlockedIsTrue()
                .stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());
    }
}
