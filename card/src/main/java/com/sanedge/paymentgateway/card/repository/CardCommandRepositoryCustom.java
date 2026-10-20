package com.sanedge.paymentgateway.card.repository;

import com.sanedge.paymentgateway.card.models.Card;

public interface CardCommandRepositoryCustom {

    Card trashed(String cardId);

    Card restore(String cardId);

    boolean deletePermanent(String cardId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
