#!/usr/bin/env bash
# Start all services using setsid for proper daemonization
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
LOG_DIR="/tmp/e2e-host-logs"
mkdir -p "$LOG_DIR"
rm -f "$LOG_DIR"/*.pid 2>/dev/null

ENV_EXPORTS='
export KAFKA_BROKERS=localhost:9092
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=dragon_knight
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
export APP_ENV=local
'

start_svc() {
    local name="$1" jar="$2"
    shift 2
    echo "Starting $name..."
    setsid bash -c "$ENV_EXPORTS
$*
exec java -jar '$ROOT/$jar'
" >"$LOG_DIR/$name.log" 2>&1 &
    sleep 0.3
    # Find the java PID
    local java_pid
    for j in $(seq 1 20); do
        java_pid=$(pgrep -f "$jar" 2>/dev/null | head -1 || true)
        if [ -n "$java_pid" ]; then break; fi
        sleep 0.2
    done
    echo "${java_pid:-unknown}" >"$LOG_DIR/$name.pid"
}

start_svc role role/target/role-0.0.1-SNAPSHOT.jar \
    MONGO_HOST=localhost MONGO_PORT=27017 MONGO_DB_NAME=example_mongo_payment_gateway
sleep 5

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
        echo "❌ Gateway not ready after 180s"
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
