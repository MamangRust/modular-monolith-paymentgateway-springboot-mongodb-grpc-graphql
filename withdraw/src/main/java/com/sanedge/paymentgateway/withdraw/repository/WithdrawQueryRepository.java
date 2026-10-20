package com.sanedge.paymentgateway.withdraw.repository;

import java.util.List;
import java.util.regex.Pattern;

import com.sanedge.paymentgateway.withdraw.models.Withdraw;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawQueryRepository extends MongoRepository<Withdraw, String> {

    Page<Withdraw> findByDeletedAtIsNull(Pageable pageable);

    Page<Withdraw> findByDeletedAtIsNotNull(Pageable pageable);

    List<Withdraw> findByCardNumberAndDeletedAtIsNullOrderByWithdrawTimeDesc(String cardNumber);

    Page<Withdraw> findByCardNumberAndDeletedAtIsNull(String cardNumber, Pageable pageable);

    @Query("{ 'deleted_at': null, $or: [ { 'card_number': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Withdraw> searchActiveWithdraws(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': { $ne: null }, $or: [ { 'card_number': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Withdraw> searchTrashedWithdraws(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': null, 'card_number': ?0, 'status': { $regex: ?1, $options: 'i' } }")
    Page<Withdraw> searchWithdrawsByCardNumber(String cardNumber, String keyword, Pageable pageable);

    default Page<Withdraw> findAllWithdraws(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveWithdraws(Pattern.quote(keyword), pageable);
    }

    default Page<Withdraw> findActiveWithdraws(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveWithdraws(Pattern.quote(keyword), pageable);
    }

    default Page<Withdraw> findTrashedWithdraws(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNotNull(pageable)
                : searchTrashedWithdraws(Pattern.quote(keyword), pageable);
    }

    default List<Withdraw> findByCardNumber(String cardNumber) {
        return findByCardNumberAndDeletedAtIsNullOrderByWithdrawTimeDesc(cardNumber);
    }

    default Page<Withdraw> findAllByCardNumber(String cardNumber, String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByCardNumberAndDeletedAtIsNull(cardNumber, pageable)
                : searchWithdrawsByCardNumber(cardNumber, Pattern.quote(keyword), pageable);
    }
}
