#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="${HOST_ENV_FILE:-$ROOT_DIR/deployments/local/host.env}"
LOG_DIR="${HOST_JAVA_LOG_DIR:-/tmp/payment-gateway-host-logs}"
PID_FILE="${HOST_JAVA_PID_FILE:-/tmp/payment-gateway-host-supervisor.pid}"

cd "$ROOT_DIR"
[[ -r "$ENV_FILE" ]] || { echo "Missing host environment file: $ENV_FILE" >&2; exit 1; }
# shellcheck disable=SC1090
source "$ENV_FILE"

stop_existing() {
    if [[ -f "$PID_FILE" ]]; then
        local supervisor_pid
        supervisor_pid="$(cat "$PID_FILE")"
        # The supervisor is a session/process-group leader created by setsid.
        # Kill the group so child Java services cannot survive a restart.
        kill -- -"$supervisor_pid" 2>/dev/null || kill "$supervisor_pid" 2>/dev/null || true
        for _ in $(seq 1 20); do
            kill -0 "$supervisor_pid" 2>/dev/null || break
            sleep 1
        done
    fi
    for pid_file in "$LOG_DIR"/*.pid; do
        [[ -f "$pid_file" ]] || continue
        kill "$(cat "$pid_file")" 2>/dev/null || true
    done
}

if [[ "${1:-}" == "stop" ]]; then
    stop_existing
    echo "Host Java services stopped."
    exit 0
fi

mkdir -p "$LOG_DIR"
stop_existing
rm -f "$LOG_DIR"/*.log "$LOG_DIR"/*.pid "$PID_FILE"

export KAFKA_BROKERS KAFKA_BOOTSTRAP_SERVERS REDIS_HOST REDIS_PORT REDIS_PASSWORD
export OTEL_EXPORTER_OTLP_ENDPOINT CHAOS_CONFIG_PATH APP_ENV

java -jar "$ROOT_DIR/db-migration/target/db-migration-0.0.1-SNAPSHOT.jar" >"$LOG_DIR/db-migration.log" 2>&1

setsid bash -c '
set -euo pipefail
ROOT="$1"
LOG_DIR="$2"

start_service() {
    local name="$1" jar="$2"
    shift 2
    ( export "$@"; exec java -jar "$ROOT/$jar" ) >"$LOG_DIR/$name.log" 2>&1 &
    echo "$!" >"$LOG_DIR/$name.pid"
}

start_service gateway gateway/target/gateway-0.0.1-SNAPSHOT.jar \
    AUTH_HOST=localhost USER_HOST=localhost CARD_HOST=localhost \
    MERCHANT_HOST=localhost ROLE_HOST=localhost SALDO_HOST=localhost TOPUP_HOST=localhost \
    TRANSACTION_HOST=localhost TRANSFER_HOST=localhost WITHDRAW_HOST=localhost
start_service auth auth/target/auth-0.0.1-SNAPSHOT.jar \
    USER_SERVICE_HOST=localhost ROLE_SERVICE_HOST=localhost
start_service card card/target/card-0.0.1-SNAPSHOT.jar USER_SERVICE_HOST=localhost
start_service merchant merchant/target/merchant-0.0.1-SNAPSHOT.jar USER_SERVICE_HOST=localhost
start_service role role/target/role-0.0.1-SNAPSHOT.jar
start_service saldo saldo/target/saldo-0.0.1-SNAPSHOT.jar CARD_SERVICE_HOST=localhost
start_service topup topup/target/topup-0.0.1-SNAPSHOT.jar CARD_SERVICE_HOST=localhost SALDO_SERVICE_HOST=localhost
start_service transaction transaction/target/transaction-0.0.1-SNAPSHOT.jar \
    CARD_SERVICE_HOST=localhost MERCHANT_SERVICE_HOST=localhost SALDO_SERVICE_HOST=localhost
start_service transfer transfer/target/transfer-0.0.1-SNAPSHOT.jar \
    CARD_SERVICE_HOST=localhost SALDO_SERVICE_HOST=localhost
start_service user user/target/user-0.0.1-SNAPSHOT.jar ROLE_SERVICE_HOST=localhost
start_service withdraw withdraw/target/withdraw-0.0.1-SNAPSHOT.jar \
    CARD_SERVICE_HOST=localhost SALDO_SERVICE_HOST=localhost
# start_service email-service email-service/target/email-service-0.0.1-SNAPSHOT.jar

exec sleep infinity
' host-java-supervisor "$ROOT_DIR" "$LOG_DIR" >"$LOG_DIR/supervisor.log" 2>&1 &

supervisor_pid=$!
echo "$supervisor_pid" >"$PID_FILE"
disown "$supervisor_pid" 2>/dev/null || true

for _ in $(seq 1 "${HOST_JAVA_WAIT_ATTEMPTS:-120}"); do
    ready=true
    curl --silent --fail --max-time 2 http://localhost:8080/actuator/health/readiness >/dev/null 2>&1 || ready=false
    for port in 9004 9005 9006 9007 9008 9009 9010 9011 9012 9013; do
        timeout 1 bash -c "</dev/tcp/127.0.0.1/$port" >/dev/null 2>&1 || ready=false
    done
    [[ "$ready" == true ]] && { echo "Host Java services ready (gateway: http://localhost:8080)."; exit 0; }
    sleep 2
done

echo "Host Java services did not become ready; see $LOG_DIR" >&2
exit 1
