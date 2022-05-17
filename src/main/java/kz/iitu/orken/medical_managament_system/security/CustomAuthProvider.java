package kz.iitu.orken.medical_managament_system.security;

import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class CustomAuthProvider implements AuthenticationProvider {

    @Resource
    UserDetailsService userDetailsService;

    private UserRepository repository;

    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        final String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
        if (StringUtils.isBlank(username)) {
            throw new BadCredentialsException("invalid login");
        }
        UserDetails user = null;
        try {
            user = userDetailsService.loadUserByUsername(username);

        } catch (UsernameNotFoundException exception) {
            throw new BadCredentialsException("invalid login");
        }

        return new UsernamePasswordAuthenticationToken(username, authentication.getCredentials(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
