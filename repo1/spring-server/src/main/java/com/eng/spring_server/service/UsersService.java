package com.eng.spring_server.service;

import com.eng.spring_server.domain.Users;
import com.eng.spring_server.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    // Firebase UID -> Users 엔티티 반환
    @Transactional(readOnly = true)
    public Optional<Users> getUserByUid(String uid) {
        return usersRepository.findByUid(uid);
    }

    // Users 데이터 저장 서비스
    public Users saveUsers(Users users) {
        return usersRepository.save(users);
    }
}
