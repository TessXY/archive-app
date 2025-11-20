package it.personal.archive.app.domain.service;

import it.personal.archive.app.domain.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public UserEntity login(String email, String password) {
        // MOCK DI TEST — sempre login valido se email == password
        if (email != null && email.equals(password)) {
            UserEntity user = new UserEntity();
            user.setEmail(email);
            user.setUsername("giada");
            return user;
        }
        return null;
    }
}
