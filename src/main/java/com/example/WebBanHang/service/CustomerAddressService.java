package com.example.WebBanHang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.CustomerAddress;
import com.example.WebBanHang.repository.CustomerAddressRepository;
@Service
public class CustomerAddressService {
    @Autowired
    private CustomerAddressRepository customerAddressRepository;
    public List<CustomerAddress> getCustomerAddresses(Integer customerId) {
        return customerAddressRepository.findByCustomerId(customerId);
    }  
    public ResponseEntity<ApiResponse<CustomerAddress>>  addCustomerAddress(CustomerAddress customerAddress) {
        try {
            return ResponseEntity.ok().body(
                new ApiResponse<>("SUCCESS", "Thêm địa chỉ thành công", customerAddressRepository.save(customerAddress))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Lỗi Server", null)
            );
        }
    } 
    public ResponseEntity<ApiResponse<CustomerAddress>>  updateCustomerAddress(CustomerAddress customerAddress) {
        try {
            return ResponseEntity.ok().body(
                new ApiResponse<>("SUCCESS", "Cập nhật địa chỉ thành công", customerAddressRepository.save(customerAddress))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Lỗi Server", null)
            );
        }
    }  
    public ResponseEntity<ApiResponse<CustomerAddress>>  deleteCustomerAddress(Integer addressId) {
        try {
            customerAddressRepository.deleteById(addressId);
            return ResponseEntity.ok().body(
                new ApiResponse<>("SUCCESS", "Xóa địa chỉ thành công", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Lỗi Server", null)
            );
        }
    }
    public List<CustomerAddress> getAllCustomerAddresses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllCustomerAddresses'");
    }   
    
    
}
