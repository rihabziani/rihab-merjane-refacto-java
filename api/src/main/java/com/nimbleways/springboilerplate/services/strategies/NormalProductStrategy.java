package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("NORMAL")
@RequiredArgsConstructor
public class NormalProductStrategy implements ProductStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void process(Product p) {
        int available = p.getAvailable() == null ? 0 : p.getAvailable();
        int leadTime = p.getLeadTime() == null ? 0 : p.getLeadTime();

        if (available > 0) {
            p.setAvailable(available - 1);
            productRepository.save(p);
        } else if (leadTime > 0) {
            notificationService.sendDelayNotification(leadTime, p.getName());
        } else {
            notificationService.sendOutOfStockNotification(p.getName());
        }
    }
}
