# Quick API Testing Guide

## The Issue
The frontend is still the default Angular template - the actual quiz app UI hasn't been built yet.

## Solution: Test Using API Directly

### Option 1: Using PowerShell

#### Login as Admin:
```powershell
$body = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:9090/api/auth/login" -Method POST -ContentType "application/json" -Body $body | ConvertTo-Json
```

#### Login as Student:
```powershell
$body = @{
    username = "student"
    password = "student123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:9090/api/auth/login" -Method POST -ContentType "application/json" -Body $body | ConvertTo-Json
```

### Option 2: Use Postman or Insomnia

1. Create a POST request to `http://localhost:9090/api/auth/login`
2. Set Headers: `Content-Type: application/json  `
3. Set Body (raw JSON):
```json
{
    "username": "admin",
    "password": "admin123"
}
```
4. Send the request

## Expected Response
```json
{
    "id": "...",
    "username": "admin",
    "password": "admin123",
    "role": "ADMIN"
}
```

## Next Steps
Would you like me to build the actual frontend UI with login/register forms?
