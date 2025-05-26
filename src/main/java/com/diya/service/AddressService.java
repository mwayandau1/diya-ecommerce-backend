
package com.diya.service;


import com.diya.dto.request.AddressRequest;
import com.diya.dto.response.AddressResponse;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.AddressMapper;
import com.diya.model.Address;
import com.diya.model.User;
import com.diya.repository.AddressRepository;
import com.diya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    public List<AddressResponse> getUserAddresses(String username) {
        User user = findUserByEmail(username);
        return addressRepository.findByUser(user).stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse getAddressById(String username, Long addressId) {
        User user = findUserByEmail(username);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        
        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address does not belong to the user");
        }
        
        return addressMapper.toAddressResponse(address);
    }

    @Transactional
    public AddressResponse createAddress(String username, AddressRequest addressRequest) {
        User user = findUserByEmail(username);
        
        Address address = addressMapper.toAddress(addressRequest);
        address.setUser(user);
        
        // If this is the first address or if isDefault is true, make it the default
        boolean isFirstAddress = addressRepository.findByUser(user).isEmpty();
        if (isFirstAddress || addressRequest.isDefault()) {
            address.setDefault(true);
            addressRepository.unsetDefaultAddresses(user, null);
        }
        
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toAddressResponse(savedAddress);
    }

    @Transactional
    public AddressResponse updateAddress(String username, Long addressId, AddressRequest addressRequest) {
        User user = findUserByEmail(username);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        
        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address does not belong to the user");
        }
        
        addressMapper.updateAddressFromRequest(addressRequest, address);
        
        if (addressRequest.isDefault()) {
            addressRepository.unsetDefaultAddresses(user, addressId);
            address.setDefault(true);
        }
        
        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toAddressResponse(updatedAddress);
    }

    @Transactional
    public void deleteAddress(String username, Long addressId) {
        User user = findUserByEmail(username);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        
        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address does not belong to the user");
        }
        
        // If this was the default address, find another to make default
        if (address.isDefault()) {
            addressRepository.delete(address);
            
            addressRepository.findByUser(user).stream()
                    .findFirst()
                    .ifPresent(newDefault -> {
                        newDefault.setDefault(true);
                        addressRepository.save(newDefault);
                    });
        } else {
            addressRepository.delete(address);
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
