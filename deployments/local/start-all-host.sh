#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
LOG_DIR="/tmp/e2e-host-logs"
mkdir -p "$LOG_DIR"

# Kill any existing
pkill -f "springboot-payment-gateway" 2>/dev/null || true
pkill -f "paymentgateway" 2>/dev/null || true
sleep 2
rm -f "$LOG_DIR"/*.log "$LOG_DIR"/*.pid 2>/dev/null

export KAFKA_BROKERS=localhost:9092
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=dragon_knight
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
export APP_ENV=local

start_svc() {
    local name="$1" jar="$2"; shift 2
    local java_opts=""
    local env_args=()
    for arg in "$@"; do
        if [[ "$arg" == JAVA_OPTS=* ]]; then
            java_opts="${arg#JAVA_OPTS=}"
        else
            env_args+=("$arg")
        fi
    done
    echo "Starting $name..."
    ( export "${env_args[@]}"; exec java $java_opts -jar "$ROOT/$jar" ) >"$LOG_DIR/$name.log" 2>&1 &
    echo $! >"$LOG_DIR/$name.pid"
}

# ── Start per-service with its own DB connection ────────────────
start_svc role role/target/role-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway

start_svc user user/target/user-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway ROLE_SERVICE_HOST=localhost

start_svc auth auth/target/auth-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway \
    USER_SERVICE_HOST=localhost ROLE_SERVICE_HOST=localhost

start_svc card card/target/card-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway

start_svc merchant merchant/target/merchant-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway

start_svc saldo saldo/target/saldo-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway

start_svc topup topup/target/topup-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway

start_svc transaction transaction/target/transaction-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway

start_svc transfer transfer/target/transfer-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway

start_svc withdraw withdraw/target/withdraw-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway

# ── Start gateway last (needs all gRPC backends) ────────────────
start_svc gateway gateway/target/gateway-0.0.1-SNAPSHOT.jar \
    AUTH_HOST=localhost USER_HOST=localhost CARD_HOST=localhost \
    MERCHANT_HOST=localhost ROLE_HOST=localhost SALDO_HOST=localhost \
    TOPUP_HOST=localhost TRANSACTION_HOST=localhost TRANSFER_HOST=localhost \
    WITHDRAW_HOST=localhost

echo ""
echo "All 11 services started. Waiting for gateway..."
for i in $(seq 1 90); do
    if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ Gateway ready after ${i}s on http://localhost:8080"
        break
    fi
    if [ $i -eq 90 ]; then
        echo "❌ Gateway not ready after 180s. Check $LOG_DIR/gateway.log"
        tail -5 "$LOG_DIR/gateway.log" 2>/dev/null
    fi
    sleep 2
done

echo ""
echo "=== Service Status ==="
for pidfile in "$LOG_DIR"/*.pid; do
    name=$(basename "$pidfile" .pid)
    pid=$(cat "$pidfile")
    alive="❌"
    kill -0 "$pid" 2>/dev/null && alive="✅"
    printf "%s %-15s PID %s\n" "$alive" "$name" "$pid"
done
