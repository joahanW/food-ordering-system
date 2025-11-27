package com.metrodata.order.service.application.domain;

import com.metrodata.common.domain.valueobject.*;
import com.metrodata.order.service.domain.application.dto.create.CreateOrderCommand;
import com.metrodata.order.service.domain.application.dto.create.CreateOrderResponse;
import com.metrodata.order.service.domain.application.dto.create.OrderAddress;
import com.metrodata.order.service.domain.application.dto.create.OrderItem;
import com.metrodata.order.service.domain.core.entity.Customer;
import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.entity.Product;
import com.metrodata.order.service.domain.core.entity.Restaurant;
import com.metrodata.order.service.domain.core.exception.OrderDomainException;
import com.metrodata.order.service.domain.application.mapper.OrderDataMapper;
import com.metrodata.order.service.domain.application.ports.input.service.OrderApplicationService;
import com.metrodata.order.service.domain.application.ports.output.repository.CustomerRepository;
import com.metrodata.order.service.domain.application.ports.output.repository.OrderRepository;
import com.metrodata.order.service.domain.application.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

//    @Autowired
//    private OrderApplicationService orderApplicationService;
//
//    @Autowired
//    private OrderDataMapper orderDataMapper;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private RestaurantRepository restaurantRepository;
//
//    private CreateOrderCommand createOrderCommand;
//    private CreateOrderCommand createOrderCommandWrongPrice;
//    private CreateOrderCommand createOrderCommandWrongProductPrice;
//
//    private final UUID CUSTOMER_ID = UUID.fromString("7b5f1233-18ae-4fdc-80af-c0dd07250656");
//    private final UUID RESTAURANT_ID = UUID.fromString("41d2ae65-f809-46b7-81e3-a24c64fd63cf");
//    private final UUID ORDER_ID = UUID.fromString("ac028f18-b19c-4eae-a639-f67911292a33");
//    private final UUID PRODUCT_ID = UUID.fromString("a574e661-c730-4025-8aed-55584a6f2e69");
//    private final BigDecimal PRICE = new BigDecimal("200.00");
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    @BeforeAll
//    public void setUp() {
//        createOrderCommand = CreateOrderCommand.builder()
//                .customerId(CUSTOMER_ID)
//                .restaurantId(RESTAURANT_ID)
//                .address(OrderAddress.builder()
//                        .street("street_1")
//                        .postalCode("1000AB")
//                        .city("Paris")
//                        .build())
//                .price(PRICE)
//                .items(List.of(OrderItem.builder()
//                        .productId(PRODUCT_ID)
//                        .quantity(1)
//                        .price(new BigDecimal("50.00"))
//                        .subTotal(new BigDecimal("50.00"))
//                        .build(),
//                        OrderItem.builder()
//                                .productId(PRODUCT_ID)
//                                .quantity(3)
//                                .price(new BigDecimal("50.00"))
//                                .subTotal(new BigDecimal("150.00"))
//                                .build()))
//                .build();
//        createOrderCommandWrongPrice = CreateOrderCommand.builder()
//                .customerId(CUSTOMER_ID)
//                .restaurantId(RESTAURANT_ID)
//                .address(OrderAddress.builder()
//                        .street("street_1")
//                        .postalCode("1000AB")
//                        .city("Paris")
//                        .build())
//                .price(new BigDecimal("250.00"))
//                .items(List.of(OrderItem.builder()
//                                .productId(PRODUCT_ID)
//                                .quantity(1)
//                                .price(new BigDecimal("50.00"))
//                                .subTotal(new BigDecimal("50.00"))
//                                .build(),
//                        OrderItem.builder()
//                                .productId(PRODUCT_ID)
//                                .quantity(3)
//                                .price(new BigDecimal("50.00"))
//                                .subTotal(new BigDecimal("150.00"))
//                                .build()))
//                .build();
//
//        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
//                .customerId(CUSTOMER_ID)
//                .restaurantId(RESTAURANT_ID)
//                .address(OrderAddress.builder()
//                        .street("street_1")
//                        .postalCode("1000AB")
//                        .city("Paris")
//                        .build())
//                .price(new BigDecimal("210.00"))
//                .items(List.of(OrderItem.builder()
//                                .productId(PRODUCT_ID)
//                                .quantity(1)
//                                .price(new BigDecimal("60.00"))
//                                .subTotal(new BigDecimal("60.00"))
//                                .build(),
//                        OrderItem.builder()
//                                .productId(PRODUCT_ID)
//                                .quantity(3)
//                                .price(new BigDecimal("50.00"))
//                                .subTotal(new BigDecimal("150.00"))
//                                .build()))
//                .build();
//
//        Customer customer = new Customer();
//        customer.setId(new CustomerId(CUSTOMER_ID));
//        Restaurant restaurantResponse = Restaurant.Builder
//                .newBuilder()
//                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
//                .products(List.of(
//                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
//                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
//                        ))
//                .active(true)
//                .build();
//
//        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
//        order.setId(new OrderId(ORDER_ID));
//
//        // Creating Mock for Repository
//        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
//        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
//                .thenReturn(Optional.of(restaurantResponse));
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//
//    }
//
////    @Test
//    public void testCreateOrder(){
//        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
//        assertEquals(createOrderResponse.getOrderStatus(), OrderStatus.PENDING);
//        assertEquals(createOrderResponse.getMessage(), "Order is created successfully");
//        assertNotNull(createOrderResponse.getOrderTrackingId());
//    }
//
////    @Test
//    public void testCreateOrderWithWrongTotalPrice(){
//        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class, () ->
//                orderApplicationService.createOrder(createOrderCommandWrongPrice));
//        assertEquals(orderDomainException.getMessage(), "Total Price: 250.00 is not equal to Order items total: 200.00 !");
//    }
//
////    @Test
//    public void testCreateOrderWithWrongProductPrice(){
//        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class, () ->
//                orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
//        assertEquals(orderDomainException.getMessage(), "Order item price: 60.00 is not valid for product: product-2 !");
//    }
//
////    @Test
//    public void testCreateOrderWithPassiveRestaurant(){
//        Restaurant restaurantResponse = Restaurant.Builder.newBuilder()
//                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
//                .products(List.of(
//                        new Product((new ProductId(PRODUCT_ID)), "product-1", new Money(new BigDecimal("50.00"))),
//                        new Product((new ProductId(PRODUCT_ID)), "product-2", new Money(new BigDecimal("50.00")))
//                ))
//                .active(false)
//                .build();
//        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
//                .thenReturn(Optional.of(restaurantResponse));
//        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class, () ->
//                orderApplicationService.createOrder(createOrderCommand));
//        assertEquals(orderDomainException.getMessage(), String.format("Restaurant with id %s is currently not active!",RESTAURANT_ID));
//    }
}
