package com.sanedge.paymentgateway.card.repository.dashboard;

import com.sanedge.paymentgateway.card.models.Card;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardDashboardByCardRepository extends MongoRepository<Card, String> {

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0 } }",
            "{ $group: { _id: null, total: { $sum: '$total_balance' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalBalanceByCard(String cardNumber);

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0 } }",
            "{ $group: { _id: null, total: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalTopupAmountByCard(String cardNumber);

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0 } }",
            "{ $group: { _id: null, total: { $sum: '$withdraw_amount' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalWithdrawAmountByCard(String cardNumber);

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0 } }",
            "{ $group: { _id: null, total: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalTransactionAmountByCard(String cardNumber);

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transfer_from: ?0 } }",
            "{ $group: { _id: null, total: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalTransferAmountBySender(String sender);

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transfer_to: ?0 } }",
            "{ $group: { _id: null, total: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, total: { $ifNull: ['$total', 0] } } }"
    })
    Long aggregateTotalTransferAmountByReceiver(String receiver);

    default Long getTotalBalanceByCard(String cardNumber) {
        Long total = aggregateTotalBalanceByCard(cardNumber);
        return total != null ? total : 0L;
    }

    default Long getTotalTopupAmountByCard(String cardNumber) {
        Long total = aggregateTotalTopupAmountByCard(cardNumber);
        return total != null ? total : 0L;
    }

    default Long getTotalWithdrawAmountByCard(String cardNumber) {
        Long total = aggregateTotalWithdrawAmountByCard(cardNumber);
        return total != null ? total : 0L;
    }

    default Long getTotalTransactionAmountByCard(String cardNumber) {
        Long total = aggregateTotalTransactionAmountByCard(cardNumber);
        return total != null ? total : 0L;
    }

    default Long getTotalTransferAmountBySender(String sender) {
        Long total = aggregateTotalTransferAmountBySender(sender);
        return total != null ? total : 0L;
    }

    default Long getTotalTransferAmountByReceiver(String receiver) {
        Long total = aggregateTotalTransferAmountByReceiver(receiver);
        return total != null ? total : 0L;
    }
}
