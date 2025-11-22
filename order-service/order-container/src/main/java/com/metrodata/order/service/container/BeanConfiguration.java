package com.metrodata.order.service.container;

import com.metrodata.order.service.domain.core.OrderDomainService;
import com.metrodata.order.service.domain.core.OrderDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

}
