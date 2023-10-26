package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.dto.AddressDTO;
import com.brocamp.babystore.exception.AddressNotFoundException;
import com.brocamp.babystore.model.Address;
import com.brocamp.babystore.model.Users;
import com.brocamp.babystore.repository.AddressRepository;
import com.brocamp.babystore.security.CustomUser;
import com.brocamp.babystore.service.AddressService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {
    private AddressRepository addressRepository;
    @Override
    public List<Address> findAllByUsersId(long usersId) {

        return addressRepository.findAllByUsersId(usersId).orElse(new ArrayList<Address>());
    }

    @Override
    public void saveAddress(AddressDTO addressDTO, Users users) {
        Address address = new Address();
        address.setRecipientName(addressDTO.getRecipientName());
        address.setHouseDetails(addressDTO.getHouseDetails());
        address.setStreetAddress(addressDTO.getStreetAddress());
        address.setLandmark(addressDTO.getLandmark());
        address.setCity(addressDTO.getCity());
        address.setPinCode(addressDTO.getPinCode());
        address.setPhoneNumber(addressDTO.getPhoneNumber());
        address.setAlternatePhoneNUmber(addressDTO.getAlternatePhoneNUmber());
        address.setState(addressDTO.getState());
        address.setTypeOfAddress(addressDTO.getTypeOfAddress());
        address.setUsers(users);
        addressRepository.save(address);
    }

    @Override
    public void deleteById(long id) {
        Optional<Address> optionalAddress = addressRepository.findById(id);
        Address address = optionalAddress.orElse(null);
        if(address!=null){
            address.setDeleted(true);
            addressRepository.save(address);
        }
    }

    @Override
    public AddressDTO findById(long id) {
        Optional<Address> optionalAddress = addressRepository.findById(id);
        Address address = optionalAddress.orElseThrow(()->new AddressNotFoundException("no address found with id : "+id));
        AddressDTO addressDTO = new AddressDTO();
        if(address!=null){
            addressDTO.setId(address.getId());
            addressDTO.setRecipientName(address.getRecipientName());
            addressDTO.setHouseDetails(address.getHouseDetails());
            addressDTO.setStreetAddress(address.getStreetAddress());
            addressDTO.setLandmark(address.getLandmark());
            addressDTO.setCity(address.getCity());
            addressDTO.setPinCode(address.getPinCode());
            addressDTO.setPhoneNumber(address.getPhoneNumber());
            addressDTO.setAlternatePhoneNUmber(address.getAlternatePhoneNUmber());
            addressDTO.setTypeOfAddress(address.getTypeOfAddress());
            addressDTO.setState(address.getState());
        }
        return addressDTO;
    }

    @Override
    public void updateAddress(AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressDTO.getId()).orElseThrow(
                ()->new AddressNotFoundException("No address found with this id : "+addressDTO.getId()));
        if(address!=null){
            address.setRecipientName(addressDTO.getRecipientName());
            address.setHouseDetails(addressDTO.getHouseDetails());
            address.setStreetAddress(addressDTO.getStreetAddress());
            address.setLandmark(addressDTO.getLandmark());
            address.setCity(addressDTO.getCity());
            address.setPinCode(addressDTO.getPinCode());
            address.setPhoneNumber(addressDTO.getPhoneNumber());
            address.setAlternatePhoneNUmber(addressDTO.getAlternatePhoneNUmber());
            address.setTypeOfAddress(addressDTO.getTypeOfAddress());
            address.setState(addressDTO.getState());
            addressRepository.save(address);
        }
    }
}
