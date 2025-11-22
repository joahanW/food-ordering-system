package com.metrodata.order.service.domain.application.ports.output.repository;

import com.metrodata.order.service.domain.core.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Optional<Customer> findCustomer(UUID customerId);
}
