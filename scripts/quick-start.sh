#!/bin/bash

# Quick Start Script for Task Management Platform
# This script provides a fast way to get the entire system running

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Task Management Platform - Quick Start${NC}"
echo "=============================================="
echo ""

# Check if required tools are installed
check_requirements() {
    echo -e "${YELLOW}🔍 Checking requirements...${NC}"
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        echo -e "${RED}❌ Node.js is not installed${NC}"
        echo "Please install Node.js from https://nodejs.org/"
        exit 1
    fi
    
    # Check Java
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java is not installed${NC}"
        echo "Please install Java 17 or higher"
        exit 1
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null && [ ! -f "./server/mvnw" ]; then
        echo -e "${RED}❌ Maven is not installed${NC}"
        echo "Please install Maven or ensure mvnw is available"
        exit 1
    fi
    
    echo -e "${GREEN}✅ All requirements satisfied${NC}"
}

# Install dependencies
install_dependencies() {
    echo -e "${YELLOW}📦 Installing dependencies...${NC}"
    
    # Frontend dependencies
    if [ ! -d "client/node_modules" ]; then
        echo -e "${YELLOW}Installing frontend dependencies...${NC}"
        cd client && npm install && cd ..
    fi
    
    # Backend dependencies
    if [ ! -d "server/target" ]; then
        echo -e "${YELLOW}Installing backend dependencies...${NC}"
        cd server && ./mvnw clean install -DskipTests && cd ..
    fi
    
    echo -e "${GREEN}✅ Dependencies installed${NC}"
}

# Start backend
start_backend() {
    echo -e "${YELLOW}🚀 Starting backend server...${NC}"
    cd server
    
    # Start backend in background
    nohup ./mvnw spring-boot:run > ../backend.log 2>&1 &
    BACKEND_PID=$!
    echo $BACKEND_PID > ../backend.pid
    
    cd ..
    
    # Wait for backend to start
    echo -e "${YELLOW}⏳ Waiting for backend to start...${NC}"
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Backend started successfully${NC}"
            break
        fi
        sleep 2
    done
    
    if [ $i -eq 30 ]; then
        echo -e "${RED}❌ Backend failed to start within 60 seconds${NC}"
        echo "Check backend.log for details"
        exit 1
    fi
}

# Start frontend
start_frontend() {
    echo -e "${YELLOW}🚀 Starting frontend server...${NC}"
    cd client
    
    # Start frontend in background
    nohup npm start > ../frontend.log 2>&1 &
    FRONTEND_PID=$!
    echo $FRONTEND_PID > ../frontend.pid
    
    cd ..
    
    # Wait for frontend to start
    echo -e "${YELLOW}⏳ Waiting for frontend to start...${NC}"
    for i in {1..30}; do
        if curl -s http://localhost:4200 > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Frontend started successfully${NC}"
            break
        fi
        sleep 2
    done
    
    if [ $i -eq 30 ]; then
        echo -e "${RED}❌ Frontend failed to start within 60 seconds${NC}"
        echo "Check frontend.log for details"
        exit 1
    fi
}

# Show system status
show_status() {
    echo ""
    echo -e "${BLUE}📊 System Status${NC}"
    echo "=================="
    echo -e "${GREEN}✅ Backend API: http://localhost:8080${NC}"
    echo -e "${GREEN}✅ Frontend App: http://localhost:4200${NC}"
    echo -e "${GREEN}✅ H2 Database Console: http://localhost:8080/h2-console${NC}"
    echo -e "${GREEN}✅ API Documentation: http://localhost:8080/swagger-ui.html${NC}"
    echo ""
    echo -e "${YELLOW}📝 Logs:${NC}"
    echo "- Backend: tail -f backend.log"
    echo "- Frontend: tail -f frontend.log"
    echo ""
    echo -e "${YELLOW}🛑 To stop the system:${NC}"
    echo "- Backend: kill \$(cat backend.pid)"
    echo "- Frontend: kill \$(cat frontend.pid)"
    echo "- Or run: ./scripts/stop-system.sh"
    echo ""
}

# Cleanup function
cleanup() {
    echo -e "${YELLOW}🧹 Cleaning up...${NC}"
    
    # Stop backend
    if [ -f "backend.pid" ]; then
        BACKEND_PID=$(cat backend.pid)
        if kill -0 $BACKEND_PID 2>/dev/null; then
            kill $BACKEND_PID
            echo -e "${GREEN}✅ Backend stopped${NC}"
        fi
        rm -f backend.pid
    fi
    
    # Stop frontend
    if [ -f "frontend.pid" ]; then
        FRONTEND_PID=$(cat frontend.pid)
        if kill -0 $FRONTEND_PID 2>/dev/null; then
            kill $FRONTEND_PID
            echo -e "${GREEN}✅ Frontend stopped${NC}"
        fi
        rm -f frontend.pid
    fi
}

# Set up trap for cleanup
trap cleanup EXIT

# Main execution
main() {
    check_requirements
    install_dependencies
    start_backend
    start_frontend
    show_status
    
    echo -e "${GREEN}🎉 Task Management Platform is now running!${NC}"
    echo ""
    echo -e "${BLUE}🌐 Open your browser and visit:${NC}"
    echo "   Frontend: http://localhost:4200"
    echo "   Backend API: http://localhost:8080"
    echo "   API Docs: http://localhost:8080/swagger-ui.html"
    echo ""
    echo -e "${YELLOW}💡 Next steps:${NC}"
    echo "1. Open http://localhost:4200 in your browser"
    echo "2. Register a new account or use test credentials"
    echo "3. Explore the task management features"
    echo "4. Check the API documentation at http://localhost:8080/swagger-ui.html"
    echo ""
    echo -e "${BLUE}📚 Documentation:${NC}"
    echo "- Integration Guide: docs/integration-guide.md"
    echo "- Load Testing: docs/load-testing.md"
    echo "- E2E Testing: docs/e2e-testing.md"
    echo ""
    echo -e "${YELLOW}Press Ctrl+C to stop the system${NC}"
    
    # Keep script running
    while true; do
        sleep 1
    done
}

# Run main function
main "$@"
