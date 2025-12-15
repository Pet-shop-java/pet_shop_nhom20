package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.config.JwtTokenProvider;
import com.webpet_nhom20.backdend.dto.request.Address.AddressRequest;
import com.webpet_nhom20.backdend.dto.request.Address.UpdateAddressRequest;
import com.webpet_nhom20.backdend.dto.response.Address.AddressResponse;
import com.webpet_nhom20.backdend.entity.Addresses;
import com.webpet_nhom20.backdend.entity.User;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.mapper.AddressMapper;
import com.webpet_nhom20.backdend.repository.AddressRepository;
import com.webpet_nhom20.backdend.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AddressMapper addressMapper;
    @Override
    public Page<AddressResponse> getAddressById(String token, Pageable pageable) {
        Integer userIdFromToken = jwtTokenProvider.getUserId(token);

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Order.desc("isDefault"),
                        Sort.Order.desc("createdDate")
                )
        );

        Page<Addresses> addressPage = addressRepository.findAllByUserIdAndIsDeleted(userIdFromToken, "0", sortedPageable);


        Page<AddressResponse> responsePage = addressPage.map(addressMapper::toResponse);

        return responsePage;

    }

    @Override
    public AddressResponse createAddress(String token, AddressRequest request) {
        Integer userId = jwtTokenProvider.getUserId(token);
        User user = new User();
        user.setId(userId);

        Addresses newAddress = addressMapper.toEntity(request);
        newAddress.setUser(user);

        List<Addresses> userAddresses = addressRepository.findByUserId(userId);

        if (userAddresses.isEmpty()) {
            newAddress.setIsDefault("1");
        }else if ("1".equals(request.getIsDefault())) {
            // Nếu user đã có địa chỉ mặc định mà request này muốn set mặc định
            for (Addresses addr : userAddresses) {
                if ("1".equals(addr.getIsDefault())) {
                    addr.setIsDefault("0");
                }
            }
            addressRepository.saveAll(userAddresses);
            newAddress.setIsDefault("1");
        }
        else {
            newAddress.setIsDefault("0");
        }

        Addresses savedAddress = addressRepository.save(newAddress);

        return addressMapper.toResponse(savedAddress);
    }

    @Override
    public AddressResponse updateAddress(String token, UpdateAddressRequest request) {
        Integer userId = jwtTokenProvider.getUserId(token);

        Addresses address = addressRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (address.getUser().getId() != (userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if ("1".equals(address.getIsDefault()) && "1".equals(request.getIsDeleted())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_DEFAULT_ADDRESS);
        }
        address.setContactName(request.getContactName());
        address.setPhone(request.getPhone());
        address.setDetailAddress(request.getDetailAddress());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setWard(request.getWard());
        address.setIsDefault(request.getIsDefault());
        address.setUpdatedDate(new Date());
        address.setIsDeleted(request.getIsDeleted());
        // Nếu frontend gửi isDefault = "1"
        if ("1".equals(request.getIsDefault())) {
            // Reset các address khác của user về 0
            addressRepository.findByUserIdAndIsDeleted(userId, "0")
                    .forEach(addr -> {
                        if (addr.getId() !=(address.getId()) && "1".equals(addr.getIsDefault())) {
                            addr.setIsDefault("0");
                            addr.setUpdatedDate(new Date());
                            addressRepository.save(addr);
                        }
                    });
            // Cập nhật cái đang sửa là mặc định
            address.setIsDefault("1");
        }
        Addresses updatedAddress = addressRepository.save(address);
        return addressMapper.toResponse(updatedAddress);
    }

    @Override
    public void deleteAddress(String token, Integer addressId) {

    }
}
