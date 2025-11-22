package com.metrodata.restaurant.service.container;

import com.metrodata.restaurant.service.domain.core.RestaurantDomainService;
import com.metrodata.restaurant.service.domain.core.RestaurantDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestaurantDomainService restaurantDomainService(){
        return new RestaurantDomainServiceImpl();
    }

}
