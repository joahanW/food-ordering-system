package com.metrodata.order.service.dataaccess.customer.mapper;

import com.metrodata.order.service.dataaccess.customer.entity.CustomerEntity;
import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.order.service.domain.core.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper{

    public Customer CustomerEntityToCustomer(CustomerEntity customerEntity){
        return new Customer(new CustomerId(customerEntity.getId()));
    }

}
