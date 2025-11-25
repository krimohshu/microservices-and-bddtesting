#!/bin/bash

# Pact Broker Management Script
# Usage: ./pact-broker.sh [start|stop|status|logs|clean]

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() {
    echo ""
    echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
    echo ""
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

start_broker() {
    print_header "Starting Pact Broker"
    
    cd "$SCRIPT_DIR"
    
    if docker ps | grep -q pact-broker; then
        print_warning "Pact Broker is already running"
        return 0
    fi
    
    print_info "Starting PostgreSQL and Pact Broker containers..."
    docker compose -f docker-compose-pact.yml up -d
    
    print_info "Waiting for Pact Broker to be healthy..."
    
    for i in {1..30}; do
        if curl -s -f http://localhost:9292/diagnostic/status/heartbeat > /dev/null 2>&1; then
            print_success "Pact Broker is ready!"
            echo ""
            print_info "Pact Broker URL: http://localhost:9292"
            print_info "Username: pactbroker"
            print_info "Password: pactbroker"
            echo ""
            return 0
        fi
        echo -n "."
        sleep 2
    done
    
    print_error "Pact Broker failed to start. Check logs with: ./pact-broker.sh logs"
    return 1
}

stop_broker() {
    print_header "Stopping Pact Broker"
    
    cd "$SCRIPT_DIR"
    docker compose -f docker-compose-pact.yml down
    
    print_success "Pact Broker stopped"
}

status_broker() {
    print_header "Pact Broker Status"
    
    if docker ps | grep -q pact-broker; then
        print_success "Pact Broker is running"
        echo ""
        docker ps --filter "name=pact-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
        echo ""
        
        if curl -s -f http://localhost:9292/diagnostic/status/heartbeat > /dev/null 2>&1; then
            print_success "Pact Broker is healthy and responding"
            print_info "Access at: http://localhost:9292"
        else
            print_warning "Pact Broker container is running but not responding"
        fi
    else
        print_warning "Pact Broker is not running"
        print_info "Start with: ./pact-broker.sh start"
    fi
}

view_logs() {
    print_header "Pact Broker Logs"
    
    cd "$SCRIPT_DIR"
    
    if ! docker ps | grep -q pact-broker; then
        print_error "Pact Broker is not running"
        return 1
    fi
    
    print_info "Press Ctrl+C to exit logs"
    echo ""
    docker compose -f docker-compose-pact.yml logs -f pact-broker
}

clean_broker() {
    print_header "Cleaning Pact Broker"
    
    print_warning "This will remove all containers, volumes, and data!"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo ""
    
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Cancelled"
        return 0
    fi
    
    cd "$SCRIPT_DIR"
    docker compose -f docker-compose-pact.yml down -v
    
    print_success "Pact Broker cleaned (all data removed)"
}

open_ui() {
    print_header "Opening Pact Broker UI"
    
    if ! docker ps | grep -q pact-broker; then
        print_error "Pact Broker is not running"
        print_info "Start with: ./pact-broker.sh start"
        return 1
    fi
    
    if ! curl -s -f http://localhost:9292/diagnostic/status/heartbeat > /dev/null 2>&1; then
        print_error "Pact Broker is not responding"
        return 1
    fi
    
    print_success "Opening Pact Broker UI in browser..."
    open http://localhost:9292 || xdg-open http://localhost:9292 || print_info "Please open: http://localhost:9292"
}

show_help() {
    print_header "Pact Broker Management"
    
    echo "Usage: ./pact-broker.sh [command]"
    echo ""
    echo "Commands:"
    echo "  start    - Start Pact Broker and PostgreSQL"
    echo "  stop     - Stop Pact Broker and PostgreSQL"
    echo "  restart  - Restart Pact Broker"
    echo "  status   - Show Pact Broker status"
    echo "  logs     - View Pact Broker logs"
    echo "  clean    - Remove all containers and data"
    echo "  ui       - Open Pact Broker UI in browser"
    echo "  help     - Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./pact-broker.sh start"
    echo "  ./pact-broker.sh status"
    echo "  ./pact-broker.sh ui"
    echo ""
}

# Main script logic
case "${1:-help}" in
    start)
        start_broker
        ;;
    stop)
        stop_broker
        ;;
    restart)
        stop_broker
        sleep 2
        start_broker
        ;;
    status)
        status_broker
        ;;
    logs)
        view_logs
        ;;
    clean)
        clean_broker
        ;;
    ui)
        open_ui
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac
