# ✅ Deployment Checklist

Print this or keep it open while deploying!

---

## 🗄️ MONGODB ATLAS (15 minutes)

- [ ] Go to mongodb.com/cloud/atlas/register
- [ ] Create account and verify email
- [ ] Click "Build a Database"
- [ ] Select M0 FREE tier
- [ ] Choose AWS and nearest region
- [ ] Click "Create Cluster"
- [ ] Create database user: `quizappuser`
- [ ] Save password: ________________
- [ ] Add IP: 0.0.0.0/0 (Allow all)
- [ ] Click "Connect" → "Connect your application"
- [ ] Copy connection string
- [ ] Replace <password> with actual password
- [ ] Add /quizapp before the ?
- [ ] Final string saved: ________________

---

## 🖥️ BACKEND DEPLOYMENT (10 minutes)

- [ ] Go to render.com
- [ ] Sign up with GitHub
- [ ] Click "New +" → "Web Service"
- [ ] Connect QuizApplication repo
- [ ] Name: `quiz-app-backend`
- [ ] Root Directory: `quiz-app/backend`
- [ ] Runtime: Java
- [ ] Build Command: `mvn clean install`
- [ ] Start Command: `java -jar target/backend-0.0.1-SNAPSHOT.jar`
- [ ] Instance Type: Free
- [ ] Add env var: MONGODB_URI = (paste connection string)
- [ ] Add env var: PORT = 10000
- [ ] Click "Create Web Service"
- [ ] Wait for deployment (5-10 min)
- [ ] Backend URL: ________________

---

## 📝 UPDATE FRONTEND CONFIG (5 minutes)

- [ ] Open: `frontend/src/environments/environment.prod.ts`
- [ ] Replace YOUR_BACKEND_URL with actual URL
- [ ] Should end with `/api`
- [ ] Save file
- [ ] Run: `git add .`
- [ ] Run: `git commit -m "Configure for production"`
- [ ] Run: `git push`
- [ ] Wait for backend to redeploy

---

## 🎨 FRONTEND DEPLOYMENT (10 minutes)

- [ ] Go to render.com dashboard
- [ ] Click "New +" → "Static Site"
- [ ] Connect QuizApplication repo
- [ ] Name: `quiz-app-frontend`
- [ ] Root Directory: `quiz-app/frontend`
- [ ] Build Command: `npm install && npm run build`
- [ ] Publish Directory: `dist/frontend/browser`
- [ ] Click "Create Static Site"
- [ ] Wait for deployment (5-10 min)
- [ ] Frontend URL: ________________

---

## 🔧 FINAL CORS UPDATE (5 minutes)

- [ ] Open: `backend/config/CorsConfig.java`
- [ ] Add frontend URL to allowedOrigins
- [ ] Save file
- [ ] Run: `git add .`
- [ ] Run: `git commit -m "Update CORS"`
- [ ] Run: `git push`
- [ ] Wait for backend redeploy (2-3 min)

---

## 🧪 TESTING (5 minutes)

- [ ] Open frontend URL in browser
- [ ] Wait 30-60 seconds (first load)
- [ ] Click "Register here"
- [ ] Create test account
- [ ] Verify registration works
- [ ] Log in
- [ ] Test quiz functionality

---

## 🎉 DONE!

Your app is live at: ________________

Share it with the world! 🌍

---

## 📞 Quick Reference

**MongoDB Atlas:** https://cloud.mongodb.com
**Render Dashboard:** https://dashboard.render.com
**Your GitHub Repo:** https://github.com/pavansidhhu/QuizApplication

**Connection String Format:**
```
mongodb+srv://quizappuser:PASSWORD@cluster0.xxxxx.mongodb.net/quizapp?retryWrites=true&w=majority
```

**Render Backend Config:**
- Root: `quiz-app/backend`
- Build: `mvn clean install`
- Start: `java -jar target/backend-0.0.1-SNAPSHOT.jar`

**Render Frontend Config:**
- Root: `quiz-app/frontend`
- Build: `npm install && npm run build`
- Publish: `dist/frontend/browser`

---

Total Time: ~50 minutes
Cost: $0 (FREE!)
