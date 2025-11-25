#!/bin/bash

################################################################################
# Stop All Microservices Script
# This script stops all running microservices and releases ports
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
PIDS_DIR="$BASE_DIR/pids"

# Service configuration (compatible with Bash 3.2)
# Format: service_name:port
SERVICES=(
    "user-service:8082"
    "product-service:8081"
    "order-service:8083"
    "springboot-api-test-cucumber:9091"
)

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

# Function to kill process by PID
kill_by_pid() {
    local pid=$1
    local service_name=$2

    if ps -p $pid > /dev/null 2>&1; then
        print_info "Stopping $service_name (PID: $pid)..."
        kill $pid 2>/dev/null || true
        sleep 2

        # Force kill if still running
        if ps -p $pid > /dev/null 2>&1; then
            print_warning "Force killing $service_name (PID: $pid)..."
            kill -9 $pid 2>/dev/null || true
        fi

        # Verify it's stopped
        if ! ps -p $pid > /dev/null 2>&1; then
            print_success "$service_name stopped successfully"
            return 0
        else
            print_error "Failed to stop $service_name (PID: $pid)"
            return 1
        fi
    else
        print_warning "$service_name (PID: $pid) is not running"
        return 0
    fi
}

# Function to kill process by port
kill_by_port() {
    local port=$1
    local service_name=$2

    local pids=$(lsof -ti :$port 2>/dev/null)

    if [ -z "$pids" ]; then
        print_info "No process found on port $port for $service_name"
        return 0
    fi

    print_info "Found process(es) on port $port: $pids"
    for pid in $pids; do
        kill_by_pid $pid "$service_name"
    done
}

# Function to stop a service
stop_service() {
    local service_name=$1
    local port=$2
    local pid_file="$PIDS_DIR/${service_name}.pid"

    print_info "Stopping $service_name..."

    # Try to stop by PID file first
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if kill_by_pid $pid "$service_name"; then
            rm -f "$pid_file"
        fi
    fi

    # Also check and kill by port (in case PID file is stale)
    kill_by_port $port "$service_name"

    # Verify port is free
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        print_error "Port $port is still in use after stopping $service_name"
        return 1
    else
        print_success "Port $port is now free"
        return 0
    fi
}

# Function to cleanup all resources
cleanup_resources() {
    print_info "Cleaning up resources..."

    # Remove PID files
    if [ -d "$PIDS_DIR" ]; then
        rm -rf "$PIDS_DIR"
        print_info "Removed PID directory"
    fi

    # Optional: Clean up H2 database files
    for entry in "${SERVICES[@]}"; do
        local service="${entry%%:*}"
        local db_dir="$BASE_DIR/$service/*.db"
        if ls $db_dir 1> /dev/null 2>&1; then
            print_info "Found database files for $service"
            # Uncomment to delete: rm -f $db_dir
        fi
    done

    print_success "Cleanup completed"
}

# Main execution
main() {
    print_info "=================================="
    print_info "Stopping All Microservices"
    print_info "=================================="
    echo ""

    # Stop services in reverse order: api-test, order, product, then user
    local failed_services=()
    local services_reverse=("springboot-api-test-cucumber:9091" "order-service:8083" "product-service:8081" "user-service:8082")

    for entry in "${services_reverse[@]}"; do
        local service="${entry%%:*}"
        local port="${entry##*:}"
        if ! stop_service "$service" "$port"; then
            failed_services+=("$service")
        fi
        echo ""
    done

    # Cleanup resources
    cleanup_resources
    echo ""

    # Summary
    print_info "=================================="
    print_info "Shutdown Summary"
    print_info "=================================="

    if [ ${#failed_services[@]} -eq 0 ]; then
        print_success "All services stopped successfully!"
        echo ""
        print_info "Port Status:"
        for entry in "${SERVICES[@]}"; do
            local service="${entry%%:*}"
            local port="${entry##*:}"
            if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
                print_error "  ✗ Port $port ($service) - STILL IN USE"
            else
                print_success "  ✓ Port $port ($service) - FREE"
            fi
        done
    else
        print_error "Some services failed to stop properly:"
        for service in "${failed_services[@]}"; do
            print_error "  ✗ $service"
        done
        echo ""
        print_info "You may need to manually kill processes"
        exit 1
    fi
}

# Run main function
main
