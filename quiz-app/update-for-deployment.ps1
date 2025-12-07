# Quiz App - Render.com Deployment Checklist
# Run this after you have your MongoDB Atlas URI and Backend URL from Render

param(
    [Parameter(Mandatory=$true)]
    [string]$MongoDbUri,
    
    [Parameter(Mandatory=$true)]
    [string]$BackendUrl
)

Write-Host "🚀 Quiz App Deployment Helper" -ForegroundColor Cyan
Write-Host "==============================" -ForegroundColor Cyan
Write-Host ""

# Validate inputs
if ([string]::IsNullOrWhiteSpace($MongoDbUri)) {
    Write-Host "❌ Error: MongoDB URI is required" -ForegroundColor Red
    Write-Host ""
    Write-Host "Usage: .\update-for-deployment.ps1 -MongoDbUri 'your-mongodb-uri' -BackendUrl 'your-backend-url'" -ForegroundColor Yellow
    exit 1
}

if ([string]::IsNullOrWhiteSpace($BackendUrl)) {
    Write-Host "❌ Error: Backend URL is required" -ForegroundColor Red
    Write-Host ""
    Write-Host "Usage: .\update-for-deployment.ps1 -MongoDbUri 'your-mongodb-uri' -BackendUrl 'your-backend-url'" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ MongoDB URI: $($MongoDbUri.Substring(0, [Math]::Min(30, $MongoDbUri.Length)))..." -ForegroundColor Green
Write-Host "✅ Backend URL: $BackendUrl" -ForegroundColor Green
Write-Host ""

# Update frontend environment file
Write-Host "📝 Updating frontend production environment..." -ForegroundColor Yellow

$envProdFile = ".\frontend\src\environments\environment.prod.ts"
$content = Get-Content $envProdFile -Raw
$content = $content -replace 'https://YOUR_BACKEND_URL\.onrender\.com', $BackendUrl
Set-Content $envProdFile -Value $content

Write-Host "✅ Frontend environment updated!" -ForegroundColor Green
Write-Host ""

# Update CORS configuration
Write-Host "📝 Updating backend CORS configuration..." -ForegroundColor Yellow

$corsFile = ".\backend\src\main\java\com\quizapp\backend\config\CorsConfig.java"
$corsContent = Get-Content $corsFile -Raw

# Extract frontend URL from backend URL
$frontendUrl = $BackendUrl -replace 'backend', 'frontend'

# Check if the frontend URL is already in CORS config
if ($corsContent -notmatch [regex]::Escape($frontendUrl)) {
    # Add the frontend URL to allowed origins
    $corsContent = $corsContent -replace '("https://seven-areas-cut\.loca\.lt")', "`$1,`n                                `"$frontendUrl`""
    Set-Content $corsFile -Value $corsContent
    Write-Host "✅ Added $frontendUrl to CORS configuration!" -ForegroundColor Green
} else {
    Write-Host "✅ Frontend URL already in CORS configuration!" -ForegroundColor Green
}

Write-Host ""
Write-Host "📋 Deployment Configuration Complete!" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "🔄 Next Steps:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Commit and push your changes:" -ForegroundColor White
Write-Host "   git add ." -ForegroundColor Gray
Write-Host "   git commit -m 'Configure for production deployment'" -ForegroundColor Gray
Write-Host "   git push" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Deploy Backend to Render.com:" -ForegroundColor White
Write-Host "   - Go to https://render.com" -ForegroundColor Gray
Write-Host "   - Create New Web Service" -ForegroundColor Gray
Write-Host "   - Connect your GitHub repo" -ForegroundColor Gray
Write-Host "   - Root Directory: quiz-app/backend" -ForegroundColor Gray
Write-Host "   - Build Command: mvn clean install" -ForegroundColor Gray
Write-Host "   - Start Command: java -jar target/backend-0.0.1-SNAPSHOT.jar" -ForegroundColor Gray
Write-Host "   - Add Environment Variable:" -ForegroundColor Gray
Write-Host "     MONGODB_URI = $MongoDbUri" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Deploy Frontend to Render.com:" -ForegroundColor White
Write-Host "   - Create New Static Site" -ForegroundColor Gray
Write-Host "   - Connect your GitHub repo" -ForegroundColor Gray
Write-Host "   - Root Directory: quiz-app/frontend" -ForegroundColor Gray
Write-Host "   - Build Command: npm install && npm run build" -ForegroundColor Gray
Write-Host "   - Publish Directory: dist/frontend/browser" -ForegroundColor Gray
Write-Host ""
Write-Host "🎉 Your app will be live shortly!" -ForegroundColor Green
Write-Host ""
Write-Host "📖 For detailed instructions, see DEPLOYMENT_GUIDE.md" -ForegroundColor Cyan
