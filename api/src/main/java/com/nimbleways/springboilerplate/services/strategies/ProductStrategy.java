package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductStrategy {
    void process(Product product);
}