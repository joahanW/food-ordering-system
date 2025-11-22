package com.metrodata.order.service.application.rest;

import com.metrodata.order.service.domain.application.dto.create.CreateOrderCommand;
import com.metrodata.order.service.domain.application.dto.create.CreateOrderResponse;
import com.metrodata.order.service.domain.application.dto.track.TrackOrderQuery;
import com.metrodata.order.service.domain.application.dto.track.TrackOrderResponse;
import com.metrodata.order.service.domain.application.ports.input.service.OrderApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody CreateOrderCommand createOrderCommand){
        log.info("Creating order for customer: {} at restaurant: {}",
                createOrderCommand.getCustomerId(),
                createOrderCommand.getRestaurantId());
        CreateOrderResponse order = orderApplicationService.createOrder(createOrderCommand);
        log.info("Create order with tracking id: {}", order.getOrderTrackingId());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(@PathVariable UUID trackingId){
        TrackOrderResponse trackOrderResponse = orderApplicationService.trackOrder(new TrackOrderQuery(trackingId));
        return ResponseEntity.ok(trackOrderResponse);
    }
}
