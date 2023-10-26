package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.model.PaymentMethods;
import com.brocamp.babystore.repository.PaymentMethodsRepository;
import com.brocamp.babystore.service.PaymentMethodsService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class PaymentMethodsServiceImpl implements PaymentMethodsService {
    private PaymentMethodsRepository paymentMethodsRepository;
    @Override
    public List<PaymentMethods> findAllPaymentMethods() {
        return paymentMethodsRepository.findAll();
    }

    @Override
    public PaymentMethods findById(long paymentMethodsId) {
        Optional<PaymentMethods> optionalPaymentMethods= paymentMethodsRepository.findById(paymentMethodsId);
        return optionalPaymentMethods.orElse(null);
    }
}
