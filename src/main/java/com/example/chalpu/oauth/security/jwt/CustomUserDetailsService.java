package com.example.chalpu.oauth.security.jwt;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.UserException;
import com.example.chalpu.user.domain.User;
import com.example.chalpu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new UserException(ErrorMessage.USER_NOT_FOUND));

        return UserDetailsImpl.build(user);
    }
}
