# Quick Test - Login Fix

## I just cleaned your database - all duplicate users are gone!

### NOW DO THIS:

1. **Make sure backend is running** (check your terminal - should say "Started QuizApplication")

2. **Open browser**: http://localhost:4200/register

3. **Register NEW admin**:
   - Username: `admin`
   - Password: `admin123`
   - Confirm Password: `admin123`
   - **Role: Select "Admin"** ← VERY IMPORTANT!
   - Click "Register"

4. **Login**:
   - Username: `admin`
   - Password: `admin123`
   - Click "Login"

5. **Expected**: You should be redirected to http://localhost:4200/admin

---

## If it STILL doesn't work:

### Open Browser Console (F12) and check for errors:

**Red error about CORS?**
- Backend not running or wrong port

**401 Unauthorized?**
- Wrong password or user doesn't exist

**500 Internal Server Error?**
- Backend crashed - check backend terminal

**Network request pending forever?**
- Backend not responding - restart it

---

## Backend Status Check

Run this in PowerShell:
```powershell
curl http://127.0.0.1:9091/api/student/quizzes
```

Should return: `[]` (empty array) or list of quizzes

If it fails: Backend is not running on port 9091

---

## Emergency Reset

If nothing works, do this:

```powershell
# 1. Stop backend (Ctrl+C in backend terminal)

# 2. Clear database
mongosh --eval "use quizapp; db.users.deleteMany({})"

# 3. Restart backend
cd c:\Users\Pavan Sidhhu\.gemini\antigravity\playground\ionic-pioneer\quiz-app\backend
$env:JAVA_HOME='C:\Program Files\Java\jdk-20'
.\mvnw.cmd spring-boot:run

# 4. Wait for "Started QuizApplication"

# 5. Try register + login again
```
