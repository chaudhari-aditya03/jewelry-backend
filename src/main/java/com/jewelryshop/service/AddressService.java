package com.jewelryshop.service;

import com.jewelryshop.dto.AddressRequest;
import com.jewelryshop.dto.AddressResponse;

import java.util.List;

public interface AddressService {
    AddressResponse createAddress(Long userId, AddressRequest request);
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    List<AddressResponse> getUserAddresses(Long userId);
    AddressResponse getAddressById(Long userId, Long addressId);
}
