package com.example.WebBanHang.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.CustomerAddress;
import com.example.WebBanHang.service.CustomerAddressService;

@RestController
@RequestMapping("/customer-address") 
public class CustomerAddressController {
    @Autowired
    private CustomerAddressService customerAddressService;

    @GetMapping("/{id}")
    public List<CustomerAddress> getCustomerAddresses(@PathVariable("id") Integer id) {      
        return customerAddressService.getCustomerAddresses(id);
    }

    @PostMapping("/add") 
    public ResponseEntity<ApiResponse<CustomerAddress>> addCustomerAddress(@RequestBody CustomerAddress customerAddress) {  
        return customerAddressService.addCustomerAddress(customerAddress);
    }  

    @PostMapping("/update") 
    public ResponseEntity<ApiResponse<CustomerAddress>> updateCustomerAddress(@RequestBody CustomerAddress customerAddress) {  
        return customerAddressService.updateCustomerAddress(customerAddress);
    }  

    @PostMapping("/delete") 
    public ResponseEntity<ApiResponse<CustomerAddress>> deleteCustomerAddress(@RequestParam("addressId") Integer addressId) {  
        return customerAddressService.deleteCustomerAddress(addressId);
    }  
}
