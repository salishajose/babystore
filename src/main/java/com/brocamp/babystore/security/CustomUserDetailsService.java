package com.brocamp.babystore.security;

import com.brocamp.babystore.exception.UserBlockedException;
import com.brocamp.babystore.model.Users;
import com.brocamp.babystore.repository.UsersRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, UserBlockedException {
        Users users = usersRepository.findByEmail(username);
        System.out.println(users);
        if(users != null){
            if(users.isBlocked()){
                throw new UserBlockedException("Temporarily blocked this user..");
            }else {
                List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
                grantedAuthorityList.add(new SimpleGrantedAuthority(users.getRole()));
                return new CustomUser(users.getEmail(), users.getPassword(), grantedAuthorityList, users.getId(),
                        users.getFirstName(), users.getLastName(), users.getPhoneNumber(),
                        users.isDelete(), users.isBlocked(), users.isActive(),
                        users.getCreatedAt(), users.getUpdateOn());
            }
        }else{
             throw new UsernameNotFoundException("Invalid Username or password");
        }
    }
}
