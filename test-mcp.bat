@echo off
echo Starting MCP Spring Bridge...
start /B gradlew bootRun

echo Waiting for application to start...
timeout /t 15 /nobreak > nul

echo Testing MCP endpoints...
echo.
echo === MCP Tools ===
curl -s http://localhost:8081/mcp/tools | jq .

echo.
echo === Test API Call ===
curl -s -X POST http://localhost:8081/mcp/call ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"getUsers\",\"arguments\":{\"method\":\"GET\",\"path\":\"/api/users\"}}" | jq .

echo.
echo === OpenAPI Docs ===
curl -s http://localhost:8081/v3/api-docs | jq .info

pause