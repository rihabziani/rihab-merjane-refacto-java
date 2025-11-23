package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import com.nimbleways.springboilerplate.utils.StaticLogbackAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@UnitTest
public class NormalProductStrategyTest {

    private ProductRepository productRepository;
    private NotificationService notificationService;
    private NormalProductStrategy strategy;

    @BeforeEach
    public void setup() {
        productRepository = mock(ProductRepository.class);
        notificationService = mock(NotificationService.class);
        strategy = new NormalProductStrategy(productRepository, notificationService);
        StaticLogbackAppender.clearEvents();
    }

    @Test
    public void testProcessNormalProductWithStock() {
        Product product = new Product(null, 5, 10, "NORMAL", "RJ45 Cable", null, null, null);

        strategy.process(product);

        verify(productRepository, times(1)).save(product);
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
    }

    @Test
    public void testProcessNormalProductOutOfStockWithLeadTime() {
        Product product = new Product(null, 5, 0, "NORMAL", "RJ45 Cable", null, null, null);

        strategy.process(product);

        verify(notificationService, times(1)).sendDelayNotification(5, "RJ45 Cable");
    }

    @Test
    public void testProcessNormalProductOutOfStockNoLeadTime() {
        Product product = new Product(null, 0, 0, "NORMAL", "RJ45 Cable", null, null, null);

        strategy.process(product);

        verify(notificationService, times(1)).sendOutOfStockNotification("RJ45 Cable");
    }
}
