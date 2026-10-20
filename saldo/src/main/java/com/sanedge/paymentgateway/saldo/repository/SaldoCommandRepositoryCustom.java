package com.sanedge.paymentgateway.saldo.repository;

import com.sanedge.paymentgateway.saldo.models.Saldo;

public interface SaldoCommandRepositoryCustom {
    Saldo trashed(String saldoId);

    Saldo restore(String saldoId);

    int updateBalanceByCardNumber(String cardNumber, Long newBalance);

    int updateWithdrawByCardNumber(String cardNumber, Long withdrawAmount);

    boolean deletePermanent(String saldoId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
