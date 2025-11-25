#!/bin/bash

################################################################################
# Start All Microservices Script
# This script starts all three microservices in the background
################################################################################

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Base directory
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOGS_DIR="$BASE_DIR/logs"
PIDS_DIR="$BASE_DIR/pids"

# Create directories if they don't exist
mkdir -p "$LOGS_DIR"
mkdir -p "$PIDS_DIR"

# Service configuration (compatible with Bash 3.2)
# Format: service_name:port
SERVICES=(
    "user-service:8082"
    "product-service:8081"
    "order-service:8083"
    "springboot-api-test-cucumber:9091"
)

# Function to get port for a service
get_service_port() {
    local service_name=$1
    for entry in "${SERVICES[@]}"; do
        local svc="${entry%%:*}"
        local port="${entry##*:}"
        if [ "$svc" = "$service_name" ]; then
            echo "$port"
            return 0
        fi
    done
    return 1
}

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0  # Port is in use
    else
        return 1  # Port is free
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local service_name=$1
    local port=$2
    local max_attempts=60
    local attempt=0

    print_info "Waiting for $service_name to start on port $port..."

    while [ $attempt -lt $max_attempts ]; do
        if check_port $port; then
            # Additional check: try to curl the actuator health endpoint
            if curl -s http://localhost:$port/actuator/health >/dev/null 2>&1; then
                print_success "$service_name is ready on port $port"
                return 0
            fi
        fi
        sleep 2
        attempt=$((attempt + 1))
        echo -n "."
    done

    echo ""
    print_error "$service_name failed to start within timeout"
    return 1
}

# Function to start a service
start_service() {
    local service_name=$1
    local port=$2
    local service_dir="$BASE_DIR/$service_name"

    print_info "Starting $service_name..."

    # Check if service directory exists
    if [ ! -d "$service_dir" ]; then
        print_error "Service directory not found: $service_dir"
        return 1
    fi

    # Check if port is already in use
    if check_port $port; then
        print_warning "Port $port is already in use. Skipping $service_name."
        # Save existing PID if possible
        local existing_pid=$(lsof -ti :$port)
        if [ ! -z "$existing_pid" ]; then
            echo $existing_pid > "$PIDS_DIR/${service_name}.pid"
            print_info "Saved existing PID: $existing_pid"
        fi
        return 0
    fi

    # Navigate to service directory and start
    cd "$service_dir"

    # Build the service first
    print_info "Building $service_name..."
    if ! mvn clean package -DskipTests > "$LOGS_DIR/${service_name}-build.log" 2>&1; then
        print_error "Failed to build $service_name. Check logs: $LOGS_DIR/${service_name}-build.log"
        return 1
    fi

    # Start the service in background
    print_info "Launching $service_name..."
    nohup mvn spring-boot:run > "$LOGS_DIR/${service_name}.log" 2>&1 &
    local pid=$!

    # Save PID
    echo $pid > "$PIDS_DIR/${service_name}.pid"
    print_info "$service_name started with PID: $pid"

    # Wait for service to be ready
    if wait_for_service "$service_name" "$port"; then
        return 0
    else
        print_error "Failed to start $service_name"
        return 1
    fi
}

# Main execution
main() {
    print_info "=================================="
    print_info "Starting All Microservices"
    print_info "=================================="
    echo ""

    # Start services in order: user, product, then order
    local failed_services=()

    for entry in "${SERVICES[@]}"; do
        local service="${entry%%:*}"
        local port="${entry##*:}"
        if ! start_service "$service" "$port"; then
            failed_services+=("$service")
        fi
        echo ""
    done

    # Summary
    echo ""
    print_info "=================================="
    print_info "Startup Summary"
    print_info "=================================="

    if [ ${#failed_services[@]} -eq 0 ]; then
        print_success "All services started successfully!"
        echo ""
        print_info "Service Status:"
        for entry in "${SERVICES[@]}"; do
            local service="${entry%%:*}"
            local port="${entry##*:}"
            if [ -f "$PIDS_DIR/${service}.pid" ]; then
                pid=$(cat "$PIDS_DIR/${service}.pid")
                print_success "  ✓ $service (PID: $pid, Port: $port)"
                print_info "    - Health: http://localhost:$port/actuator/health"
                print_info "    - Logs: $LOGS_DIR/${service}.log"
            fi
        done
        echo ""
        print_info "Use './stop-all-services.sh' to stop all services"
        print_info "Use './run-tests.sh' to run integration tests"
    else
        print_error "Some services failed to start:"
        for service in "${failed_services[@]}"; do
            print_error "  ✗ $service"
        done
        echo ""
        print_info "Check logs in: $LOGS_DIR/"
        exit 1
    fi
}

# Run main function
main
