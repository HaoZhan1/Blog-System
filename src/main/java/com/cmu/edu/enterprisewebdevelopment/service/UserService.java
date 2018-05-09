package com.cmu.edu.enterprisewebdevelopment.service;

import com.cmu.edu.enterprisewebdevelopment.domain.Role;
import com.cmu.edu.enterprisewebdevelopment.domain.User;
import com.cmu.edu.enterprisewebdevelopment.repository.RoleRepository;
import com.cmu.edu.enterprisewebdevelopment.repository.UserRepository;
import org.omg.SendingContext.RunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private BCryptPasswordEncoder encoder;

    public void createUser(User newUser)   {
        //validate if userName exists
        User user = userRepository.findUserByUsername(newUser.getUsername());
        if (user != null) {
            throw new RuntimeException("error.username.exist");
        }
        //add Role
        String user_role = "customer";
        Role role = roleRepository.findRoleByName(user_role);
        if (role == null) {
            role = new Role(user_role);
        }
        List<Role> roles = new ArrayList<>();
        roles.add(role);
         //add Password
        newUser.setPassword(encoder.encode(newUser.getPassword()));
        newUser.setRoles(roles);
        //save user
        userRepository.save(newUser);
    }

    public User findUserByUserName(String userName) {
        return userRepository.findUserByUsername(userName);
    }



    //security
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (s == null || s.isEmpty()) {
            throw new UsernameNotFoundException("username cannot be empty");
        }
        User user = userRepository.findUserByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("User doestn't exist");
        }
        List<Role> roles = user.getRoles();
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Role role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                grantedAuthorities
        );
    }
}
