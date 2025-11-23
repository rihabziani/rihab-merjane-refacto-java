package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component("SEASONAL")
@RequiredArgsConstructor
public class SeasonalProductStrategy implements ProductStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void process(Product p) {
        LocalDate today = LocalDate.now();
        int available = p.getAvailable() == null ? 0 : p.getAvailable();
        int leadTime = p.getLeadTime() == null ? 0 : p.getLeadTime();
        LocalDate start = p.getSeasonStartDate();
        LocalDate end = p.getSeasonEndDate();

        if (start == null || end == null) {
            notificationService.sendOutOfStockNotification(p.getName());
            p.setAvailable(0);
            productRepository.save(p);
            return;
        }

        boolean inSeason = !today.isBefore(start) && !today.isAfter(end);
        if (inSeason && available > 0) {
            p.setAvailable(available - 1);
            productRepository.save(p);
            return;
        }

        LocalDate restock = today.plusDays(leadTime);
        if (restock.isAfter(end)) {
            notificationService.sendOutOfStockNotification(p.getName());
            p.setAvailable(0);
            productRepository.save(p);
            return;
        }

        if (start.isAfter(today)) {
            notificationService.sendOutOfStockNotification(p.getName());
            productRepository.save(p);
            return;
        }

        if (leadTime > 0) {
            notificationService.sendDelayNotification(leadTime, p.getName());
        } else {
            notificationService.sendOutOfStockNotification(p.getName());
        }
    }
}
