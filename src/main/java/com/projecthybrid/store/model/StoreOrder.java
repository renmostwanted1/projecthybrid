package com.projecthybrid.store.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="store_orders")
public class StoreOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private String email;
    private String phone;
    @Column(length=1000) private String shippingAddress;
    private String itemsSummary;
    private BigDecimal total;
    private String status = "PENDING";
    private LocalDateTime createdAt = LocalDateTime.now();

    public StoreOrder() {}
    public StoreOrder(String customerName,String email,String phone,String shippingAddress,String itemsSummary,BigDecimal total){
        this.customerName=customerName;this.email=email;this.phone=phone;this.shippingAddress=shippingAddress;
        this.itemsSummary=itemsSummary;this.total=total;this.status="PENDING";this.createdAt=LocalDateTime.now();
    }
    public Long getId(){return id;} public String getCustomerName(){return customerName;}
    public String getEmail(){return email;} public String getPhone(){return phone;}
    public String getShippingAddress(){return shippingAddress;} public String getItemsSummary(){return itemsSummary;}
    public BigDecimal getTotal(){return total;} public String getStatus(){return status;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}