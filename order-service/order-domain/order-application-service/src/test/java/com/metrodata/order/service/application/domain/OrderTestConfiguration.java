package com.metrodata.order.service.application.domain;

import com.metrodata.order.service.domain.application.ports.output.repository.CustomerRepository;
import com.metrodata.order.service.domain.application.ports.output.repository.OrderRepository;
import com.metrodata.order.service.domain.application.ports.output.repository.RestaurantRepository;
import com.metrodata.order.service.domain.core.OrderDomainService;
import com.metrodata.order.service.domain.core.OrderDomainServiceImpl;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.metrodata")
public class OrderTestConfiguration {

//    @Bean
//    public OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher(){
//        return Mockito.mock(OrderCreatedPaymentRequestMessagePublisher.class);
//    }
//
//    @Bean
//    public OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher(){
//        return Mockito.mock(OrderCancelledPaymentRequestMessagePublisher.class);
//    }
//
//    @Bean
//    public OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher(){
//        return Mockito.mock(OrderPaidRestaurantRequestMessagePublisher.class);
//    }
//
//    @Bean
//    public OrderRepository orderRepository(){
//        return Mockito.mock(OrderRepository.class);
//    }
//
//    @Bean
//    public CustomerRepository customerRepository(){
//        return Mockito.mock(CustomerRepository.class);
//    }
//
//    @Bean
//    public RestaurantRepository restaurantRepository(){
//        return Mockito.mock(RestaurantRepository.class);
//    }
//
//    @Bean
//    public OrderDomainService orderDomainService(){
//        return new OrderDomainServiceImpl();
//    }

}
