package com.example.workingwithtokens.services;

import com.example.workingwithtokens.entities.Authority;
import com.example.workingwithtokens.entities.User;
import com.example.workingwithtokens.entities.Vehicle;
import com.example.workingwithtokens.repositories.AuthorityRepository;
import com.example.workingwithtokens.repositories.UserRepository;
import com.example.workingwithtokens.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private VehicleRepository vehicleRepository;


    public User saveUser(String username,String password,String email,String lastName,String firstName,String phone) {
        Set<Authority> authorities=new HashSet<>(Collections.singletonList(authorRepository.findAuthoritiesByAuthority("ROLE_USER")));

        User userEntity=new User(
                username,
                passwordEncoder().encode(password),
                true,
                email,
                phone,
                firstName,
                lastName,
                false,
                false,
                authorities);
        return userRepository.save(userEntity);
    }
    public User saveUserVk(String username,String email,String lastName,String firstName) {
        Set<Authority> authorities=new HashSet<>(Collections.singletonList(authorRepository.findAuthoritiesByAuthority("ROLE_USER")));

        User userEntity=new User(
                username,
                UUID.randomUUID().toString(),
                true,
                email,
                firstName,
                lastName,
                true,
                false,
                authorities);
        return userRepository.save(userEntity);
    }
    public User saveUserGoogle(String username,String email,String lastName,String firstName) {
        Set<Authority> authorities=new HashSet<>(Collections.singletonList(authorRepository.findAuthoritiesByAuthority("ROLE_USER")));

        User userEntity=new User(
                username,
                UUID.randomUUID().toString(),
                true,
                email,
                firstName,
                lastName,
                false,
                true,
                authorities);
        return userRepository.save(userEntity);
    }
    public User findByUsernameWithoutGoogle(String username) {
        User user = userRepository.findUserByUsername(username);
        if(user!=null&&!user.getGoogle())
            return user;
        return null;
    }
    public User findByUsernameWithoutVk(String username) {
        User user = userRepository.findUserByUsername(username);
        if(user!=null&&!user.getVk())
            return user;
        return null;
    }
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }
    public User findByUsernameWithoutGoogleVk(String username) {
        User user = userRepository.findUserByUsername(username);
        if(user!=null&&!user.getGoogle()&&!user.getVk())
            return user;
        return null;
    }

    public Iterable<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    public User findByUsernameAndPassword(String username, String password) {
        User userEntity = findByUsernameWithoutGoogleVk(username);
        if (userEntity != null) {
            if (passwordEncoder().matches(password, userEntity.getPassword())) {
                return userEntity;
            }
        }
        return null;
    }
    public Long getNewId(){
        return userRepository.getNewId();
    }
}
