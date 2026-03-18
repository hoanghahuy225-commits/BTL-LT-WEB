package com.example.WebBanHang.model;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_addresses")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddress {
    @Id 
    @Column(name = "address_id")
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer  Id; 
    @Column(name = "customer_id")
    private Integer customerId;
    @Column(name = "address_name" , nullable =  false)
    private String name ;
    @Column(name = "recipient_name") 
    private String  recipient_name ; 
    @Column(name = "phone")
    private String phone ;  
    @Column(name = "address_line")
    private String addressLine ;  
    @Column(name = "city")
    private String city ; 
    @Column(name = "ward")
    private String ward ; 
     
    @Column(name = "is_default")
    private Boolean isDefault ; 
    @Column(name = "created_at")
     @CreationTimestamp
    private LocalDate createdAt ; 
   
    
    

}
