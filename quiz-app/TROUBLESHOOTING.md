# Quiz App - Troubleshooting Guide

## Current Issue: Login Stuck on "Logging in..."

### Root Cause
The issue is caused by **duplicate user records** in MongoDB. When you registered the same username multiple times during testing, MongoDB created multiple documents with the same username, causing the backend to crash with `IncorrectResultSizeDataAccessException`.

### Solution Applied
I've updated the backend code to:
1. Handle multiple users with the same username
2. Automatically delete duplicates (keeping only the first one)
3. Prevent future crashes

### Steps to Fix Your Current Issue

#### Step 1: Clear Your Database (Recommended)
The fastest solution is to start fresh:

```powershell
# Connect to MongoDB and drop the users collection
mongosh
use quizapp
db.users.drop()
exit
```

#### Step 2: Restart Backend
Make sure the backend is running with the FIXED code:

```powershell
cd c:\Users\Pavan Sidhhu\.gemini\antigravity\playground\ionic-pioneer\quiz-app\backend
$env:JAVA_HOME='C:\Program Files\Java\jdk-20'
.\mvnw.cmd spring-boot:run
```

Wait until you see: `Started QuizApplication in X seconds`

#### Step 3: Register Fresh Admin Account
1. Go to `http://localhost:4200/register`
2. Username: `admin` (or any new username)
3. Password: `password123` (or your choice)
4. **IMPORTANT**: Select **"Admin"** from the dropdown
5. Click Register

#### Step 4: Login
1. Go to `http://localhost:4200/login`
2. Enter your credentials
3. Click Login
4. You should be redirected to `/admin` dashboard

### Alternative: Manual Database Cleanup

If you can't drop the collection, manually delete duplicates:

```javascript
// In mongosh
use quizapp

// Find all users
db.users.find()

// Delete duplicates (keep only one admin)
db.users.deleteMany({ username: "admin" })

// Now register again
```

### Verification Commands

Check if backend is running:
```powershell
curl http://127.0.0.1:9091/api/student/quizzes
```

Check MongoDB connection:
```powershell
mongosh --eval "db.adminCommand('ping')"
```

### Common Issues

**Issue**: "Invalid username or password"
- **Solution**: The user doesn't exist. Register first.

**Issue**: Still stuck on "Logging in..."
- **Solution**: Backend crashed. Check terminal for errors. Restart backend.

**Issue**: "CORS error" in browser console
- **Solution**: Backend not running or wrong port. Verify backend is on port 9091.

### Configuration Summary

- **Backend Port**: 9091
- **Frontend Port**: 4200
- **MongoDB**: localhost:27017/quizapp
- **API Base URL**: http://127.0.0.1:9091/api

### Files Modified to Fix Issue

1. `UserRepository.java` - Changed return type to `List<User>`
2. `UserService.java` - Added duplicate cleanup logic
3. `User.java` - Added `@Indexed(unique = true)` to username
4. `Result.java` - Added indexes for performance
5. `PdfService.java` - Optimized regex compilation
6. `CorsConfig.java` - Fixed CORS origins
7. `environment.ts` - Updated API URL to 127.0.0.1:9091

### Next Steps After Login Works

Once you can login successfully:
1. Upload a quiz PDF as admin
2. Logout and register as a student
3. Take the quiz
4. Login as admin again to see results

### Need Help?

If the issue persists:
1. Take a screenshot of the browser console (F12 → Console tab)
2. Copy the backend terminal output
3. Check if MongoDB is running: `mongosh`
