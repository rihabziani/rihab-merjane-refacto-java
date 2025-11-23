package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.strategies.ProductStrategy;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductStrategy normalProductStrategy;

    @Mock
    private ProductStrategy seasonalProductStrategy;

    @Mock
    private ProductStrategy expirableProductStrategy;

    @InjectMocks
    private ProductService productService;

    // Injecte la Map de stratégies dans ProductService
    private void setupStrategies() {
        Map<String, ProductStrategy> strategies = Map.of(
                "NORMAL", normalProductStrategy,
                "SEASONAL", seasonalProductStrategy,
                "EXPIRABLE", expirableProductStrategy
        );
        try {
            var field = ProductService.class.getDeclaredField("strategies");
            field.setAccessible(true);
            field.set(productService, strategies);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testNormalProductProcessing() {
        setupStrategies();
        Product product = new Product(null, 15, 0, "NORMAL", "RJ45 Cable", null, null, null);

        productService.processProduct(product);

        verify(normalProductStrategy, times(1)).process(product);
    }

    @Test
    public void testSeasonalProductProcessing() {
        setupStrategies();
        Product product = new Product(null, 10, 5, "SEASONAL", "Christmas Tree",
                null, LocalDate.now().minusDays(1), LocalDate.now().plusDays(10));

        productService.processProduct(product);

        verify(seasonalProductStrategy, times(1)).process(product);
    }

    @Test
    public void testExpirableProductProcessing() {
        setupStrategies();
        Product product = new Product(null, 5, 2, "EXPIRABLE", "Milk",
                LocalDate.now().plusDays(2), null, null);

        productService.processProduct(product);

        verify(expirableProductStrategy, times(1)).process(product);
    }

    @Test
    public void testUnknownProductTypeThrowsException() {
        setupStrategies();
        Product product = new Product(null, 1, 1, "UNKNOWN", "Mystery", null, null, null);

        try {
            productService.processProduct(product);
        } catch (Exception e) {
            assert(e.getMessage().contains("UNKNOWN"));
        }
    }
}
