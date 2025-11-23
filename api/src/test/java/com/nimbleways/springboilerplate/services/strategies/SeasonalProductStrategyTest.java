package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@UnitTest
public class SeasonalProductStrategyTest {

    private ProductRepository productRepository;
    private NotificationService notificationService;
    private SeasonalProductStrategy strategy;

    @BeforeEach
    public void setup() {
        productRepository = mock(ProductRepository.class);
        notificationService = mock(NotificationService.class);
        strategy = new SeasonalProductStrategy(productRepository, notificationService);
    }

    @Test
    public void testInSeasonWithStock() {
        Product product = new Product(null, 3, 5, "SEASONAL", "Christmas Tree",
                null, LocalDate.now().minusDays(1), LocalDate.now().plusDays(10));

        strategy.process(product);

        verify(productRepository, times(1)).save(product);
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
    }

    @Test
    public void testOutOfSeasonBeforeStart() {
        Product product = new Product(null, 3, 5, "SEASONAL", "Christmas Tree",
                null, LocalDate.now().plusDays(1), LocalDate.now().plusDays(10));

        strategy.process(product);

        verify(notificationService, times(1)).sendOutOfStockNotification("Christmas Tree");
    }

    @Test
    public void testRestockAfterSeasonEnd() {
        Product product = new Product(null, 15, 0, "SEASONAL", "Christmas Tree",
                null, LocalDate.now().minusDays(10), LocalDate.now().plusDays(5));

        strategy.process(product);

        verify(notificationService, times(1)).sendOutOfStockNotification("Christmas Tree");
    }
}
