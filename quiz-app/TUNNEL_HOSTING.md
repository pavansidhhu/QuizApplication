# Quiz App - Tunnel Hosting Guide

## Problem
You're running `lt --port 8000` but your app runs on:
- Frontend: http://localhost:4200
- Backend: http://localhost:9091

## Solution: Host Both Frontend and Backend

### Step 1: Install localtunnel (if not already installed)
```powershell
npm install -g localtunnel
```

### Step 2: Start Your Applications
Make sure both are running:
```powershell
# Terminal 1 - Backend
cd c:\Users\Pavan Sidhhu\.gemini\antigravity\playground\ionic-pioneer\quiz-app\backend
$env:JAVA_HOME='C:\Program Files\Java\jdk-20'
.\mvnw.cmd spring-boot:run

# Terminal 2 - Frontend
cd c:\Users\Pavan Sidhhu\.gemini\antigravity\playground\ionic-pioneer
npm start
```

### Step 3: Create Tunnels

#### Option A: Tunnel Frontend Only (Recommended for Testing)
```powershell
# Terminal 3
lt --port 4200 --subdomain my-quiz-app-frontend
```

**Issue**: Frontend will try to call backend at http://127.0.0.1:9091 which won't work from external devices.

#### Option B: Tunnel Both (Full Solution)

**Terminal 3 - Backend Tunnel:**
```powershell
lt --port 9091 --subdomain my-quiz-backend
```
You'll get: `https://my-quiz-backend.loca.lt`

**Terminal 4 - Frontend Tunnel:**
```powershell
lt --port 4200 --subdomain my-quiz-frontend
```
You'll get: `https://my-quiz-frontend.loca.lt`

### Step 4: Update Frontend to Use Backend Tunnel URL

Edit: `quiz-app/frontend/src/environments/environment.ts`

```typescript
export const environment = {
    production: false,
    apiUrl: 'https://my-quiz-backend.loca.lt/api'  // ← Change this
};
```

**Then restart frontend** (Ctrl+C and `npm start` again)

### Step 5: Update CORS in Backend

Edit: `quiz-app/backend/src/main/java/com/quizapp/backend/config/CorsConfig.java`

```java
.allowedOrigins(
    "http://localhost:4200", 
    "http://127.0.0.1:4200",
    "https://my-quiz-frontend.loca.lt"  // ← Add this
)
```

**Then restart backend**

### Step 6: Access Your App

Open: `https://my-quiz-frontend.loca.lt`

You may see a warning page - click "Click to Continue"

---

## Alternative: Use ngrok (More Reliable)

### Install ngrok
Download from: https://ngrok.com/download

### Run ngrok
```powershell
# Terminal 3 - Backend
ngrok http 9091

# Terminal 4 - Frontend  
ngrok http 4200
```

You'll get URLs like:
- Backend: `https://abc123.ngrok.io`
- Frontend: `https://xyz789.ngrok.io`

Update `environment.ts` with the backend ngrok URL.

---

## Troubleshooting

### "Tunnel not working"
- Check if localtunnel is installed: `lt --version`
- Try without subdomain: `lt --port 4200`
- Check if port is actually running: `curl http://localhost:4200`

### "CORS Error"
- Make sure you added the tunnel URL to CORS config
- Restart backend after changing CORS

### "API calls failing"
- Frontend must use the **backend tunnel URL**, not localhost
- Check browser console for the actual API URL being called

### "Can't access from phone/other device"
- Make sure you're using the HTTPS tunnel URL, not localhost
- Localtunnel URLs work from anywhere with internet

---

## Quick Test Commands

**Check if frontend is running:**
```powershell
curl http://localhost:4200
```

**Check if backend is running:**
```powershell
curl http://localhost:9091/api/student/quizzes
```

**Check tunnel:**
```powershell
curl https://your-subdomain.loca.lt
```

---

## Production Deployment (Better than Tunnels)

For permanent hosting, consider:

1. **Frontend**: Deploy to Vercel/Netlify (Free)
2. **Backend**: Deploy to Render/Railway (Free tier)
3. **Database**: MongoDB Atlas (Free tier)

Would you like a guide for permanent deployment instead?
