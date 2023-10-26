package com.brocamp.babystore.exception;

public class OrderDetailsNotFoundException extends RuntimeException{
    public OrderDetailsNotFoundException(String message){
        super(message);
    }
}
