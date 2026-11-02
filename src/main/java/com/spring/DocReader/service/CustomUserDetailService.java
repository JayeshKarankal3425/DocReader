package com.spring.DocReader.service;

import com.spring.DocReader.entity.User;
import com.spring.DocReader.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {


    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//        This must be the own implementation

        Optional<User> user = userRepository.findByUsername(username);
        return new CustomUserDetail(
                user.orElseThrow(() -> new UsernameNotFoundException("Invalid username or password")));

    }
}
