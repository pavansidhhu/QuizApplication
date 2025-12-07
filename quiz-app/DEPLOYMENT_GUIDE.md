# 🚀 Quiz Application - Public Hosting Guide

This guide will help you deploy your Quiz Application to the public internet for **FREE** using Render.com.

## 📋 Prerequisites

1. ✅ GitHub account (you already have this)
2. ✅ Code pushed to GitHub (done!)
3. 🔲 Render.com account (free)
4. 🔲 MongoDB Atlas account (free)

---

## 🗄️ Step 1: Set Up MongoDB Atlas (Free Cloud Database)

### 1.1 Create MongoDB Atlas Account
1. Go to [https://www.mongodb.com/cloud/atlas/register](https://www.mongodb.com/cloud/atlas/register)
2. Sign up for a free account
3. Choose **M0 (Free)** tier

### 1.2 Create a Cluster
1. Click **"Build a Database"**
2. Choose **M0 FREE** tier
3. Select a cloud provider (AWS recommended)
4. Choose a region closest to you
5. Click **"Create Cluster"**

### 1.3 Create Database User
1. Click **"Database Access"** in the left sidebar
2. Click **"Add New Database User"**
3. Choose **"Password"** authentication
4. Username: `quizapp`
5. Password: Generate a strong password (save this!)
6. Database User Privileges: **"Read and write to any database"**
7. Click **"Add User"**

### 1.4 Whitelist IP Addresses
1. Click **"Network Access"** in the left sidebar
2. Click **"Add IP Address"**
3. Click **"Allow Access from Anywhere"** (0.0.0.0/0)
4. Click **"Confirm"**

### 1.5 Get Connection String
1. Click **"Database"** in the left sidebar
2. Click **"Connect"** on your cluster
3. Choose **"Connect your application"**
4. Copy the connection string (looks like this):
   ```
   mongodb+srv://quizapp:<password>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority
   ```
5. Replace `<password>` with your actual password
6. Add `/quizapp` before the `?` to specify the database name:
   ```
   mongodb+srv://quizapp:YOUR_PASSWORD@cluster0.xxxxx.mongodb.net/quizapp?retryWrites=true&w=majority
   ```

**Save this connection string - you'll need it!**

---

## 🖥️ Step 2: Deploy Backend to Render.com

### 2.1 Create Render Account
1. Go to [https://render.com](https://render.com)
2. Sign up using your **GitHub account**
3. Authorize Render to access your repositories

### 2.2 Create New Web Service
1. Click **"New +"** → **"Web Service"**
2. Connect your GitHub repository: `QuizApplication`
3. Configure the service:

   **Basic Settings:**
   - **Name:** `quiz-app-backend`
   - **Region:** Choose closest to you
   - **Branch:** `main`
   - **Root Directory:** `quiz-app/backend`
   - **Runtime:** `Java`
   - **Build Command:** `mvn clean install`
   - **Start Command:** `java -jar target/backend-0.0.1-SNAPSHOT.jar`

   **Instance Type:**
   - Select **Free** tier

### 2.3 Add Environment Variables
Click **"Advanced"** and add these environment variables:

| Key | Value |
|-----|-------|
| `MONGODB_URI` | Your MongoDB Atlas connection string from Step 1.5 |
| `PORT` | `9092` |

### 2.4 Deploy
1. Click **"Create Web Service"**
2. Wait for deployment (5-10 minutes)
3. Once deployed, you'll get a URL like: `https://quiz-app-backend.onrender.com`

**Save this backend URL!**

---

## 🎨 Step 3: Deploy Frontend to Render.com

### 3.1 Update Frontend Environment
Before deploying, we need to update the frontend to use your production backend URL.

The frontend environment file will be updated to use the backend URL from Step 2.4.

### 3.2 Create Static Site
1. In Render dashboard, click **"New +"** → **"Static Site"**
2. Select your GitHub repository: `QuizApplication`
3. Configure the site:

   **Basic Settings:**
   - **Name:** `quiz-app-frontend`
   - **Branch:** `main`
   - **Root Directory:** `quiz-app/frontend`
   - **Build Command:** `npm install && npm run build`
   - **Publish Directory:** `dist/frontend/browser`

### 3.3 Deploy
1. Click **"Create Static Site"**
2. Wait for deployment (5-10 minutes)
3. Once deployed, you'll get a URL like: `https://quiz-app-frontend.onrender.com`

---

## 🔧 Step 4: Update CORS Configuration

After getting your frontend URL, you need to update the backend CORS configuration:

1. Update `backend/src/main/java/com/quizapp/backend/config/CorsConfig.java`
2. Add your frontend URL to the allowed origins
3. Commit and push changes
4. Render will automatically redeploy

---

## ✅ Step 5: Test Your Application

1. Visit your frontend URL: `https://quiz-app-frontend.onrender.com`
2. Try registering a new user
3. Login with your credentials
4. Test the quiz functionality

---

## 🎉 You're Live!

Your Quiz Application is now publicly accessible!

**Share these URLs:**
- Frontend: `https://quiz-app-frontend.onrender.com`
- Backend API: `https://quiz-app-backend.onrender.com/api`

---

## 📝 Important Notes

### Free Tier Limitations:
- **Render Free Tier:** Services spin down after 15 minutes of inactivity
- **First request after inactivity:** May take 30-60 seconds to wake up
- **MongoDB Atlas Free Tier:** 512 MB storage

### Keeping Your App Awake:
You can use services like [UptimeRobot](https://uptimerobot.com/) to ping your app every 5 minutes to keep it awake.

---

## 🆘 Troubleshooting

### Backend won't start:
- Check environment variables are set correctly
- Verify MongoDB connection string is correct
- Check Render logs for errors

### Frontend can't connect to backend:
- Verify backend URL in frontend environment
- Check CORS configuration includes frontend URL
- Ensure backend is running (check Render dashboard)

### Database connection issues:
- Verify MongoDB Atlas IP whitelist includes 0.0.0.0/0
- Check database user credentials
- Ensure connection string includes database name

---

## 🔄 Updating Your App

To update your deployed application:

1. Make changes locally
2. Commit and push to GitHub:
   ```bash
   git add .
   git commit -m "Update description"
   git push
   ```
3. Render will automatically detect changes and redeploy!

---

## 💰 Cost

**Everything is FREE!** 🎉
- Render Free Tier: $0/month
- MongoDB Atlas M0: $0/month
- GitHub: $0/month

---

Need help? Check the Render documentation or MongoDB Atlas documentation for more details.
