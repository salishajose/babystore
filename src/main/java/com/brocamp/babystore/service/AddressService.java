package com.brocamp.babystore.service;

import com.brocamp.babystore.dto.AddressDTO;
import com.brocamp.babystore.model.Address;
import com.brocamp.babystore.model.Users;
import com.brocamp.babystore.security.CustomUser;

import java.util.List;

public interface AddressService {
    List<Address> findAllByUsersId(long usersId);

    void saveAddress(AddressDTO addressDTO, Users users);

    void deleteById(long id);

    AddressDTO findById(long id);

    void updateAddress(AddressDTO addressDTO);
}
