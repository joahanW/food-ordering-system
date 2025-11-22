package com.metrodata.payment.service.container;

import com.metrodata.payment.service.domain.core.PaymentDomainService;
import com.metrodata.payment.service.domain.core.PaymentDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public PaymentDomainService paymentDomainService(){
        return new PaymentDomainServiceImpl();
    }

}
