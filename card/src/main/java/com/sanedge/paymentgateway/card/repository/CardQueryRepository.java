package com.sanedge.paymentgateway.card.repository;

import java.util.Optional;
import java.util.regex.Pattern;

import com.sanedge.paymentgateway.card.models.Card;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardQueryRepository extends MongoRepository<Card, String> {

    Page<Card> findByDeletedAtIsNull(Pageable pageable);

    Page<Card> findByDeletedAtIsNotNull(Pageable pageable);

    Optional<Card> findByCardIdAndDeletedAtIsNull(String cardId);

    Optional<Card> findByUserIdAndDeletedAtIsNull(String userId);

    Optional<Card> findByCardNumberAndDeletedAtIsNull(String cardNumber);

    @Query("{ 'deleted_at': null, $or: [ { 'card_number': { $regex: ?0, $options: 'i' } }, { 'card_type': { $regex: ?0, $options: 'i' } }, { 'card_provider': { $regex: ?0, $options: 'i' } } ] }")
    Page<Card> searchActiveCards(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': { $ne: null }, $or: [ { 'card_number': { $regex: ?0, $options: 'i' } }, { 'card_type': { $regex: ?0, $options: 'i' } }, { 'card_provider': { $regex: ?0, $options: 'i' } } ] }")
    Page<Card> searchTrashedCards(String keyword, Pageable pageable);

    default Page<Card> findCards(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveCards(Pattern.quote(keyword), pageable);
    }

    default Page<Card> findActiveCards(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveCards(Pattern.quote(keyword), pageable);
    }

    default Page<Card> findTrashedCards(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNotNull(pageable)
                : searchTrashedCards(Pattern.quote(keyword), pageable);
    }

    default Optional<Card> findCardById(String cardId) {
        return findByCardIdAndDeletedAtIsNull(cardId);
    }

    default Optional<Card> findCardByUserId(String userId) {
        return findByUserIdAndDeletedAtIsNull(userId);
    }

    default Optional<Card> findCardByCardNumber(String cardNumber) {
        return findByCardNumberAndDeletedAtIsNull(cardNumber);
    }
}
