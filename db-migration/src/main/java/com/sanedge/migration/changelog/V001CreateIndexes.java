package com.sanedge.migration.changelog;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

/**
 * Creates the indexes for the payment-gateway collections.
 *
 * <p>Field names mirror the Java property names on the domain models — the
 * models do not override {@code @Field}, so Mongo persists the camelCase names
 * as-is. Unique indexes replicate the {@code @Indexed(unique = true)}
 * annotations so the guarantees hold even when a service boots with
 * {@code spring.data.mongodb.auto-index-creation=false}.
 *
 * <p>Collections covered: users, roles, user_roles, refresh_tokens, merchants,
 * merchant_documents, cards, saldos, topups, transactions, transfers,
 * withdraws, outbox. {@code transactions} is declared by both the merchant and
 * transaction modules; {@code user_roles} by both role and user — the index set
 * is the union, created exactly once here.
 */
@ChangeUnit(id = "001-create-indexes", order = "001", author = "payment-gateway")
public class V001CreateIndexes {

    private static final String USERS = "users";
    private static final String ROLES = "roles";
    private static final String USER_ROLES = "user_roles";
    private static final String REFRESH_TOKENS = "refresh_tokens";
    private static final String MERCHANTS = "merchants";
    private static final String MERCHANT_DOCUMENTS = "merchant_documents";
    private static final String CARDS = "cards";
    private static final String SALDOS = "saldos";
    private static final String TOPUPS = "topups";
    private static final String TRANSACTIONS = "transactions";
    private static final String TRANSFERS = "transfers";
    private static final String WITHDRAWS = "withdraws";
    private static final String OUTBOX = "outbox";

    @Execution
    public void migrate(MongoTemplate template) {
        // users: unique email + username used for lookups.
        IndexOperations users = template.indexOps(USERS);
        users.ensureIndex(new Index().on("email", Sort.Direction.ASC).unique().named("uq_users_email"));
        users.ensureIndex(new Index().on("username", Sort.Direction.ASC).unique().named("uq_users_username"));

        // roles: unique role_name.
        template.indexOps(ROLES)
                .ensureIndex(new Index().on("roleName", Sort.Direction.ASC).unique().named("uq_roles_role_name"));

        // user_roles: fast lookup by user / role; a user may hold a role only once.
        IndexOperations userRoles = template.indexOps(USER_ROLES);
        userRoles.ensureIndex(new Index().on("userId", Sort.Direction.ASC).named("ix_user_roles_user_id"));
        userRoles.ensureIndex(new Index().on("roleId", Sort.Direction.ASC).named("ix_user_roles_role_id"));
        userRoles.ensureIndex(new Index().on("userId", Sort.Direction.ASC)
                .on("roleId", Sort.Direction.ASC).unique().named("uq_user_roles_user_role"));

        // refresh_tokens: unique token; per-user lookup and TTL cleanup on expiry.
        IndexOperations refreshTokens = template.indexOps(REFRESH_TOKENS);
        refreshTokens.ensureIndex(new Index().on("token", Sort.Direction.ASC).unique().named("uq_refresh_tokens_token"));
        refreshTokens.ensureIndex(new Index().on("userId", Sort.Direction.ASC).named("ix_refresh_tokens_user_id"));
        refreshTokens.ensureIndex(new Index().on("expiration", Sort.Direction.ASC)
                .expire(0).named("ttl_refresh_tokens_expiration"));

        // merchants: unique merchant_no + api_key; owner lookup and status filter.
        IndexOperations merchants = template.indexOps(MERCHANTS);
        merchants.ensureIndex(new Index().on("merchantNo", Sort.Direction.ASC).unique().named("uq_merchants_merchant_no"));
        merchants.ensureIndex(new Index().on("apiKey", Sort.Direction.ASC).unique().named("uq_merchants_api_key"));
        merchants.ensureIndex(new Index().on("userId", Sort.Direction.ASC).named("ix_merchants_user_id"));
        merchants.ensureIndex(new Index().on("status", Sort.Direction.ASC).named("ix_merchants_status"));

        // merchant_documents: per-merchant lookup and status filter.
        IndexOperations merchantDocuments = template.indexOps(MERCHANT_DOCUMENTS);
        merchantDocuments.ensureIndex(new Index().on("merchantId", Sort.Direction.ASC)
                .named("ix_merchant_documents_merchant_id"));
        merchantDocuments.ensureIndex(new Index().on("status", Sort.Direction.ASC)
                .named("ix_merchant_documents_status"));

        // cards: unique card_number; per-user lookup.
        IndexOperations cards = template.indexOps(CARDS);
        cards.ensureIndex(new Index().on("cardNumber", Sort.Direction.ASC).unique().named("uq_cards_card_number"));
        cards.ensureIndex(new Index().on("userId", Sort.Direction.ASC).named("ix_cards_user_id"));

        // saldos: per-card lookup.
        template.indexOps(SALDOS)
                .ensureIndex(new Index().on("cardNumber", Sort.Direction.ASC).named("ix_saldos_card_number"));

        // topups: unique topup_no; per-card lookup and status filter.
        IndexOperations topups = template.indexOps(TOPUPS);
        topups.ensureIndex(new Index().on("topupNo", Sort.Direction.ASC).unique().named("uq_topups_topup_no"));
        topups.ensureIndex(new Index().on("cardNumber", Sort.Direction.ASC).named("ix_topups_card_number"));
        topups.ensureIndex(new Index().on("status", Sort.Direction.ASC).named("ix_topups_status"));

        // transactions: unique transaction_no; card / merchant lookup and status filter.
        IndexOperations transactions = template.indexOps(TRANSACTIONS);
        transactions.ensureIndex(new Index().on("transactionNo", Sort.Direction.ASC).unique()
                .named("uq_transactions_transaction_no"));
        transactions.ensureIndex(new Index().on("cardNumber", Sort.Direction.ASC)
                .named("ix_transactions_card_number"));
        transactions.ensureIndex(new Index().on("merchantId", Sort.Direction.ASC)
                .named("ix_transactions_merchant_id"));
        transactions.ensureIndex(new Index().on("status", Sort.Direction.ASC).named("ix_transactions_status"));

        // transfers: unique transfer_no; from / to lookup and status filter.
        IndexOperations transfers = template.indexOps(TRANSFERS);
        transfers.ensureIndex(new Index().on("transferNo", Sort.Direction.ASC).unique().named("uq_transfers_transfer_no"));
        transfers.ensureIndex(new Index().on("transferFrom", Sort.Direction.ASC).named("ix_transfers_transfer_from"));
        transfers.ensureIndex(new Index().on("transferTo", Sort.Direction.ASC).named("ix_transfers_transfer_to"));
        transfers.ensureIndex(new Index().on("status", Sort.Direction.ASC).named("ix_transfers_status"));

        // withdraws: unique withdraw_no; per-card lookup and status filter.
        IndexOperations withdraws = template.indexOps(WITHDRAWS);
        withdraws.ensureIndex(new Index().on("withdrawNo", Sort.Direction.ASC).unique().named("uq_withdraws_withdraw_no"));
        withdraws.ensureIndex(new Index().on("cardNumber", Sort.Direction.ASC).named("ix_withdraws_card_number"));
        withdraws.ensureIndex(new Index().on("status", Sort.Direction.ASC).named("ix_withdraws_status"));

        // outbox: relay drains PENDING rows oldest-first; status used for counts.
        IndexOperations outbox = template.indexOps(OUTBOX);
        outbox.ensureIndex(new Index().on("status", Sort.Direction.ASC).named("ix_outbox_status"));
        outbox.ensureIndex(new Index().on("status", Sort.Direction.ASC)
                .on("createdAt", Sort.Direction.ASC).named("ix_outbox_status_created"));
    }

    /**
     * Drops the indexes created by {@link #migrate(MongoTemplate)}. Collections
     * and documents are intentionally left untouched — this change unit only
     * owns indexes.
     */
    @RollbackExecution
    public void rollback(MongoTemplate template) {
        dropIndexes(template, USERS, List.of("uq_users_email", "uq_users_username"));
        dropIndexes(template, ROLES, List.of("uq_roles_role_name"));
        dropIndexes(template, USER_ROLES,
                List.of("ix_user_roles_user_id", "ix_user_roles_role_id", "uq_user_roles_user_role"));
        dropIndexes(template, REFRESH_TOKENS,
                List.of("uq_refresh_tokens_token", "ix_refresh_tokens_user_id", "ttl_refresh_tokens_expiration"));
        dropIndexes(template, MERCHANTS,
                List.of("uq_merchants_merchant_no", "uq_merchants_api_key", "ix_merchants_user_id", "ix_merchants_status"));
        dropIndexes(template, MERCHANT_DOCUMENTS,
                List.of("ix_merchant_documents_merchant_id", "ix_merchant_documents_status"));
        dropIndexes(template, CARDS, List.of("uq_cards_card_number", "ix_cards_user_id"));
        dropIndexes(template, SALDOS, List.of("ix_saldos_card_number"));
        dropIndexes(template, TOPUPS, List.of("uq_topups_topup_no", "ix_topups_card_number", "ix_topups_status"));
        dropIndexes(template, TRANSACTIONS,
                List.of("uq_transactions_transaction_no", "ix_transactions_card_number",
                        "ix_transactions_merchant_id", "ix_transactions_status"));
        dropIndexes(template, TRANSFERS,
                List.of("uq_transfers_transfer_no", "ix_transfers_transfer_from", "ix_transfers_transfer_to",
                        "ix_transfers_status"));
        dropIndexes(template, WITHDRAWS,
                List.of("uq_withdraws_withdraw_no", "ix_withdraws_card_number", "ix_withdraws_status"));
        dropIndexes(template, OUTBOX, List.of("ix_outbox_status", "ix_outbox_status_created"));
    }

    private void dropIndexes(MongoTemplate template, String collection, List<String> names) {
        IndexOperations ops = template.indexOps(collection);
        for (IndexInfo index : ops.getIndexInfo()) {
            if (names.contains(index.getName())) {
                ops.dropIndex(index.getName());
            }
        }
    }
}
