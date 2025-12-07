$body = @{
    username = "testadmin"
    password = "password123"
    role = "ADMIN"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://127.0.0.1:9091/api/auth/register" `
    -Method POST `
    -Body $body `
    -ContentType "application/json" `
    -UseBasicParsing

Write-Host "Status Code: $($response.StatusCode)"
Write-Host "Response: $($response.Content)"
