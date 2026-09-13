#!/bin/bash



# Load environment variables if .env exists

if [ -f .env ]; then

  export $(grep -v '^#' .env | xargs)

fi



echo "--- Targetprocess API Validation (Direct CURL) ---"

if [ -n "$TP_URL" ] && [ -n "$TP_TOKEN" ]; then

  echo "1. Testing Connection & User Search..."

  curl -s "$TP_URL/api/v1/Users?access_token=$TP_TOKEN&take=1&format=json" | grep -q "Items" && echo "âœ… API Connection Successful" || echo "âŒ API Connection Failed"

  

  echo "2. Example: Search for user 'Aldo'..."

  # curl "$TP_URL/api/v1/Users?access_token=$TP_TOKEN&where=(Login contains 'aldo')&format=json"

else

  echo "âš ï¸ TP_URL or TP_TOKEN not set in .env. Skipping CURL tests."

fi



echo ""

echo "--- MCP Server Lifecycle ---"

# 1. Build the local JVM version (fastest for testing)

echo "Building Docker image..."

docker build --target jvm -t zdtp-mcp:local .



# 2. Run the local image

echo "Starting MCP Server (STDIO mode)..."

echo "Note: You can paste JSON-RPC calls here or use an MCP inspector."

docker run -i --rm \

  -e TP_URL="$TP_URL" \

  -e TP_TOKEN="$TP_TOKEN" \

  zdtp-mcp:local