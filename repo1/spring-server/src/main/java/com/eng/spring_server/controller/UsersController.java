package com.eng.spring_server.controller;

import com.eng.spring_server.config.TokenRequest;
import com.eng.spring_server.domain.Users;
import com.eng.spring_server.dto.UserIdResponse;
import com.eng.spring_server.service.UsersService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody TokenRequest tokenRequest) {

        // idToken 추출
        String idToken = tokenRequest.getIdToken();

        try {
            // ID Token 검증
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String profile = decodedToken.getPicture();

            // DB Users 조회
            Optional<Users> userOpt = usersService.getUserByUid(uid);

            // DB 사용자 정보가 없다면 새로 생성
            if (userOpt.isEmpty()) {
                Users newUser = new Users();

                newUser.setUid(uid);
                newUser.setEmail(email);
                newUser.setNickname(name);
                newUser.setProfile(profile);

                Users users = usersService.saveUsers(newUser);
                Long userId = users.getId();

                // userId 응답
                UserIdResponse response = new UserIdResponse();
                response.setUserId(userId);

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            UserIdResponse response = new UserIdResponse();
            response.setUserId(userOpt.get().getId());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.info("User Identification Error : {}", e.getMessage());
            return ResponseEntity.status(401).body("Invalid ID Token");
        }
    }
}
