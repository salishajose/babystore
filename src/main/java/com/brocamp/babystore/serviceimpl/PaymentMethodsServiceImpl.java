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

    @Override
    public void createPaymentMethodsIfNotExists() {
        try{
            List<PaymentMethods> paymentMethodsList =paymentMethodsRepository.findAll();
            if(paymentMethodsList.isEmpty()){
                PaymentMethods paymentMethods1 = new PaymentMethods();
                paymentMethods1.setPaymentMode("Cash On Delivery");
                paymentMethodsRepository.save(paymentMethods1);

                PaymentMethods paymentMethods2 = new PaymentMethods();
                paymentMethods2.setPaymentMode("Razor Pay");
                paymentMethodsRepository.save(paymentMethods2);

                PaymentMethods paymentMethods3 = new PaymentMethods();
                paymentMethods3.setPaymentMode("Wallet");
                paymentMethodsRepository.save(paymentMethods3);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


    }
}
