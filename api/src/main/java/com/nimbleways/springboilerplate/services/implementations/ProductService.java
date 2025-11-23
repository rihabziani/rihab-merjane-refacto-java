package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.UnknownProductTypeException;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.strategies.ProductStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class ProductService {

    private final OrderRepository orderRepository;
    private final Map<String, ProductStrategy> strategies;

    public Long processOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        for (Product product : order.getItems()) {
            processProduct(product);
        }

        return order.getId();
    }

    public void processProduct(Product product) {

        ProductStrategy strategy = strategies.get(product.getType());

        if (strategy == null) {
            throw new UnknownProductTypeException(product.getType());
        }

        strategy.process(product);
    }
}
