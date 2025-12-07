# Tunnel Hosting Setup - Quick Guide

## ⚠️ IMPORTANT: You tunneled the WRONG port!

You ran: `lt --port 9092`
But your backend runs on: **Port 9091**

## ✅ Here's What to Do:

### Step 1: Stop the Wrong Tunnel
Press `Ctrl+C` in the terminal where you ran `lt --port 9092`

### Step 2: Start the CORRECT Backend Tunnel
```powershell
lt --port 9091
```

You'll get a NEW URL (different from smart-dragons-happen.loca.lt)
**Copy that URL!**

### Step 3: Update Frontend Config Again
I already updated `environment.ts` with the old URL. You need to change it to the NEW URL you just got.

Edit: `quiz-app/frontend/src/environments/environment.ts`
```typescript
apiUrl: 'https://YOUR-NEW-URL.loca.lt/api'  // ← Paste your new URL here
```

### Step 4: Restart Frontend
In the frontend terminal:
```powershell
# Press Ctrl+C to stop
npm start  # Start again
```

### Step 5: Restart Backend
In the backend terminal:
```powershell
# Press Ctrl+C to stop
$env:JAVA_HOME='C:\Program Files\Java\jdk-20'
.\mvnw.cmd spring-boot:run  # Start again
```

### Step 6: Tunnel the Frontend
Open a NEW terminal:
```powershell
cd c:\Users\Pavan Sidhhu\.gemini\antigravity\playground\ionic-pioneer
lt --port 4200
```

You'll get another URL like: `https://abc-xyz.loca.lt`

### Step 7: Share the Frontend URL
Give people the **frontend tunnel URL** (from Step 6), NOT the backend URL!

---

## Quick Checklist

- [ ] Backend tunnel on port **9091** (not 9092!)
- [ ] Frontend `environment.ts` updated with backend tunnel URL
- [ ] Frontend restarted
- [ ] Backend restarted (to apply CORS changes)
- [ ] Frontend tunnel on port **4200**
- [ ] Share the **frontend** tunnel URL with others

---

## Testing

1. Open the frontend tunnel URL in your browser
2. You may see a warning - click "Click to Continue"
3. Try to register and login
4. If it works locally but not via tunnel, check browser console for errors

---

## Common Issues

**"Click to Continue" page keeps appearing**
- This is normal for localtunnel
- Just click it each time

**CORS errors**
- Make sure you restarted the backend after updating CorsConfig.java
- Check that the frontend URL in CORS matches your actual tunnel URL

**API calls fail**
- Make sure backend tunnel is on port 9091, not 9092
- Check that environment.ts has the correct backend tunnel URL
