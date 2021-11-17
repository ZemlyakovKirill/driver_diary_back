package com.example.workingwithtokens.details;

import com.example.workingwithtokens.entities.User;
import com.example.workingwithtokens.services.UserService;
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
