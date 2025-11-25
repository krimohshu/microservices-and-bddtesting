#!/bin/bash

################################################################################
# Run Tests Script
# This script runs tests for all services with support for tag filtering
################################################################################

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Base directory
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPORTS_DIR="$BASE_DIR/test-reports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Service configuration
declare -A SERVICES=(
    ["user-service"]="8082"
    ["product-service"]="8081"
    ["order-service"]="8083"
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

print_section() {
    echo ""
    echo -e "${CYAN}=================================="
    echo -e "$1"
    echo -e "==================================${NC}"
    echo ""
}

# Function to show usage
show_usage() {
    cat << EOF
Usage: $0 [OPTIONS]

Run tests for microservices with optional tag filtering

OPTIONS:
    -s, --service <name>       Run tests only for specific service
                               Options: user-service, product-service, order-service, all (default)
    
    -t, --tags <tags>          Cucumber tags to filter tests (e.g., "@smoke", "not @Skip")
                               Multiple tags can be combined with "and", "or"
    
    -p, --parallel             Enable parallel test execution (default: true)
    
    -r, --report               Generate HTML report after tests (default: true)
    
    -c, --coverage             Generate code coverage report (default: false)
    
    -f, --fail-fast            Stop on first test failure (default: false)
    
    -h, --help                 Show this help message

EXAMPLES:
    # Run all tests for all services
    $0

    # Run tests for specific service
    $0 --service user-service

    # Run only smoke tests
    $0 --tags "@smoke"

    # Run tests excluding skipped scenarios
    $0 --tags "not @Skip"

    # Run specific service with tag filtering
    $0 --service order-service --tags "@v1"

    # Run with coverage report
    $0 --coverage

    # Run and stop on first failure
    $0 --fail-fast

EOF
}

# Parse command line arguments
SERVICE="all"
TAGS=""
PARALLEL="true"
GENERATE_REPORT="true"
COVERAGE="false"
FAIL_FAST="false"

while [[ $# -gt 0 ]]; do
    case $1 in
        -s|--service)
            SERVICE="$2"
            shift 2
            ;;
        -t|--tags)
            TAGS="$2"
            shift 2
            ;;
        -p|--parallel)
            PARALLEL="true"
            shift
            ;;
        --no-parallel)
            PARALLEL="false"
            shift
            ;;
        -r|--report)
            GENERATE_REPORT="true"
            shift
            ;;
        --no-report)
            GENERATE_REPORT="false"
            shift
            ;;
        -c|--coverage)
            COVERAGE="true"
            shift
            ;;
        -f|--fail-fast)
            FAIL_FAST="true"
            shift
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Function to check if services are running
check_services() {
    print_info "Checking if services are running..."
    local all_running=true

    for service in "${!SERVICES[@]}"; do
        port=${SERVICES[$service]}
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            print_success "  ✓ $service is running on port $port"
        else
            print_warning "  ✗ $service is not running on port $port"
            all_running=false
        fi
    done

    if [ "$all_running" = false ]; then
        print_warning "Not all services are running. Tests may fail."
        read -p "Do you want to start services now? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            print_info "Starting services..."
            "$BASE_DIR/start-all-services.sh"
        else
            print_info "Continuing without starting services..."
        fi
    fi
}

# Function to build Maven test command
build_test_command() {
    local service_dir=$1
    local cmd="mvn test"

    # Add tag filtering if specified
    if [ ! -z "$TAGS" ]; then
        cmd="$cmd -Dcucumber.filter.tags=\"$TAGS\""
    fi

    # Add fail-fast option
    if [ "$FAIL_FAST" = "true" ]; then
        cmd="$cmd -Dsurefire.skipAfterFailureCount=1"
    fi

    # Add coverage if requested
    if [ "$COVERAGE" = "true" ]; then
        cmd="$cmd jacoco:report"
    fi

    echo "$cmd"
}

# Function to run tests for a service
run_service_tests() {
    local service_name=$1
    local service_dir="$BASE_DIR/$service_name"

    print_section "Running Tests: $service_name"

    # Check if service directory exists
    if [ ! -d "$service_dir" ]; then
        print_error "Service directory not found: $service_dir"
        return 1
    fi

    # Create reports directory
    local service_report_dir="$REPORTS_DIR/$service_name/$TIMESTAMP"
    mkdir -p "$service_report_dir"

    # Navigate to service directory
    cd "$service_dir"

    # Build test command
    local test_cmd=$(build_test_command "$service_dir")

    print_info "Executing: $test_cmd"
    print_info "Report directory: $service_report_dir"
    echo ""

    # Run tests
    if eval "$test_cmd" > "$service_report_dir/test-output.log" 2>&1; then
        print_success "Tests passed for $service_name"
        
        # Copy reports
        if [ -d "target/cucumber-reports" ]; then
            cp -r target/cucumber-reports/* "$service_report_dir/" 2>/dev/null || true
        fi
        if [ -d "target/surefire-reports" ]; then
            cp -r target/surefire-reports/* "$service_report_dir/" 2>/dev/null || true
        fi
        
        return 0
    else
        print_error "Tests failed for $service_name"
        print_info "Check logs: $service_report_dir/test-output.log"
        
        # Copy reports even on failure
        if [ -d "target/cucumber-reports" ]; then
            cp -r target/cucumber-reports/* "$service_report_dir/" 2>/dev/null || true
        fi
        if [ -d "target/surefire-reports" ]; then
            cp -r target/surefire-reports/* "$service_report_dir/" 2>/dev/null || true
        fi
        
        return 1
    fi
}

# Function to generate combined report
generate_combined_report() {
    print_section "Generating Combined Test Report"

    local latest_report="$REPORTS_DIR/latest"
    rm -rf "$latest_report"
    mkdir -p "$latest_report"

    # Create index.html
    cat > "$latest_report/index.html" << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Microservices Test Report</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f5f5; padding: 20px; }
        .container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 8px 8px 0 0; }
        h1 { font-size: 2em; margin-bottom: 10px; }
        .timestamp { opacity: 0.9; font-size: 0.9em; }
        .services { padding: 30px; }
        .service-card { background: #f8f9fa; border-radius: 8px; padding: 20px; margin-bottom: 20px; border-left: 4px solid #667eea; }
        .service-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        .service-title { font-size: 1.5em; color: #333; margin-bottom: 15px; }
        .service-links { display: flex; gap: 15px; flex-wrap: wrap; }
        .btn { display: inline-block; padding: 10px 20px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; transition: all 0.3s; }
        .btn:hover { background: #5568d3; transform: translateY(-2px); }
        .btn-secondary { background: #28a745; }
        .btn-secondary:hover { background: #218838; }
        .status { display: inline-block; padding: 5px 15px; border-radius: 20px; font-size: 0.9em; margin-bottom: 10px; }
        .status.passed { background: #d4edda; color: #155724; }
        .status.failed { background: #f8d7da; color: #721c24; }
        .footer { text-align: center; padding: 20px; color: #666; border-top: 1px solid #eee; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🚀 Microservices Test Report</h1>
            <div class="timestamp">Generated: TIMESTAMP_PLACEHOLDER</div>
        </div>
        <div class="services">
            SERVICES_PLACEHOLDER
        </div>
        <div class="footer">
            <p>Generated by automated test runner | Spring Boot Microservices</p>
        </div>
    </div>
</body>
</html>
EOF

    # Replace timestamp
    sed -i '' "s/TIMESTAMP_PLACEHOLDER/$(date '+%Y-%m-%d %H:%M:%S')/" "$latest_report/index.html"

    # Build services section
    local services_html=""
    for service in user-service product-service order-service; do
        local service_report="$REPORTS_DIR/$service/$TIMESTAMP"
        if [ -d "$service_report" ]; then
            # Copy service reports to latest
            mkdir -p "$latest_report/$service"
            cp -r "$service_report"/* "$latest_report/$service/" 2>/dev/null || true

            # Check test status
            local status="passed"
            if grep -q "BUILD FAILURE" "$service_report/test-output.log" 2>/dev/null; then
                status="failed"
            fi

            services_html+="<div class=\"service-card\">"
            services_html+="<div class=\"service-title\">$service</div>"
            services_html+="<div class=\"status $status\">$(echo $status | tr '[:lower:]' '[:upper:]')</div>"
            services_html+="<div class=\"service-links\">"
            
            if [ -f "$latest_report/$service/cucumber.html" ]; then
                services_html+="<a href=\"$service/cucumber.html\" class=\"btn\" target=\"_blank\">📊 Cucumber Report</a>"
            fi
            
            if [ -f "$latest_report/$service/test-output.log" ]; then
                services_html+="<a href=\"$service/test-output.log\" class=\"btn btn-secondary\" target=\"_blank\">📝 Test Logs</a>"
            fi
            
            services_html+="</div></div>"
        fi
    done

    # Replace services placeholder
    sed -i '' "s|SERVICES_PLACEHOLDER|$services_html|" "$latest_report/index.html"

    print_success "Combined report generated: $latest_report/index.html"
    print_info "Opening report in browser..."
    open "$latest_report/index.html" 2>/dev/null || print_info "Please open: $latest_report/index.html"
}

# Main execution
main() {
    print_section "Microservices Test Runner"

    print_info "Configuration:"
    print_info "  Service: $SERVICE"
    print_info "  Tags: ${TAGS:-none}"
    print_info "  Parallel: $PARALLEL"
    print_info "  Coverage: $COVERAGE"
    print_info "  Fail Fast: $FAIL_FAST"
    echo ""

    # Check services
    check_services
    echo ""

    # Run tests
    local failed_services=()
    
    if [ "$SERVICE" = "all" ]; then
        # Run tests for all services
        for service in user-service product-service order-service; do
            if ! run_service_tests "$service"; then
                failed_services+=("$service")
                if [ "$FAIL_FAST" = "true" ]; then
                    print_error "Stopping due to failure (fail-fast mode)"
                    break
                fi
            fi
        done
    else
        # Run tests for specific service
        if ! run_service_tests "$SERVICE"; then
            failed_services+=("$SERVICE")
        fi
    fi

    # Generate report
    if [ "$GENERATE_REPORT" = "true" ]; then
        generate_combined_report
    fi

    # Summary
    print_section "Test Summary"

    if [ ${#failed_services[@]} -eq 0 ]; then
        print_success "All tests passed! 🎉"
        print_info "Reports available at: $REPORTS_DIR/latest/index.html"
        exit 0
    else
        print_error "Some tests failed:"
        for service in "${failed_services[@]}"; do
            print_error "  ✗ $service"
        done
        print_info "Check detailed reports at: $REPORTS_DIR/latest/index.html"
        exit 1
    fi
}

# Run main function
main
