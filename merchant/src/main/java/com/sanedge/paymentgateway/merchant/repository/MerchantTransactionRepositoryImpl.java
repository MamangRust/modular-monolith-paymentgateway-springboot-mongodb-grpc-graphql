package com.sanedge.paymentgateway.merchant.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.merchant.models.MerchantTransactions;
import com.sanedge.paymentgateway.merchant.models.Transaction;

/**
 * MongoDB implementation of the paged merchant-transaction queries. The former
 * SQL INNER JOINed {@code transactions} with {@code merchants} on
 * {@code merchant_id}; the {@code $lookup} + {@code $unwind} preserves that
 * semantics. Sorting and pagination are applied after the join so the order
 * matches {@code ORDER BY t.transaction_time DESC}.
 */
@Repository
public class MerchantTransactionRepositoryImpl implements MerchantTransactionRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public MerchantTransactionRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<MerchantTransactions> findAllTransactions(String search, Pageable pageable) {
        Criteria criteria = new Criteria();
        List<Criteria> filters = new ArrayList<>();
        filters.add(Criteria.where("deleted_at").is(null));
        if (search != null && !search.isBlank()) {
            filters.add(new Criteria().orOperator(
                    Criteria.where("card_number").regex(Pattern.quote(search), "i"),
                    Criteria.where("payment_method").regex(Pattern.quote(search), "i")));
        }
        criteria.andOperator(filters.toArray(new Criteria[0]));
        return pageByCriteria(criteria, pageable);
    }

    @Override
    public Page<MerchantTransactions> findAllTransactionsByMerchant(String merchantId, String search,
            Pageable pageable) {
        List<Criteria> filters = new ArrayList<>();
        filters.add(Criteria.where("deleted_at").is(null));
        filters.add(Criteria.where("merchant_id").is(merchantId));
        if (search != null && !search.isBlank()) {
            filters.add(new Criteria().orOperator(
                    Criteria.where("card_number").regex(Pattern.quote(search), "i"),
                    Criteria.where("payment_method").regex(Pattern.quote(search), "i")));
        }
        return pageByCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])), pageable);
    }

    @Override
    public Page<MerchantTransactions> findAllTransactionsByApiKey(String apiKey, String search, Pageable pageable) {
        List<Criteria> filters = new ArrayList<>();
        filters.add(Criteria.where("deleted_at").is(null));
        if (search != null && !search.isBlank()) {
            filters.add(new Criteria().orOperator(
                    Criteria.where("card_number").regex(Pattern.quote(search), "i"),
                    Criteria.where("payment_method").regex(Pattern.quote(search), "i")));
        }
        Criteria criteria = new Criteria().andOperator(filters.toArray(new Criteria[0]));

        long total = countByApiKey(apiKey, search);
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(criteria));
        operations.add(Aggregation.lookup("merchants", "merchant_id", "merchant_id", "_merchant"));
        operations.add(Aggregation.unwind("_merchant"));
        operations.add(Aggregation.match(Criteria.where("_merchant.deleted_at").is(null)
                .and("_merchant.api_key").is(apiKey)));
        operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "transaction_time")));
        operations.add(Aggregation.skip((long) pageable.getOffset()));
        operations.add(Aggregation.limit(pageable.getPageSize()));
        operations.add(projection());
        AggregationResults<MerchantTransactions> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(operations), "transactions", MerchantTransactions.class);
        return new PageImpl<>(results.getMappedResults(), pageable, total);
    }

    private Page<MerchantTransactions> pageByCriteria(Criteria criteria, Pageable pageable) {
        long total = mongoTemplate.count(Query.query(criteria), Transaction.class);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.lookup("merchants", "merchant_id", "merchant_id", "_merchant"),
                Aggregation.unwind("_merchant"),
                Aggregation.match(Criteria.where("_merchant.deleted_at").is(null)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "transaction_time")),
                Aggregation.skip((long) pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()),
                projection());
        AggregationResults<MerchantTransactions> results = mongoTemplate.aggregate(
                aggregation, "transactions", MerchantTransactions.class);
        return new PageImpl<>(results.getMappedResults(), pageable, total);
    }

    private long countByApiKey(String apiKey, String search) {
        List<AggregationOperation> operations = new ArrayList<>();
        List<Criteria> filters = new ArrayList<>();
        filters.add(Criteria.where("deleted_at").is(null));
        if (search != null && !search.isBlank()) {
            filters.add(new Criteria().orOperator(
                    Criteria.where("card_number").regex(Pattern.quote(search), "i"),
                    Criteria.where("payment_method").regex(Pattern.quote(search), "i")));
        }
        operations.add(Aggregation.match(new Criteria().andOperator(filters.toArray(new Criteria[0]))));
        operations.add(Aggregation.lookup("merchants", "merchant_id", "merchant_id", "_merchant"));
        operations.add(Aggregation.unwind("_merchant"));
        operations.add(Aggregation.match(Criteria.where("_merchant.deleted_at").is(null)
                .and("_merchant.api_key").is(apiKey)));
        operations.add(Aggregation.count().as("total"));
        operations.add(Aggregation.project(Fields.fields("total")));
        AggregationResults<Document> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(operations), "transactions", Document.class);
        Document doc = results.getUniqueMappedResult();
        return doc == null ? 0L : ((Number) doc.get("total")).longValue();
    }

    private AggregationOperation projection() {
        return context -> new Document("$project", new Document()
                .append("_id", 0)
                .append("transactionId", "$transaction_id")
                .append("cardNumber", "$card_number")
                .append("amount", "$amount")
                .append("paymentMethod", "$payment_method")
                .append("merchantId", "$merchant_id")
                .append("merchantName", "$_merchant.name")
                .append("transactionTime", "$transaction_time")
                .append("createdAt", "$created_at")
                .append("updatedAt", "$updated_at")
                .append("deletedAt", "$deleted_at"));
    }
}
