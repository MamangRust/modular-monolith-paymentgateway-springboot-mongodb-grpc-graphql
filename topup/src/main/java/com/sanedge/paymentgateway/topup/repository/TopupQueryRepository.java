package com.sanedge.paymentgateway.topup.repository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.sanedge.paymentgateway.topup.models.Topup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopupQueryRepository extends MongoRepository<Topup, String> {

    Page<Topup> findByDeletedAtIsNull(Pageable pageable);

    Page<Topup> findByDeletedAtIsNotNull(Pageable pageable);

    Optional<Topup> findByTopupIdAndDeletedAtIsNull(String topupId);

    Page<Topup> findByCardNumberAndDeletedAtIsNull(String cardNumber, Pageable pageable);

    List<Topup> findByCardNumberAndDeletedAtIsNullOrderByTopupTimeDesc(String cardNumber);

    @Query("{ 'deleted_at': null, $or: [ { 'card_number': { $regex: ?0, $options: 'i' } }, { 'topup_no': { $regex: ?0, $options: 'i' } }, { 'topup_method': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Topup> searchActiveTopups(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': { $ne: null }, $or: [ { 'card_number': { $regex: ?0, $options: 'i' } }, { 'topup_no': { $regex: ?0, $options: 'i' } }, { 'topup_method': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Topup> searchTrashedTopups(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': null, 'card_number': ?0, $or: [ { 'topup_no': { $regex: ?1, $options: 'i' } }, { 'topup_method': { $regex: ?1, $options: 'i' } }, { 'status': { $regex: ?1, $options: 'i' } } ] }")
    Page<Topup> searchActiveTopupsByCard(String cardNumber, String keyword, Pageable pageable);

    default Page<Topup> findTopups(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveTopups(Pattern.quote(keyword), pageable);
    }

    default Page<Topup> findTopupByCard(String cardNumber, String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByCardNumberAndDeletedAtIsNull(cardNumber, pageable)
                : searchActiveTopupsByCard(cardNumber, Pattern.quote(keyword), pageable);
    }

    default Page<Topup> findActiveTopups(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveTopups(Pattern.quote(keyword), pageable);
    }

    default Page<Topup> findTrashedTopups(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNotNull(pageable)
                : searchTrashedTopups(Pattern.quote(keyword), pageable);
    }

    default List<Topup> findByCardNumber(String cardNumber) {
        return findByCardNumberAndDeletedAtIsNullOrderByTopupTimeDesc(cardNumber);
    }

    default Topup findTopupById(String topupId) {
        return findByTopupIdAndDeletedAtIsNull(topupId).orElse(null);
    }
}
