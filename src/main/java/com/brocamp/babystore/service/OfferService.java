package com.brocamp.babystore.service;

import com.brocamp.babystore.dto.OfferDTO;
import com.brocamp.babystore.model.Offer;

import java.util.List;

public interface OfferService {
    List<Offer> findAllOffers();

    Offer SaveOffer(OfferDTO offerDTO);

    Offer findById(long id);

    Offer update(Offer offer);

    void disable(long id);

    void enable(long id);

    void deleteOffer(long id);
}
