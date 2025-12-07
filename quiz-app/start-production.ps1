# Stop npm start
Write-Host "Stopping any existing Angular dev server..." -ForegroundColor Yellow
Get-Process -Name "node" -ErrorAction SilentlyContinue | Where-Object {$_.Path -like "*node.exe*"} | Stop-Process -Force -ErrorAction SilentlyContinue

# Wait a moment
Start-Sleep -Seconds 2

# Navigate to dist folder
$distPath = "c:\Users\Pavan Sidhhu\.gemini\antigravity\playground\ionic-pioneer\quiz-app\frontend\dist\frontend\browser"

if (!(Test-Path $distPath)) {
    Write-Host "Building production version..." -ForegroundColor Yellow
    cd "c:\Users\Pavan Sidhhu\.gemini\antigravity\playground\ionic-pioneer\quiz-app\frontend"
    npm run build
}

# Start http-server
Write-Host "Starting http-server on port 4200..." -ForegroundColor Green
cd $distPath
http-server -p 4200 --cors

Write-Host "Server is running at http://localhost:4200" -ForegroundColor Green
Write-Host "Press Ctrl+C to stop" -ForegroundColor Yellow
