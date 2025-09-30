#!/bin/bash

# Load Testing Script for Task Management Platform
# This script runs Apache JMeter load tests against the backend API

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
JMETER_HOME=${JMETER_HOME:-"/opt/apache-jmeter-5.6.2"}
TEST_PLAN="load-testing.jmx"
RESULTS_DIR="load-test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_FILE="${RESULTS_DIR}/load-test-${TIMESTAMP}.jtl"
REPORT_DIR="${RESULTS_DIR}/report-${TIMESTAMP}"

# Test scenarios
SCENARIOS=(
    "light:10:60:300"      # 10 users, 60s ramp, 5min duration
    "medium:25:120:600"     # 25 users, 2min ramp, 10min duration  
    "heavy:50:180:900"      # 50 users, 3min ramp, 15min duration
    "stress:100:300:1200"  # 100 users, 5min ramp, 20min duration
)

echo -e "${BLUE}🚀 Task Management Platform Load Testing${NC}"
echo "================================================"

# Check if JMeter is installed
if [ ! -d "$JMETER_HOME" ]; then
    echo -e "${RED}❌ JMeter not found at $JMETER_HOME${NC}"
    echo "Please install JMeter or set JMETER_HOME environment variable"
    echo "Download from: https://jmeter.apache.org/download_jmeter.cgi"
    exit 1
fi

# Check if backend is running
echo -e "${YELLOW}🔍 Checking if backend is running...${NC}"
if ! curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
    echo -e "${RED}❌ Backend is not running on localhost:8080${NC}"
    echo "Please start the backend server first:"
    echo "  cd server && ./mvnw spring-boot:run"
    exit 1
fi

echo -e "${GREEN}✅ Backend is running${NC}"

# Create results directory
mkdir -p "$RESULTS_DIR"

# Function to run a test scenario
run_test_scenario() {
    local scenario_name=$1
    local users=$2
    local ramp_time=$3
    local duration=$4
    
    echo -e "${BLUE}📊 Running scenario: $scenario_name${NC}"
    echo "  Users: $users"
    echo "  Ramp time: ${ramp_time}s"
    echo "  Duration: ${duration}s"
    
    local scenario_results="${RESULTS_DIR}/${scenario_name}-${TIMESTAMP}.jtl"
    local scenario_report="${RESULTS_DIR}/${scenario_name}-report-${TIMESTAMP}"
    
    # Update JMeter test plan with scenario parameters
    sed -i.bak "s/<stringProp name=\"ThreadGroup.num_threads\">.*<\/stringProp>/<stringProp name=\"ThreadGroup.num_threads\">$users<\/stringProp>/" "$TEST_PLAN"
    sed -i.bak "s/<stringProp name=\"ThreadGroup.ramp_time\">.*<\/stringProp>/<stringProp name=\"ThreadGroup.ramp_time\">$ramp_time<\/stringProp>/" "$TEST_PLAN"
    sed -i.bak "s/<stringProp name=\"ThreadGroup.duration\">.*<\/stringProp>/<stringProp name=\"ThreadGroup.duration\">$duration<\/stringProp>/" "$TEST_PLAN"
    
    # Run JMeter test
    echo -e "${YELLOW}⏳ Starting load test...${NC}"
    "$JMETER_HOME/bin/jmeter" \
        -n \
        -t "$TEST_PLAN" \
        -l "$scenario_results" \
        -e \
        -o "$scenario_report" \
        -Jusers=$users \
        -Jramp_time=$ramp_time \
        -Jduration=$duration
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Scenario $scenario_name completed successfully${NC}"
        echo "  Results: $scenario_results"
        echo "  Report: $scenario_report/index.html"
    else
        echo -e "${RED}❌ Scenario $scenario_name failed${NC}"
    fi
    
    # Restore original test plan
    mv "$TEST_PLAN.bak" "$TEST_PLAN"
}

# Function to generate performance report
generate_performance_report() {
    echo -e "${BLUE}📈 Generating performance report...${NC}"
    
    local report_file="${RESULTS_DIR}/performance-report-${TIMESTAMP}.md"
    
    cat > "$report_file" << EOF
# Task Management Platform - Load Testing Report

**Generated:** $(date)
**Test Duration:** $(date -d @$(( $(date +%s) - $start_time )) -u +%H:%M:%S)

## Test Scenarios

EOF

    for scenario in "${SCENARIOS[@]}"; do
        IFS=':' read -r name users ramp duration <<< "$scenario"
        echo "### $name Scenario" >> "$report_file"
        echo "- **Users:** $users" >> "$report_file"
        echo "- **Ramp Time:** ${ramp}s" >> "$report_file"
        echo "- **Duration:** ${duration}s" >> "$report_file"
        echo "" >> "$report_file"
    done

    echo -e "${GREEN}📊 Performance report generated: $report_file${NC}"
}

# Function to check system resources
check_system_resources() {
    echo -e "${BLUE}💻 System Resources${NC}"
    echo "CPU Usage: $(top -l 1 | grep "CPU usage" | awk '{print $3}')"
    echo "Memory Usage: $(top -l 1 | grep "PhysMem" | awk '{print $2}')"
    echo "Disk Usage: $(df -h . | tail -1 | awk '{print $5}')"
    echo ""
}

# Main execution
start_time=$(date +%s)

echo -e "${BLUE}🎯 Starting load testing scenarios...${NC}"
echo ""

# Check system resources before testing
check_system_resources

# Run each test scenario
for scenario in "${SCENARIOS[@]}"; do
    IFS=':' read -r name users ramp duration <<< "$scenario"
    run_test_scenario "$name" "$users" "$ramp" "$duration"
    echo ""
done

# Generate performance report
generate_performance_report

# Final system resource check
echo -e "${BLUE}💻 Final System Resources${NC}"
check_system_resources

echo -e "${GREEN}🎉 Load testing completed!${NC}"
echo "Results directory: $RESULTS_DIR"
echo ""

# Display summary
echo -e "${BLUE}📋 Test Summary${NC}"
echo "================"
for scenario in "${SCENARIOS[@]}"; do
    IFS=':' read -r name users ramp duration <<< "$scenario"
    echo "✅ $name: $users users, ${ramp}s ramp, ${duration}s duration"
done

echo ""
echo -e "${YELLOW}💡 Next Steps:${NC}"
echo "1. Review the generated HTML reports in $RESULTS_DIR"
echo "2. Analyze performance bottlenecks"
echo "3. Optimize backend based on results"
echo "4. Consider scaling strategies if needed"
