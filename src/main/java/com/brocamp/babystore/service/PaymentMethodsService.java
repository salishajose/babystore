package com.brocamp.babystore.service;

import com.brocamp.babystore.model.PaymentMethods;

import java.util.List;

public interface PaymentMethodsService {
    List<PaymentMethods> findAllPaymentMethods();

    PaymentMethods findById(long paymentMethodsId);
}
