
package com.diya.mapper;

import com.diya.dto.request.AddressRequest;
import com.diya.dto.response.AddressResponse;
import com.diya.model.Address;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AddressMapper {

    public AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phone(address.getPhone())
                .isDefault(address.isDefault())
                .type(address.getType())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }

    public Address toAddress(AddressRequest addressRequest) {
        Address address = new Address();
        updateAddressFromRequest(addressRequest, address);
        return address;
    }

    public void updateAddressFromRequest(AddressRequest request, Address address) {
        address.setFullName(request.getFullName());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setPhone(request.getPhone());
        address.setDefault(request.isDefault());
        address.setType(request.getType());
        
        if (address.getCreatedAt() == null) {
            address.setCreatedAt(LocalDateTime.now());
        }
        address.setUpdatedAt(LocalDateTime.now());
    }
}
