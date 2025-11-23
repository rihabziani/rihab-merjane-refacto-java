package com.nimbleways.springboilerplate.exceptions;

public class UnknownProductTypeException extends RuntimeException {
    public UnknownProductTypeException(String type) {
        super("Unknown product type: " + type);
    }
}