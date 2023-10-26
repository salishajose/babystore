package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.dto.PasswordDTO;
import com.brocamp.babystore.dto.UsersDTO;
import com.brocamp.babystore.model.Users;
import com.brocamp.babystore.repository.UsersRepository;
import com.brocamp.babystore.service.UsersSevice;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsersSeviceImpl implements UsersSevice {

    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsService;

    public UsersSeviceImpl(UsersRepository usersRepository,
                           PasswordEncoder passwordEncoder,
                           UserDetailsService userDetailsService) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean findAlreadyExistUserByEmail(String email) {

        return usersRepository.existsByEmail(email);
    }

    @Override
    public void saveOrUpdate(Users newUser) {
        usersRepository.save(newUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    @Override
    public List<Users> findAll() {
        Optional<List<Users>> optionalUsersList = Optional.ofNullable(usersRepository.findAll());
        return optionalUsersList.orElse(new ArrayList<Users>());
    }

    @Override
    public boolean existById(long id) {
        return usersRepository.existsById(id);
    }

    @Override
    public Users findById(long id) {
        Optional<Users> optionalUsers = Optional.ofNullable(usersRepository.findById(id));
        return optionalUsers.orElse(new Users());
    }

    @Override
    public void updateUserProfile(UsersDTO usersDTO) {
        Users users = usersRepository.findById(usersDTO.getId());
        users.setFirstName(usersDTO.getFirstName());
        users.setLastName(usersDTO.getLastName());
        users.setPhoneNumber(users.getPhoneNumber());
        users.setUpdateOn(new Date());
        usersRepository.save(users);
    }

    @Override
    public void changePassword(PasswordDTO passwordDTO) {
        Users users = usersRepository.findById(passwordDTO.getId());
        users.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
        users.setUpdateOn(new Date());
        usersRepository.save(users);
    }

    @Override
    public Page<Users> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        return usersRepository.findAll(pageable);
    }


}
