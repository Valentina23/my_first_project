package com.uploader.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    private Logger log = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    public CustomAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (isCredentialsCorrect(username, password)) {
            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(username, password, grantedAuths);
        }

        throw new BadCredentialsException("1000");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }

    public UserEntity findUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }

    public boolean isCredentialsCorrect(String login, String password) {
        UserEntity user = findUserByLogin(login);

        if (user == null) {
            throw new BadCredentialsException("1000");
        }

        String convertedPassword = convertToMD5(password);

        return user.getPassword().equals(convertedPassword);
    }

    private String convertToMD5(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(StandardCharsets.UTF_8.encode(str));
            return String.format("%032x", new BigInteger(1, digest.digest()));
        } catch (NoSuchAlgorithmException e) {
            log.error("Unable to convert to MD5", e);

            e.getMessage();
        }

        throw new IllegalArgumentException();
    }
}
