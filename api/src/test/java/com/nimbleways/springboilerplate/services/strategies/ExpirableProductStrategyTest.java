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
public class ExpirableProductStrategyTest {

    private ProductRepository productRepository;
    private NotificationService notificationService;
    private ExpirableProductStrategy strategy;

    @BeforeEach
    public void setup() {
        productRepository = mock(ProductRepository.class);
        notificationService = mock(NotificationService.class);
        strategy = new ExpirableProductStrategy(productRepository, notificationService);
    }

    @Test
    public void testProcessValidExpirableProduct() {
        Product product = new Product(null, 5, 2, "EXPIRABLE", "Milk",
                LocalDate.now().plusDays(2), null, null);

        strategy.process(product);

        verify(productRepository, times(1)).save(product);
        verify(notificationService, never()).sendExpirationNotification(anyString(), any());
    }

    @Test
    public void testProcessExpiredProduct() {
        Product product = new Product(null, 5, 2, "EXPIRABLE", "Milk",
                LocalDate.now().minusDays(1), null, null);

        strategy.process(product);

        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1))
                .sendExpirationNotification("Milk", LocalDate.now().minusDays(1));
    }
}
