#!/bin/bash

################################################################################
# Master Automation Script
# Complete automation for starting services, running tests, and cleanup
################################################################################

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Base directory
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_section() {
    echo ""
    echo -e "${CYAN}╔════════════════════════════════════════════════════════╗"
    echo -e "║  $1"
    echo -e "╚════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

# Function to show main menu
show_menu() {
    clear
    echo -e "${CYAN}"
    cat << "EOF"
╔══════════════════════════════════════════════════════════════╗
║                                                              ║
║        🚀 MICROSERVICES AUTOMATION SUITE 🚀                  ║
║                                                              ║
║              Spring Boot Test Automation                     ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
EOF
    echo -e "${NC}"

    echo -e "${BLUE}Available Services:${NC}"
    echo "  • User Service     (Port: 8082)"
    echo "  • Product Service  (Port: 8081)"
    echo "  • Order Service    (Port: 8083)"
    echo ""

    echo -e "${BLUE}Main Menu:${NC}"
    echo ""
    echo "  ${GREEN}1)${NC} Start All Services"
    echo "  ${GREEN}2)${NC} Stop All Services"
    echo "  ${GREEN}3)${NC} Restart All Services"
    echo ""
    echo "  ${GREEN}4)${NC} Run All Tests"
    echo "  ${GREEN}5)${NC} Run Tests with Tag Filter"
    echo "  ${GREEN}6)${NC} Run Tests for Specific Service"
    echo ""
    echo "  ${GREEN}7)${NC} View Latest Test Results"
    echo "  ${GREEN}8)${NC} Check Service Status"
    echo ""
    echo "  ${GREEN}9)${NC} Full Automation (Start → Test → Stop)"
    echo ""
    echo "  ${RED}0)${NC} Exit"
    echo ""
    echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
}

# Function to check service status
check_service_status() {
    print_section "Service Status Check"

    declare -A SERVICES=(
        ["User Service"]="8082"
        ["Product Service"]="8081"
        ["Order Service"]="8083"
    )

    for service in "${!SERVICES[@]}"; do
        port=${SERVICES[$service]}
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            print_success "✓ $service is RUNNING on port $port"
            # Show health endpoint
            health_status=$(curl -s http://localhost:$port/actuator/health | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
            echo "    Health: $health_status"
        else
            print_error "✗ $service is NOT RUNNING (port $port)"
        fi
    done

    echo ""
    read -p "Press Enter to continue..."
}

# Function to start services
start_services() {
    print_section "Starting All Services"
    "$BASE_DIR/start-all-services.sh"
    echo ""
    read -p "Press Enter to continue..."
}

# Function to stop services
stop_services() {
    print_section "Stopping All Services"
    "$BASE_DIR/stop-all-services.sh"
    echo ""
    read -p "Press Enter to continue..."
}

# Function to restart services
restart_services() {
    print_section "Restarting All Services"
    "$BASE_DIR/stop-all-services.sh"
    sleep 3
    "$BASE_DIR/start-all-services.sh"
    echo ""
    read -p "Press Enter to continue..."
}

# Function to run all tests
run_all_tests() {
    print_section "Running All Tests"
    "$BASE_DIR/run-tests.sh"
    echo ""
    read -p "Press Enter to continue..."
}

# Function to run tests with tags
run_tests_with_tags() {
    print_section "Run Tests with Tag Filter"
    echo ""
    echo "Example tags:"
    echo "  @smoke          - Run only smoke tests"
    echo "  @regression     - Run regression tests"
    echo "  not @Skip       - Exclude skipped tests"
    echo "  @v1             - Run V1 API tests only"
    echo "  @v2             - Run V2 API tests only"
    echo ""
    read -p "Enter Cucumber tags: " tags
    
    if [ ! -z "$tags" ]; then
        "$BASE_DIR/run-tests.sh" --tags "$tags"
    else
        print_error "No tags specified"
    fi
    echo ""
    read -p "Press Enter to continue..."
}

# Function to run tests for specific service
run_service_tests() {
    print_section "Run Tests for Specific Service"
    echo ""
    echo "Available services:"
    echo "  1) user-service"
    echo "  2) product-service"
    echo "  3) order-service"
    echo ""
    read -p "Select service (1-3): " service_choice

    case $service_choice in
        1)
            "$BASE_DIR/run-tests.sh" --service user-service
            ;;
        2)
            "$BASE_DIR/run-tests.sh" --service product-service
            ;;
        3)
            "$BASE_DIR/run-tests.sh" --service order-service
            ;;
        *)
            print_error "Invalid service selection"
            ;;
    esac
    echo ""
    read -p "Press Enter to continue..."
}

# Function to view latest results
view_results() {
    "$BASE_DIR/view-latest-results.sh"
}

# Function to run full automation
full_automation() {
    print_section "Full Automation Pipeline"
    print_info "This will: Start Services → Run Tests → Show Results → Stop Services"
    echo ""
    read -p "Do you want to continue? (y/n) " -n 1 -r
    echo ""

    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Cancelled"
        return
    fi

    # Step 1: Start services
    print_section "Step 1/4: Starting Services"
    if ! "$BASE_DIR/start-all-services.sh"; then
        print_error "Failed to start services. Aborting."
        read -p "Press Enter to continue..."
        return
    fi
    sleep 5

    # Step 2: Run tests
    print_section "Step 2/4: Running Tests"
    "$BASE_DIR/run-tests.sh"
    local test_exit_code=$?

    # Step 3: Show results
    print_section "Step 3/4: Test Results Summary"
    if [ $test_exit_code -eq 0 ]; then
        print_success "All tests passed! 🎉"
    else
        print_error "Some tests failed. Check detailed reports."
    fi
    echo ""
    read -p "View detailed results now? (y/n) " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        "$BASE_DIR/view-latest-results.sh"
    fi

    # Step 4: Stop services
    print_section "Step 4/4: Stopping Services"
    read -p "Stop all services? (y/n) " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        "$BASE_DIR/stop-all-services.sh"
    else
        print_info "Services are still running"
    fi

    echo ""
    read -p "Press Enter to continue..."
}

# Main loop
main() {
    while true; do
        show_menu
        read -p "Select an option (0-9): " choice

        case $choice in
            1)
                start_services
                ;;
            2)
                stop_services
                ;;
            3)
                restart_services
                ;;
            4)
                run_all_tests
                ;;
            5)
                run_tests_with_tags
                ;;
            6)
                run_service_tests
                ;;
            7)
                view_results
                ;;
            8)
                check_service_status
                ;;
            9)
                full_automation
                ;;
            0)
                print_info "Exiting..."
                exit 0
                ;;
            *)
                print_error "Invalid option. Please select 0-9."
                sleep 2
                ;;
        esac
    done
}

# Check if script has execute permissions
if [ ! -x "$BASE_DIR/start-all-services.sh" ]; then
    print_warning "Setting execute permissions on scripts..."
    chmod +x "$BASE_DIR"/*.sh
fi

# Run main function
main
