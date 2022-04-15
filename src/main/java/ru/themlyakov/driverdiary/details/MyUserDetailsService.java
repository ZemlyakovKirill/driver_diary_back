package ru.themlyakov.driverdiary.details;

import ru.themlyakov.driverdiary.entities.User;
import ru.themlyakov.driverdiary.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userService.findByUsername(username);
        return userEntity!=null?MyUserDetails.fromUserEntityToUserDetails(userEntity):null;
    }
}
