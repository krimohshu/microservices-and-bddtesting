#!/bin/bash

# Spring Boot API Testing - Test Execution Script
# This script runs the test suite with various options

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Print banner
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Spring Boot API Testing Framework${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Function to print section headers
print_header() {
    echo -e "${YELLOW}>>> $1${NC}"
}

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Error: Maven is not installed${NC}"
    exit 1
fi

# Parse command line arguments
CLEAN=false
PARALLEL=true
REPORTS=true

while [[ $# -gt 0 ]]; do
    case $1 in
        --clean)
            CLEAN=true
            shift
            ;;
        --no-parallel)
            PARALLEL=false
            shift
            ;;
        --no-reports)
            REPORTS=false
            shift
            ;;
        --help)
            echo "Usage: ./run-tests.sh [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --clean         Clean build before running tests"
            echo "  --no-parallel   Disable parallel test execution"
            echo "  --no-reports    Don't open HTML report after tests"
            echo "  --help          Show this help message"
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Clean build if requested
if [ "$CLEAN" = true ]; then
    print_header "Cleaning build directory..."
    mvn clean
    echo ""
fi

# Set parallel execution property
if [ "$PARALLEL" = false ]; then
    print_header "Running tests sequentially..."
    PARALLEL_PROP="-Dcucumber.execution.parallel.enabled=false"
else
    print_header "Running tests with parallel execution..."
    PARALLEL_PROP=""
fi

# Run tests
print_header "Executing test suite..."
if mvn test $PARALLEL_PROP; then
    echo ""
    echo -e "${GREEN}✓ All tests passed successfully!${NC}"
    EXIT_CODE=0
else
    echo ""
    echo -e "${RED}✗ Some tests failed${NC}"
    EXIT_CODE=1
fi

# Print test results location
echo ""
print_header "Test Reports"
echo "Cucumber HTML: target/cucumber-reports/cucumber.html"
echo "Cucumber JSON: target/cucumber-reports/cucumber.json"
echo "Surefire Reports: target/surefire-reports/"

# Open HTML report if requested
if [ "$REPORTS" = true ] && [ -f "target/cucumber-reports/cucumber.html" ]; then
    print_header "Opening HTML report..."
    
    # Detect OS and open report
    if [[ "$OSTYPE" == "darwin"* ]]; then
        open target/cucumber-reports/cucumber.html
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        xdg-open target/cucumber-reports/cucumber.html 2>/dev/null || echo "Please open target/cucumber-reports/cucumber.html manually"
    elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
        start target/cucumber-reports/cucumber.html
    fi
fi

echo ""
echo -e "${GREEN}========================================${NC}"
exit $EXIT_CODE
