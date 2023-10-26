package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.model.Wallet;
import com.brocamp.babystore.repository.WalletRepository;
import com.brocamp.babystore.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {
    private WalletRepository walletRepository;
    @Override
    public List<Wallet> findAllByUserId(long id) {
        return walletRepository.findByUsersId(id);
    }

    @Override
    public double findSumOfWalletAmount(long id) {
        double creditAmount = walletRepository.findCreditedAmount(id);
        double debitedAmount = walletRepository.findDebitedAmount(id);
        double amountInWallet = creditAmount-debitedAmount;
        return amountInWallet;
    }

    @Override
    public void save(Wallet wallet) {
        walletRepository.save(wallet);
    }
}
