package com.metrodata.order.service.dataaccess.customer.adapter;

import com.metrodata.order.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import com.metrodata.order.service.dataaccess.customer.repository.CustomerJpaRepository;
import com.metrodata.order.service.domain.core.entity.Customer;
import com.metrodata.order.service.domain.application.ports.output.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;

    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository.findById(customerId)
                .map(customerDataAccessMapper::CustomerEntityToCustomer);
    }

}
