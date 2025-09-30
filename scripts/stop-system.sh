#!/bin/bash

# Stop System Script for Task Management Platform
# This script stops all running services

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🛑 Stopping Task Management Platform${NC}"
echo "=========================================="
echo ""

# Function to stop service by PID file
stop_service() {
    local service_name=$1
    local pid_file=$2
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if kill -0 $pid 2>/dev/null; then
            echo -e "${YELLOW}Stopping $service_name (PID: $pid)...${NC}"
            kill $pid
            sleep 2
            
            # Force kill if still running
            if kill -0 $pid 2>/dev/null; then
                echo -e "${YELLOW}Force stopping $service_name...${NC}"
                kill -9 $pid
            fi
            
            echo -e "${GREEN}✅ $service_name stopped${NC}"
        else
            echo -e "${YELLOW}⚠️  $service_name was not running${NC}"
        fi
        rm -f "$pid_file"
    else
        echo -e "${YELLOW}⚠️  No PID file found for $service_name${NC}"
    fi
}

# Function to stop service by port
stop_service_by_port() {
    local service_name=$1
    local port=$2
    
    local pid=$(lsof -ti:$port 2>/dev/null || echo "")
    if [ ! -z "$pid" ]; then
        echo -e "${YELLOW}Stopping $service_name on port $port (PID: $pid)...${NC}"
        kill $pid
        sleep 2
        
        # Force kill if still running
        if kill -0 $pid 2>/dev/null; then
            echo -e "${YELLOW}Force stopping $service_name...${NC}"
            kill -9 $pid
        fi
        
        echo -e "${GREEN}✅ $service_name stopped${NC}"
    else
        echo -e "${YELLOW}⚠️  No service running on port $port${NC}"
    fi
}

# Stop services by PID files
echo -e "${YELLOW}🔍 Checking for PID files...${NC}"
stop_service "Backend" "backend.pid"
stop_service "Frontend" "frontend.pid"

# Stop services by port (fallback)
echo -e "${YELLOW}🔍 Checking for services by port...${NC}"
stop_service_by_port "Backend" "8080"
stop_service_by_port "Frontend" "4200"

# Stop any remaining Node.js processes related to the project
echo -e "${YELLOW}🔍 Checking for remaining Node.js processes...${NC}"
NODE_PIDS=$(ps aux | grep "node.*ng serve\|node.*npm start" | grep -v grep | awk '{print $2}' || echo "")
if [ ! -z "$NODE_PIDS" ]; then
    echo -e "${YELLOW}Stopping remaining Node.js processes...${NC}"
    echo $NODE_PIDS | xargs kill 2>/dev/null || true
    echo -e "${GREEN}✅ Node.js processes stopped${NC}"
fi

# Stop any remaining Java processes related to the project
echo -e "${YELLOW}🔍 Checking for remaining Java processes...${NC}"
JAVA_PIDS=$(ps aux | grep "java.*spring-boot" | grep -v grep | awk '{print $2}' || echo "")
if [ ! -z "$JAVA_PIDS" ]; then
    echo -e "${YELLOW}Stopping remaining Java processes...${NC}"
    echo $JAVA_PIDS | xargs kill 2>/dev/null || true
    echo -e "${GREEN}✅ Java processes stopped${NC}"
fi

# Clean up log files
echo -e "${YELLOW}🧹 Cleaning up log files...${NC}"
if [ -f "backend.log" ]; then
    rm -f backend.log
    echo -e "${GREEN}✅ Backend log file removed${NC}"
fi

if [ -f "frontend.log" ]; then
    rm -f frontend.log
    echo -e "${GREEN}✅ Frontend log file removed${NC}"
fi

# Final status check
echo ""
echo -e "${BLUE}📊 Final Status Check${NC}"
echo "======================"

# Check if ports are still in use
if lsof -ti:8080 > /dev/null 2>&1; then
    echo -e "${RED}❌ Port 8080 is still in use${NC}"
    echo "   Run: lsof -ti:8080 | xargs kill -9"
else
    echo -e "${GREEN}✅ Port 8080 is free${NC}"
fi

if lsof -ti:4200 > /dev/null 2>&1; then
    echo -e "${RED}❌ Port 4200 is still in use${NC}"
    echo "   Run: lsof -ti:4200 | xargs kill -9"
else
    echo -e "${GREEN}✅ Port 4200 is free${NC}"
fi

echo ""
echo -e "${GREEN}🎉 Task Management Platform stopped successfully!${NC}"
echo ""
echo -e "${YELLOW}💡 To start the system again:${NC}"
echo "   ./scripts/quick-start.sh"
echo ""
echo -e "${YELLOW}📚 For more information:${NC}"
echo "   - Integration Guide: docs/integration-guide.md"
echo "   - Load Testing: docs/load-testing.md"
echo "   - E2E Testing: docs/e2e-testing.md"
