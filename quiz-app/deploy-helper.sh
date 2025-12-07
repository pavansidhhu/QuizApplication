#!/bin/bash

# Quiz App Deployment Helper Script
# This script helps you prepare your app for deployment

echo "🚀 Quiz App Deployment Helper"
echo "=============================="
echo ""

# Check if MongoDB URI is provided
if [ -z "$1" ]; then
    echo "❌ Error: MongoDB URI not provided"
    echo ""
    echo "Usage: ./deploy-helper.sh <MONGODB_URI> <BACKEND_URL>"
    echo ""
    echo "Example:"
    echo "./deploy-helper.sh 'mongodb+srv://user:pass@cluster.mongodb.net/quizapp' 'https://quiz-app-backend.onrender.com'"
    echo ""
    exit 1
fi

# Check if Backend URL is provided
if [ -z "$2" ]; then
    echo "❌ Error: Backend URL not provided"
    echo ""
    echo "Usage: ./deploy-helper.sh <MONGODB_URI> <BACKEND_URL>"
    echo ""
    exit 1
fi

MONGODB_URI=$1
BACKEND_URL=$2

echo "✅ MongoDB URI: ${MONGODB_URI:0:30}..."
echo "✅ Backend URL: $BACKEND_URL"
echo ""

# Update frontend environment
echo "📝 Updating frontend production environment..."
sed -i "s|https://YOUR_BACKEND_URL.onrender.com|$BACKEND_URL|g" quiz-app/frontend/src/environments/environment.prod.ts

echo "✅ Frontend environment updated!"
echo ""

echo "📋 Next Steps:"
echo "1. Commit and push changes:"
echo "   git add ."
echo "   git commit -m 'Configure for production deployment'"
echo "   git push"
echo ""
echo "2. Deploy backend to Render with these settings:"
echo "   - Root Directory: quiz-app/backend"
echo "   - Build Command: mvn clean install"
echo "   - Start Command: java -jar target/backend-0.0.1-SNAPSHOT.jar"
echo "   - Environment Variable: MONGODB_URI=$MONGODB_URI"
echo ""
echo "3. Deploy frontend to Render with these settings:"
echo "   - Root Directory: quiz-app/frontend"
echo "   - Build Command: npm install && npm run build"
echo "   - Publish Directory: dist/frontend/browser"
echo ""
echo "🎉 Your app is ready for deployment!"
