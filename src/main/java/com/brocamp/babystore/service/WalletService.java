package com.brocamp.babystore.service;

import com.brocamp.babystore.model.Wallet;

import java.util.List;

public interface WalletService {
    List<Wallet> findAllByUserId(long id);

    double findSumOfWalletAmount(long id);

    void save(Wallet wallet);
}
