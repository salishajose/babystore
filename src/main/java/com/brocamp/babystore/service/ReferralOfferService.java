package com.brocamp.babystore.service;

import com.brocamp.babystore.model.ReferralOffer;

public interface ReferralOfferService {
    void saveReferral(long id, String token,String email);

    ReferralOffer findByReferralCode(String token);

    void addReferralAmount(String email);

    boolean existsByEmail(String email);

    ReferralOffer findBySenderMail(String email);

    void update(ReferralOffer referralOffer);
}
