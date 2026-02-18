package com.jewelryshop.service.impl;

import com.jewelryshop.dto.AddressRequest;
import com.jewelryshop.dto.AddressResponse;
import com.jewelryshop.entity.Address;
import com.jewelryshop.entity.User;
import com.jewelryshop.exception.ResourceNotFoundException;
import com.jewelryshop.repository.AddressRepository;
import com.jewelryshop.repository.UserRepository;
import com.jewelryshop.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        log.info("Creating address for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Address address = modelMapper.map(request, Address.class);
        address.setUser(user);

        if (request.getIsDefault()) {
            // Remove default flag from other addresses
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(defaultAddress -> {
                        defaultAddress.setIsDefault(false);
                        addressRepository.save(defaultAddress);
                    });
        }

        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully");

        return modelMapper.map(savedAddress, AddressResponse.class);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        log.info("Updating address: {}", addressId);

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found or unauthorized"));

        modelMapper.map(request, address);

        if (request.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(defaultAddress -> {
                        if (!defaultAddress.getId().equals(addressId)) {
                            defaultAddress.setIsDefault(false);
                            addressRepository.save(defaultAddress);
                        }
                    });
        }

        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated successfully");

        return modelMapper.map(updatedAddress, AddressResponse.class);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        log.info("Deleting address: {}", addressId);

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found or unauthorized"));

        addressRepository.delete(address);
        log.info("Address deleted successfully");
    }

    @Override
    public List<AddressResponse> getUserAddresses(Long userId) {
        log.info("Fetching addresses for user: {}", userId);

        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse getAddressById(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found or unauthorized"));

        return modelMapper.map(address, AddressResponse.class);
    }
}
