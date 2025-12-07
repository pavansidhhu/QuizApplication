# 🚀 Step-by-Step Render.com Deployment Guide

Follow these exact steps to deploy your Quiz Application to the public internet.

---

## 📋 PART 1: Set Up MongoDB Atlas (Cloud Database)

### Step 1: Create MongoDB Atlas Account
1. Open your browser and go to: **https://www.mongodb.com/cloud/atlas/register**
2. Click **"Sign Up"**
3. Fill in your details:
   - Email address
   - Password
   - First name / Last name
4. Click **"Create your Atlas account"**
5. Verify your email address (check your inbox)

### Step 2: Create a Free Database Cluster
1. After logging in, you'll see **"Welcome to Atlas"**
2. Click **"Create"** or **"Build a Database"**
3. Choose deployment option:
   - Select **"M0 FREE"** (should be highlighted in green)
   - Click **"Create"**
4. Configure your cluster:
   - **Cloud Provider:** Select **AWS** (recommended)
   - **Region:** Choose the one closest to you (e.g., Mumbai, Singapore, etc.)
   - **Cluster Name:** Leave as default or name it `QuizAppCluster`
5. Click **"Create Cluster"** at the bottom
6. Wait 1-3 minutes for cluster creation

### Step 3: Create Database User
1. You'll see a **"Security Quickstart"** screen
2. Under **"How would you like to authenticate your connection?"**
   - Select **"Username and Password"**
3. Create credentials:
   - **Username:** `quizappuser`
   - **Password:** Click **"Autogenerate Secure Password"** 
   - **IMPORTANT:** Copy this password and save it somewhere safe! You'll need it later.
4. Click **"Create User"**

### Step 4: Set Up Network Access
1. Still on the Security Quickstart screen
2. Under **"Where would you like to connect from?"**
   - Click **"Add My Current IP Address"** (this adds your IP)
   - Then click **"Add a Different IP Address"**
3. In the popup:
   - **IP Address:** Enter `0.0.0.0/0`
   - **Description:** Enter `Allow all IPs`
   - Click **"Add Entry"**
4. Click **"Finish and Close"**
5. Click **"Go to Database"**

### Step 5: Get Your Connection String
1. On the Database Deployments page, find your cluster
2. Click the **"Connect"** button
3. Choose **"Connect your application"**
4. You'll see a connection string like this:
   ```
   mongodb+srv://quizappuser:<password>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority
   ```
5. **Copy this string**
6. **Replace `<password>`** with the actual password you saved in Step 3
7. **Add `/quizapp`** before the `?` to specify the database name:
   ```
   mongodb+srv://quizappuser:YOUR_ACTUAL_PASSWORD@cluster0.xxxxx.mongodb.net/quizapp?retryWrites=true&w=majority
   ```
8. **Save this complete connection string** - you'll need it soon!

✅ **MongoDB Atlas Setup Complete!**

---

## 📋 PART 2: Deploy Backend to Render

### Step 6: Create Render Account
1. Go to: **https://render.com**
2. Click **"Get Started for Free"** or **"Sign Up"**
3. Choose **"Sign up with GitHub"**
4. Log in to your GitHub account if prompted
5. Click **"Authorize Render"** to connect your GitHub account

### Step 7: Create Backend Web Service
1. On the Render dashboard, click **"New +"** (top right)
2. Select **"Web Service"**
3. You'll see your GitHub repositories
4. Find **"QuizApplication"** repository
5. Click **"Connect"** next to it

### Step 8: Configure Backend Service
Fill in these settings **EXACTLY**:

**1. Name:**
```
quiz-app-backend
```

**2. Region:**
- Choose the region closest to you (e.g., Singapore, Frankfurt, Ohio)

**3. Branch:**
```
main
```

**4. Root Directory:**
```
quiz-app/backend
```

**5. Runtime:**
- Select **"Java"** from the dropdown

**6. Build Command:**
```
mvn clean install
```

**7. Start Command:**
```
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

**8. Instance Type:**
- Select **"Free"**

### Step 9: Add Environment Variables
1. Scroll down to **"Environment Variables"** section
2. Click **"Add Environment Variable"**
3. Add the first variable:
   - **Key:** `MONGODB_URI`
   - **Value:** Paste your complete MongoDB connection string from Step 5
4. Click **"Add Environment Variable"** again
5. Add the second variable:
   - **Key:** `PORT`
   - **Value:** `10000`

### Step 10: Deploy Backend
1. Scroll to the bottom
2. Click **"Create Web Service"**
3. **Wait for deployment** (this takes 5-10 minutes)
4. You'll see logs scrolling - this is normal
5. Wait until you see **"Your service is live 🎉"**
6. At the top, you'll see your backend URL like:
   ```
   https://quiz-app-backend-xxxx.onrender.com
   ```
7. **Copy this URL and save it!**

✅ **Backend Deployed!**

---

## 📋 PART 3: Update Frontend Configuration

### Step 11: Update Frontend Environment File
1. Open your code editor
2. Navigate to: `quiz-app/frontend/src/environments/environment.prod.ts`
3. Find this line:
   ```typescript
   apiUrl: 'https://YOUR_BACKEND_URL.onrender.com/api'
   ```
4. Replace `YOUR_BACKEND_URL.onrender.com` with your actual backend URL from Step 10
5. It should look like:
   ```typescript
   apiUrl: 'https://quiz-app-backend-xxxx.onrender.com/api'
   ```
6. **Save the file**

### Step 12: Update CORS Configuration
1. Navigate to: `quiz-app/backend/src/main/java/com/quizapp/backend/config/CorsConfig.java`
2. Find the `.allowedOrigins(` section (around line 17)
3. Add a new line with your frontend URL (you'll get this after deploying frontend):
   ```java
   .allowedOrigins(
           "http://localhost:4200",
           "http://127.0.0.1:4200",
           "http://localhost:62323",
           "http://127.0.0.1:62323",
           "https://seven-areas-cut.loca.lt",
           "https://quiz-app-frontend.onrender.com")  // Add this line
   ```
4. **Note:** We'll update this with the exact URL after deploying the frontend
5. **Save the file**

### Step 13: Commit and Push Changes
1. Open PowerShell/Terminal in your project root
2. Run these commands:
   ```powershell
   git add .
   git commit -m "Configure for production deployment"
   git push
   ```
3. **Wait for the push to complete**
4. Render will **automatically redeploy** your backend (takes 2-3 minutes)

✅ **Frontend Configuration Updated!**

---

## 📋 PART 4: Deploy Frontend to Render

### Step 14: Create Frontend Static Site
1. Go back to Render dashboard: **https://dashboard.render.com**
2. Click **"New +"** (top right)
3. Select **"Static Site"**
4. Find your **"QuizApplication"** repository
5. Click **"Connect"**

### Step 15: Configure Frontend Service
Fill in these settings **EXACTLY**:

**1. Name:**
```
quiz-app-frontend
```

**2. Branch:**
```
main
```

**3. Root Directory:**
```
quiz-app/frontend
```

**4. Build Command:**
```
npm install && npm run build
```

**5. Publish Directory:**
```
dist/frontend/browser
```

### Step 16: Deploy Frontend
1. Scroll to the bottom
2. Click **"Create Static Site"**
3. **Wait for deployment** (this takes 5-10 minutes)
4. You'll see build logs - this is normal
5. Wait until you see **"Your site is live 🎉"**
6. At the top, you'll see your frontend URL like:
   ```
   https://quiz-app-frontend.onrender.com
   ```
7. **Copy this URL!**

✅ **Frontend Deployed!**

---

## 📋 PART 5: Final CORS Update

### Step 17: Update CORS with Actual Frontend URL
1. Go back to your code editor
2. Open: `quiz-app/backend/src/main/java/com/quizapp/backend/config/CorsConfig.java`
3. Update the `.allowedOrigins(` section with your actual frontend URL from Step 16:
   ```java
   .allowedOrigins(
           "http://localhost:4200",
           "http://127.0.0.1:4200",
           "https://quiz-app-frontend.onrender.com")  // Your actual URL
   ```
4. **Save the file**

### Step 18: Push Final Changes
1. In PowerShell/Terminal:
   ```powershell
   git add .
   git commit -m "Update CORS with production frontend URL"
   git push
   ```
2. Wait for Render to auto-redeploy backend (2-3 minutes)

✅ **CORS Updated!**

---

## 📋 PART 6: Test Your Live Application

### Step 19: Test Your Application
1. Open your browser
2. Go to your frontend URL: `https://quiz-app-frontend.onrender.com`
3. **Wait 30-60 seconds** on first load (free tier wakes up from sleep)
4. You should see the login page
5. Click **"Register here"**
6. Create a test account:
   - Username: `testuser`
   - Password: `test123`
   - Role: Student
7. Click **"Register"**
8. If successful, you'll be redirected to login
9. Log in with your credentials

### Step 20: Share Your App! 🎉
Your Quiz Application is now **LIVE** and **PUBLIC**!

**Share these URLs:**
- **Frontend (Main App):** `https://quiz-app-frontend.onrender.com`
- **Backend API:** `https://quiz-app-backend-xxxx.onrender.com/api`

---

## 🎯 Summary of What You Created

✅ **MongoDB Atlas** - Free cloud database
✅ **Backend on Render** - Spring Boot API
✅ **Frontend on Render** - Angular application
✅ **Fully functional** - Anyone can access your quiz app!

---

## 💡 Important Notes

### Free Tier Behavior:
- **Services sleep after 15 minutes** of inactivity
- **First request** after sleep takes 30-60 seconds to wake up
- This is normal for free tier!

### Keeping Your App Awake (Optional):
1. Go to: **https://uptimerobot.com**
2. Create a free account
3. Add a monitor for your frontend URL
4. Set it to ping every 5 minutes
5. Your app stays awake!

### Automatic Deployments:
- Every time you push to GitHub, Render **automatically redeploys**
- No need to manually deploy again
- Just `git push` and wait 2-3 minutes

---

## 🆘 Troubleshooting

### Problem: Backend won't start
**Solution:**
- Check Render logs (click on your backend service → "Logs")
- Verify MONGODB_URI environment variable is set correctly
- Make sure MongoDB Atlas IP whitelist includes 0.0.0.0/0

### Problem: Frontend shows "Failed to fetch"
**Solution:**
- Check that backend is running (visit backend URL in browser)
- Verify `environment.prod.ts` has correct backend URL
- Check CORS configuration includes frontend URL

### Problem: "Cannot connect to database"
**Solution:**
- Verify MongoDB connection string is correct
- Check MongoDB Atlas network access allows 0.0.0.0/0
- Ensure database user credentials are correct

### Problem: Page loads but shows errors
**Solution:**
- Open browser console (F12)
- Check for CORS errors
- Verify backend URL in environment.prod.ts ends with `/api`

---

## 🔄 How to Update Your App

1. Make changes to your code locally
2. Test locally first
3. Commit and push:
   ```powershell
   git add .
   git commit -m "Description of changes"
   git push
   ```
4. Render automatically redeploys (2-3 minutes)
5. Refresh your live site to see changes!

---

## 🎉 Congratulations!

You've successfully deployed a full-stack application to the cloud!

**What you've accomplished:**
- ✅ Set up cloud database (MongoDB Atlas)
- ✅ Deployed Java Spring Boot backend
- ✅ Deployed Angular frontend
- ✅ Configured CORS and environment variables
- ✅ Made your app publicly accessible
- ✅ All for **FREE**!

**Your app is now live at:**
`https://quiz-app-frontend.onrender.com`

Share it with friends, add it to your portfolio, or use it for your projects!

---

Need help? Check the logs in Render dashboard or review the troubleshooting section above.
