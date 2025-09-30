#!/bin/bash

# Comprehensive Testing Script for Task Management Platform
# This script runs all types of tests: Unit, Integration, E2E, and Load tests

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RESULTS_DIR="$PROJECT_ROOT/test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
REPORT_DIR="$RESULTS_DIR/reports-$TIMESTAMP"

echo -e "${BLUE}🚀 Task Management Platform - Comprehensive Testing${NC}"
echo "=================================================="
echo "Project Root: $PROJECT_ROOT"
echo "Results Directory: $RESULTS_DIR"
echo "Report Directory: $REPORT_DIR"
echo ""

# Create results directory
mkdir -p "$RESULTS_DIR"
mkdir -p "$REPORT_DIR"

# Function to print section headers
print_section() {
    echo ""
    echo -e "${PURPLE}📋 $1${NC}"
    echo "=================================================="
}

# Function to check if service is running
check_service() {
    local service_name=$1
    local port=$2
    local url=$3
    
    echo -e "${YELLOW}🔍 Checking $service_name on port $port...${NC}"
    
    if curl -s "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $service_name is running${NC}"
        return 0
    else
        echo -e "${RED}❌ $service_name is not running on port $port${NC}"
        return 1
    fi
}

# Function to start backend if not running
start_backend() {
    if ! check_service "Backend API" "8080" "http://localhost:8080/api/health"; then
        echo -e "${YELLOW}🚀 Starting backend server...${NC}"
        cd "$PROJECT_ROOT/server"
        
        # Start backend in background
        nohup ./mvnw spring-boot:run > "$RESULTS_DIR/backend.log" 2>&1 &
        BACKEND_PID=$!
        echo $BACKEND_PID > "$RESULTS_DIR/backend.pid"
        
        # Wait for backend to start
        echo -e "${YELLOW}⏳ Waiting for backend to start...${NC}"
        for i in {1..30}; do
            if check_service "Backend API" "8080" "http://localhost:8080/api/health"; then
                echo -e "${GREEN}✅ Backend started successfully${NC}"
                break
            fi
            sleep 2
        done
        
        if [ $i -eq 30 ]; then
            echo -e "${RED}❌ Backend failed to start within 60 seconds${NC}"
            exit 1
        fi
    fi
}

# Function to start frontend if not running
start_frontend() {
    if ! check_service "Frontend" "4200" "http://localhost:4200"; then
        echo -e "${YELLOW}🚀 Starting frontend server...${NC}"
        cd "$PROJECT_ROOT/client"
        
        # Start frontend in background
        nohup npm start > "$RESULTS_DIR/frontend.log" 2>&1 &
        FRONTEND_PID=$!
        echo $FRONTEND_PID > "$RESULTS_DIR/frontend.pid"
        
        # Wait for frontend to start
        echo -e "${YELLOW}⏳ Waiting for frontend to start...${NC}"
        for i in {1..30}; do
            if check_service "Frontend" "4200" "http://localhost:4200"; then
                echo -e "${GREEN}✅ Frontend started successfully${NC}"
                break
            fi
            sleep 2
        done
        
        if [ $i -eq 30 ]; then
            echo -e "${RED}❌ Frontend failed to start within 60 seconds${NC}"
            exit 1
        fi
    fi
}

# Function to run backend tests
run_backend_tests() {
    print_section "Backend Tests (Java/Spring Boot)"
    
    cd "$PROJECT_ROOT/server"
    
    echo -e "${YELLOW}🧪 Running unit tests...${NC}"
    ./mvnw test -Dtest="**/*Test" > "$RESULTS_DIR/backend-unit-tests.log" 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Backend unit tests passed${NC}"
    else
        echo -e "${RED}❌ Backend unit tests failed${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}🧪 Running integration tests...${NC}"
    ./mvnw test -Dtest="**/*IntegrationTest" > "$RESULTS_DIR/backend-integration-tests.log" 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Backend integration tests passed${NC}"
    else
        echo -e "${RED}❌ Backend integration tests failed${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}📊 Generating test coverage report...${NC}"
    ./mvnw jacoco:report > "$RESULTS_DIR/backend-coverage.log" 2>&1
    
    if [ -f "target/site/jacoco/index.html" ]; then
        cp -r target/site/jacoco "$REPORT_DIR/backend-coverage"
        echo -e "${GREEN}✅ Backend coverage report generated${NC}"
    fi
}

# Function to run frontend tests
run_frontend_tests() {
    print_section "Frontend Tests (Angular/TypeScript)"
    
    cd "$PROJECT_ROOT/client"
    
    echo -e "${YELLOW}🧪 Running unit tests...${NC}"
    npm run test -- --watch=false --browsers=ChromeHeadless > "$RESULTS_DIR/frontend-unit-tests.log" 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Frontend unit tests passed${NC}"
    else
        echo -e "${RED}❌ Frontend unit tests failed${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}🧪 Running integration tests...${NC}"
    npm run test:integration > "$RESULTS_DIR/frontend-integration-tests.log" 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Frontend integration tests passed${NC}"
    else
        echo -e "${RED}❌ Frontend integration tests failed${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}📊 Generating test coverage report...${NC}"
    npm run test:coverage > "$RESULTS_DIR/frontend-coverage.log" 2>&1
    
    if [ -d "coverage" ]; then
        cp -r coverage "$REPORT_DIR/frontend-coverage"
        echo -e "${GREEN}✅ Frontend coverage report generated${NC}"
    fi
}

# Function to run E2E tests
run_e2e_tests() {
    print_section "E2E Tests (Cypress)"
    
    cd "$PROJECT_ROOT/client"
    
    echo -e "${YELLOW}🧪 Running E2E tests...${NC}"
    npx cypress run --reporter html --reporter-options output="$REPORT_DIR/cypress-report.html" > "$RESULTS_DIR/e2e-tests.log" 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ E2E tests passed${NC}"
    else
        echo -e "${RED}❌ E2E tests failed${NC}"
        return 1
    fi
    
    # Copy screenshots and videos if they exist
    if [ -d "cypress/screenshots" ]; then
        cp -r cypress/screenshots "$REPORT_DIR/cypress-screenshots"
    fi
    
    if [ -d "cypress/videos" ]; then
        cp -r cypress/videos "$REPORT_DIR/cypress-videos"
    fi
}

# Function to run load tests
run_load_tests() {
    print_section "Load Tests (JMeter)"
    
    cd "$PROJECT_ROOT"
    
    echo -e "${YELLOW}🧪 Running load tests...${NC}"
    ./scripts/run-load-tests.sh > "$RESULTS_DIR/load-tests.log" 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Load tests completed${NC}"
    else
        echo -e "${RED}❌ Load tests failed${NC}"
        return 1
    fi
    
    # Copy load test results
    if [ -d "load-test-results" ]; then
        cp -r load-test-results "$REPORT_DIR/load-test-results"
    fi
}

# Function to generate comprehensive report
generate_comprehensive_report() {
    print_section "Generating Comprehensive Report"
    
    local report_file="$REPORT_DIR/comprehensive-test-report.md"
    
    cat > "$report_file" << EOF
# Task Management Platform - Comprehensive Test Report

**Generated:** $(date)
**Test Duration:** $(date -d @$(( $(date +%s) - $start_time )) -u +%H:%M:%S)

## Test Summary

### Backend Tests
- **Unit Tests**: $(grep -c "✅" "$RESULTS_DIR/backend-unit-tests.log" 2>/dev/null || echo "N/A") passed
- **Integration Tests**: $(grep -c "✅" "$RESULTS_DIR/backend-integration-tests.log" 2>/dev/null || echo "N/A") passed
- **Coverage Report**: [Backend Coverage Report](./backend-coverage/index.html)

### Frontend Tests
- **Unit Tests**: $(grep -c "✅" "$RESULTS_DIR/frontend-unit-tests.log" 2>/dev/null || echo "N/A") passed
- **Integration Tests**: $(grep -c "✅" "$RESULTS_DIR/frontend-integration-tests.log" 2>/dev/null || echo "N/A") passed
- **Coverage Report**: [Frontend Coverage Report](./frontend-coverage/index.html)

### E2E Tests
- **Cypress Tests**: $(grep -c "✅" "$RESULTS_DIR/e2e-tests.log" 2>/dev/null || echo "N/A") passed
- **Screenshots**: [Cypress Screenshots](./cypress-screenshots/)
- **Videos**: [Cypress Videos](./cypress-videos/)

### Load Tests
- **JMeter Tests**: $(grep -c "✅" "$RESULTS_DIR/load-tests.log" 2>/dev/null || echo "N/A") completed
- **Load Test Results**: [Load Test Results](./load-test-results/)

## System Resources

### Before Testing
\`\`\`
$(cat "$RESULTS_DIR/system-resources-before.txt" 2>/dev/null || echo "N/A")
\`\`\`

### After Testing
\`\`\`
$(cat "$RESULTS_DIR/system-resources-after.txt" 2>/dev/null || echo "N/A")
\`\`\`

## Recommendations

1. **Performance**: Review load test results and optimize bottlenecks
2. **Coverage**: Aim for >80% test coverage across all modules
3. **E2E**: Ensure all critical user paths are covered
4. **Monitoring**: Set up continuous monitoring for production

## Next Steps

1. Review all test reports
2. Address any failing tests
3. Optimize performance based on load test results
4. Update documentation based on test findings
5. Plan for production deployment

EOF

    echo -e "${GREEN}📊 Comprehensive report generated: $report_file${NC}"
}

# Function to cleanup
cleanup() {
    echo -e "${YELLOW}🧹 Cleaning up...${NC}"
    
    # Stop backend if we started it
    if [ -f "$RESULTS_DIR/backend.pid" ]; then
        BACKEND_PID=$(cat "$RESULTS_DIR/backend.pid")
        if kill -0 $BACKEND_PID 2>/dev/null; then
            kill $BACKEND_PID
            echo -e "${GREEN}✅ Backend stopped${NC}"
        fi
        rm -f "$RESULTS_DIR/backend.pid"
    fi
    
    # Stop frontend if we started it
    if [ -f "$RESULTS_DIR/frontend.pid" ]; then
        FRONTEND_PID=$(cat "$RESULTS_DIR/frontend.pid")
        if kill -0 $FRONTEND_PID 2>/dev/null; then
            kill $FRONTEND_PID
            echo -e "${GREEN}✅ Frontend stopped${NC}"
        fi
        rm -f "$RESULTS_DIR/frontend.pid"
    fi
}

# Function to check system resources
check_system_resources() {
    local output_file=$1
    echo "CPU Usage: $(top -l 1 | grep "CPU usage" | awk '{print $3}')" > "$output_file"
    echo "Memory Usage: $(top -l 1 | grep "PhysMem" | awk '{print $2}')" >> "$output_file"
    echo "Disk Usage: $(df -h . | tail -1 | awk '{print $5}')" >> "$output_file"
    echo "Load Average: $(uptime | awk -F'load averages:' '{print $2}')" >> "$output_file"
}

# Main execution
start_time=$(date +%s)

# Set up trap for cleanup
trap cleanup EXIT

# Check system resources before testing
check_system_resources "$RESULTS_DIR/system-resources-before.txt"

# Start services if needed
start_backend
start_frontend

# Run all tests
run_backend_tests
run_frontend_tests
run_e2e_tests
run_load_tests

# Check system resources after testing
check_system_resources "$RESULTS_DIR/system-resources-after.txt"

# Generate comprehensive report
generate_comprehensive_report

# Final summary
echo ""
echo -e "${GREEN}🎉 Comprehensive testing completed!${NC}"
echo "=================================================="
echo "Results directory: $RESULTS_DIR"
echo "Report directory: $REPORT_DIR"
echo ""

# Display summary
echo -e "${BLUE}📋 Test Summary${NC}"
echo "================"
echo "✅ Backend Tests: Unit + Integration"
echo "✅ Frontend Tests: Unit + Integration"
echo "✅ E2E Tests: Cypress"
echo "✅ Load Tests: JMeter"
echo "✅ Coverage Reports: Generated"
echo "✅ Comprehensive Report: Generated"
echo ""

echo -e "${YELLOW}💡 Next Steps:${NC}"
echo "1. Review the comprehensive report: $REPORT_DIR/comprehensive-test-report.md"
echo "2. Check individual test results in $RESULTS_DIR"
echo "3. Address any failing tests"
echo "4. Optimize performance based on load test results"
echo "5. Plan for production deployment"
