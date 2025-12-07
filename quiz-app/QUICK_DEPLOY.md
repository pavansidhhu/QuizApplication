# 🚀 Quick Deployment Reference

## 📝 Render.com Configuration

### Backend (Web Service)
```
Name: quiz-app-backend
Root Directory: quiz-app/backend
Runtime: Java
Build Command: mvn clean install
Start Command: java -jar target/backend-0.0.1-SNAPSHOT.jar
Instance Type: Free

Environment Variables:
- MONGODB_URI = <your-mongodb-atlas-connection-string>
```

### Frontend (Static Site)
```
Name: quiz-app-frontend
Root Directory: quiz-app/frontend
Build Command: npm install && npm run build
Publish Directory: dist/frontend/browser
```

## 🔗 Important URLs

After deployment, you'll have:
- **Frontend:** https://quiz-app-frontend.onrender.com
- **Backend:** https://quiz-app-backend.onrender.com
- **API Endpoint:** https://quiz-app-backend.onrender.com/api

## ⚙️ MongoDB Atlas Connection String Format

```
mongodb+srv://USERNAME:PASSWORD@cluster0.xxxxx.mongodb.net/quizapp?retryWrites=true&w=majority
```

Replace:
- `USERNAME` - Your MongoDB Atlas username
- `PASSWORD` - Your MongoDB Atlas password
- `cluster0.xxxxx` - Your actual cluster address

## 🔄 Deployment Workflow

1. **Set up MongoDB Atlas** (5 minutes)
   - Create free cluster
   - Create database user
   - Whitelist all IPs (0.0.0.0/0)
   - Get connection string

2. **Deploy Backend** (10 minutes)
   - Create Web Service on Render
   - Configure as shown above
   - Add MONGODB_URI environment variable
   - Wait for deployment

3. **Update Frontend Config** (2 minutes)
   - Update `frontend/src/environments/environment.prod.ts`
   - Replace `YOUR_BACKEND_URL` with actual backend URL
   - Commit and push

4. **Deploy Frontend** (10 minutes)
   - Create Static Site on Render
   - Configure as shown above
   - Wait for deployment

5. **Update CORS** (2 minutes)
   - Update `backend/config/CorsConfig.java`
   - Add frontend URL to allowed origins
   - Commit and push (auto-redeploys)

6. **Test!** 🎉
   - Visit your frontend URL
   - Register and login
   - Take a quiz!

## 💡 Pro Tips

- **Free tier sleeps after 15 min** - First request takes 30-60 seconds
- **Use UptimeRobot** to keep your app awake
- **Auto-deploy enabled** - Push to GitHub = automatic redeploy
- **Check logs** in Render dashboard if something breaks

## 🆘 Common Issues

| Issue | Solution |
|-------|----------|
| Backend won't start | Check MONGODB_URI is set correctly |
| Frontend can't connect | Verify backend URL in environment.prod.ts |
| CORS errors | Add frontend URL to CorsConfig.java |
| Database connection fails | Check MongoDB Atlas IP whitelist |

## 📚 Full Guide

See `DEPLOYMENT_GUIDE.md` for detailed step-by-step instructions.
