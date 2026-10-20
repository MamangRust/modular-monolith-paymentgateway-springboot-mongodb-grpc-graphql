package com.sanedge.paymentgateway.card.repository.dashboard;

import com.sanedge.paymentgateway.card.models.Card;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Global dashboard totals. The SQL version INNER-JOINed each collection with
 * {@code cards} on card_number, so only rows whose card exists and is not
 * soft-deleted are counted; the {@code $lookup} keeps that semantics.
 */
@Repository
public interface CardDashboardRepository extends MongoRepository<Card, String> {

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: { $ne: null } } }",
            "{ $lookup: { from: 'cards', localField: 'card_number', foreignField: 'card_number', as: '_card' } }",
            "{ $unwind: '$_card' }",
            "{ $match: { '_card.deleted_at': null } }",
            "{ $group: { _id: null, total: { $sum: '$total_balance' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalBalance();

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: { $ne: null } } }",
            "{ $lookup: { from: 'cards', localField: 'card_number', foreignField: 'card_number', as: '_card' } }",
            "{ $unwind: '$_card' }",
            "{ $match: { '_card.deleted_at': null } }",
            "{ $group: { _id: null, total: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalTopupAmount();

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: { $ne: null } } }",
            "{ $lookup: { from: 'cards', localField: 'card_number', foreignField: 'card_number', as: '_card' } }",
            "{ $unwind: '$_card' }",
            "{ $match: { '_card.deleted_at': null } }",
            "{ $group: { _id: null, total: { $sum: '$withdraw_amount' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalWithdrawAmount();

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: { $ne: null } } }",
            "{ $lookup: { from: 'cards', localField: 'card_number', foreignField: 'card_number', as: '_card' } }",
            "{ $unwind: '$_card' }",
            "{ $match: { '_card.deleted_at': null } }",
            "{ $group: { _id: null, total: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalTransactionAmount();

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null } }",
            "{ $group: { _id: null, total: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalTransferAmount();

    default Long getTotalBalance() {
        Long total = aggregateTotalBalance();
        return total != null ? total : 0L;
    }

    default Long getTotalTopupAmount() {
        Long total = aggregateTotalTopupAmount();
        return total != null ? total : 0L;
    }

    default Long getTotalWithdrawAmount() {
        Long total = aggregateTotalWithdrawAmount();
        return total != null ? total : 0L;
    }

    default Long getTotalTransactionAmount() {
        Long total = aggregateTotalTransactionAmount();
        return total != null ? total : 0L;
    }

    default Long getTotalTransferAmount() {
        // The SQL version aggregated transfers twice (UNION ALL); kept for parity.
        Long total = aggregateTotalTransferAmount();
        return total != null ? total * 2 : 0L;
    }
}
