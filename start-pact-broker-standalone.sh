#!/bin/bash

# Pact Broker Standalone Runner
# This uses the standalone Pact Broker without Docker

set -e

PACT_STANDALONE_VERSION="2.4.7"
PACT_DIR="$HOME/.pact"
PACT_BROKER_DIR="$PACT_DIR/pact-broker-standalone-$PACT_STANDALONE_VERSION"
DOWNLOAD_URL="https://github.com/pact-foundation/pact-ruby-standalone/releases/download/v${PACT_STANDALONE_VERSION}/pact-${PACT_STANDALONE_VERSION}-osx-arm64.tar.gz"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}  Pact Broker Standalone Setup${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Check if already downloaded
if [ ! -d "$PACT_BROKER_DIR" ]; then
    echo -e "${YELLOW}Downloading Pact Standalone...${NC}"
    mkdir -p "$PACT_DIR"
    cd "$PACT_DIR"
    curl -LO "$DOWNLOAD_URL"
    tar -xzf "pact-${PACT_STANDALONE_VERSION}-osx-arm64.tar.gz"
    rm "pact-${PACT_STANDALONE_VERSION}-osx-arm64.tar.gz"
    echo -e "${GREEN}✓ Downloaded successfully${NC}"
else
    echo -e "${GREEN}✓ Pact Standalone already installed${NC}"
fi

echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}  Alternative: Use Pactflow Free Account${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "Instead of running a local broker, you can use Pactflow's free tier:"
echo ""
echo "1. Sign up at: https://pactflow.io/try-for-free/"
echo "2. Get your broker URL and API token"
echo "3. Update pom.xml with your broker URL"
echo ""
echo -e "${YELLOW}Benefits of Pactflow:${NC}"
echo "  ✓ No Docker/infrastructure needed"
echo "  ✓ Free for open source"
echo "  ✓ Beautiful UI with insights"
echo "  ✓ CI/CD integration examples"
echo "  ✓ Can-i-deploy checks"
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "Press any key to continue..."
read -n 1 -s
