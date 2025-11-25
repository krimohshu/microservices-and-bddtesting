#!/bin/bash

################################################################################
# View Latest Test Results
# This script shows the latest test results and opens the HTML report
################################################################################

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Base directory
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPORTS_DIR="$BASE_DIR/test-reports"
LATEST_REPORT="$REPORTS_DIR/latest"

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

# Function to parse test results
parse_test_results() {
    local service_name=$1
    local log_file="$LATEST_REPORT/$service_name/test-output.log"

    if [ ! -f "$log_file" ]; then
        print_error "No test results found for $service_name"
        return 1
    fi

    # Extract test counts
    local tests_run=$(grep -oP "Tests run: \K\d+" "$log_file" | tail -1)
    local failures=$(grep -oP "Failures: \K\d+" "$log_file" | tail -1)
    local errors=$(grep -oP "Errors: \K\d+" "$log_file" | tail -1)
    local skipped=$(grep -oP "Skipped: \K\d+" "$log_file" | tail -1)

    # Calculate passed tests
    local passed=$((tests_run - failures - errors))

    # Print results
    echo "  Tests Run: $tests_run"
    echo "  Passed: $passed"
    if [ "$failures" != "0" ]; then
        echo -e "  ${RED}Failures: $failures${NC}"
    else
        echo "  Failures: $failures"
    fi
    if [ "$errors" != "0" ]; then
        echo -e "  ${RED}Errors: $errors${NC}"
    else
        echo "  Errors: $errors"
    fi
    echo "  Skipped: $skipped"

    # Overall status
    if [ "$failures" = "0" ] && [ "$errors" = "0" ]; then
        echo -e "  Status: ${GREEN}✓ PASSED${NC}"
        return 0
    else
        echo -e "  Status: ${RED}✗ FAILED${NC}"
        return 1
    fi
}

# Main execution
main() {
    echo ""
    echo -e "${BLUE}=================================="
    echo "Latest Test Results"
    echo -e "==================================${NC}"
    echo ""

    # Check if latest report exists
    if [ ! -d "$LATEST_REPORT" ]; then
        print_error "No test reports found!"
        print_info "Run tests first using: ./run-tests.sh"
        exit 1
    fi

    # Get report timestamp
    if [ -f "$LATEST_REPORT/index.html" ]; then
        local timestamp=$(grep "Generated:" "$LATEST_REPORT/index.html" | sed -n 's/.*Generated: \(.*\)<.*/\1/p')
        print_info "Report Generated: $timestamp"
        echo ""
    fi

    # Show results for each service
    local all_passed=true
    for service in user-service product-service order-service; do
        if [ -d "$LATEST_REPORT/$service" ]; then
            echo -e "${BLUE}$service:${NC}"
            if ! parse_test_results "$service"; then
                all_passed=false
            fi
            echo ""
        fi
    done

    # Summary
    echo -e "${BLUE}=================================="
    echo "Actions"
    echo -e "==================================${NC}"
    echo ""

    if [ "$all_passed" = true ]; then
        print_success "All tests passed! 🎉"
    else
        print_error "Some tests failed. Check reports for details."
    fi

    echo ""
    print_info "Available Actions:"
    echo "  1. Open HTML Report"
    echo "  2. View User Service Logs"
    echo "  3. View Product Service Logs"
    echo "  4. View Order Service Logs"
    echo "  5. View All Logs"
    echo "  6. Exit"
    echo ""

    read -p "Select an option (1-6): " choice

    case $choice in
        1)
            print_info "Opening HTML report..."
            open "$LATEST_REPORT/index.html" 2>/dev/null || {
                print_info "Please open: $LATEST_REPORT/index.html"
            }
            ;;
        2)
            if [ -f "$LATEST_REPORT/user-service/test-output.log" ]; then
                less "$LATEST_REPORT/user-service/test-output.log"
            else
                print_error "Log file not found"
            fi
            ;;
        3)
            if [ -f "$LATEST_REPORT/product-service/test-output.log" ]; then
                less "$LATEST_REPORT/product-service/test-output.log"
            else
                print_error "Log file not found"
            fi
            ;;
        4)
            if [ -f "$LATEST_REPORT/order-service/test-output.log" ]; then
                less "$LATEST_REPORT/order-service/test-output.log"
            else
                print_error "Log file not found"
            fi
            ;;
        5)
            print_info "Viewing all service logs..."
            echo ""
            echo "=== User Service ==="
            tail -50 "$LATEST_REPORT/user-service/test-output.log" 2>/dev/null || echo "No logs"
            echo ""
            echo "=== Product Service ==="
            tail -50 "$LATEST_REPORT/product-service/test-output.log" 2>/dev/null || echo "No logs"
            echo ""
            echo "=== Order Service ==="
            tail -50 "$LATEST_REPORT/order-service/test-output.log" 2>/dev/null || echo "No logs"
            ;;
        6)
            print_info "Exiting..."
            exit 0
            ;;
        *)
            print_error "Invalid option"
            exit 1
            ;;
    esac
}

# Run main function
main
