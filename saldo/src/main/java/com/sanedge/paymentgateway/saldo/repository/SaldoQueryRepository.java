package com.sanedge.paymentgateway.saldo.repository;

import java.util.Optional;
import java.util.regex.Pattern;

import com.sanedge.paymentgateway.saldo.models.Saldo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SaldoQueryRepository extends MongoRepository<Saldo, String> {

    Page<Saldo> findByDeletedAtIsNull(Pageable pageable);

    Page<Saldo> findByDeletedAtIsNotNull(Pageable pageable);

    Optional<Saldo> findByCardNumberAndDeletedAtIsNull(String cardNumber);

    @Query("{ 'deleted_at': null, 'card_number': { $regex: ?0, $options: 'i' } }")
    Page<Saldo> searchActiveSaldos(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': { $ne: null }, 'card_number': { $regex: ?0, $options: 'i' } }")
    Page<Saldo> searchTrashedSaldos(String keyword, Pageable pageable);

    default Page<Saldo> findSaldos(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveSaldos(Pattern.quote(keyword), pageable);
    }

    default Page<Saldo> findActiveSaldos(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveSaldos(Pattern.quote(keyword), pageable);
    }

    default Page<Saldo> findTrashedSaldos(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNotNull(pageable)
                : searchTrashedSaldos(Pattern.quote(keyword), pageable);
    }

    default Optional<Saldo> findByCardNumber(String cardNumber) {
        return findByCardNumberAndDeletedAtIsNull(cardNumber);
    }
}
