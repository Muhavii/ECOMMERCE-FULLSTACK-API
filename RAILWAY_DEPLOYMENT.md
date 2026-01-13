# Railway Deployment Guide

## Prerequisites
- Railway account (sign up at https://railway.app)
- GitHub repository with your code pushed
- PostgreSQL addon installed on Railway

## Step-by-Step Deployment

### 1. Create New Project on Railway

1. Go to https://railway.app/dashboard
2. Click "New Project"
3. Select "Deploy from GitHub repo"
4. Choose your `ecommerce-fullstack-api` repository
5. Railway will automatically detect it's a Java/Maven project

### 2. Add PostgreSQL Database

1. In your Railway project, click "New"
2. Select "Database" → "PostgreSQL"
3. Railway will automatically:
   - Create a PostgreSQL instance
   - Set the `DATABASE_URL` environment variable
   - Connect it to your application

### 3. Configure Environment Variables

Go to your service → Variables tab and add:

```env
# JWT Secret (REQUIRED - Generate a secure key)
JWT_SECRET=your-super-secret-256-bit-key-change-this-in-production-use-random-string

# Spring Profile (automatically set)
SPRING_PROFILES_ACTIVE=prod

# Database URL (automatically set by Railway PostgreSQL addon)
DATABASE_URL=postgresql://...

# Server Port (automatically set by Railway)
PORT=8080
```

**Generate JWT_SECRET:**
```bash
# Use this command to generate a secure random key:
openssl rand -base64 32
```

### 4. Deploy Settings (Auto-Configured)

Railway automatically detects:
- **Build Command:** `mvn clean package -DskipTests`
- **Start Command:** `java -jar target/ecommerce-api-1.0.0.jar --spring.profiles.active=prod`
- **Port:** Uses `$PORT` environment variable

### 5. Create Admin User

After deployment, create your admin user:

**Option A: Via Railway PostgreSQL Console**
```sql
-- Generate password hash first (use local endpoint or API)
-- Then insert admin user
INSERT INTO users (username, email, password, role, active, created_at, updated_at)
VALUES (
  'admin',
  'admin@example.com',
  '$2a$10$YOUR_BCRYPT_HASH_HERE',
  'ADMIN',
  true,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);
```

**Option B: Via Application API**
1. Use the deployed app to register a user
2. Access Railway PostgreSQL console
3. Run: `UPDATE users SET role = 'ADMIN' WHERE username = 'your-username';`

### 6. Test Your Deployment

Your API will be available at: `https://your-app-name.up.railway.app`

**Test Endpoints:**
```bash
# Health Check
curl https://your-app-name.up.railway.app/api/products

# Register User
curl -X POST https://your-app-name.up.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@test.com","password":"test123"}'

# Login
curl -X POST https://your-app-name.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

## Frontend Deployment

### Deploy Frontend to Railway (Optional)

1. Create a new service in your Railway project
2. Point it to the `frontend` directory
3. Railway will auto-detect Vite/npm
4. Set environment variable:
   ```env
   VITE_API_URL=https://your-backend-app.up.railway.app
   ```

### Or Deploy Frontend to Vercel/Netlify

**For Vercel:**
```bash
cd frontend
vercel --prod
```

**Environment Variable:**
```env
VITE_API_URL=https://your-backend-app.up.railway.app
```

## Database Management

### Access Railway PostgreSQL

1. Go to PostgreSQL service in Railway
2. Click "Data" tab to view tables
3. Or use connection string to connect via `psql`:

```bash
# Get connection string from Railway PostgreSQL variables
psql postgresql://username:password@host:port/database
```

### Database Migrations

The app uses `spring.jpa.hibernate.ddl-auto=update` which:
- ✅ Auto-creates tables on first run
- ✅ Updates schema on entity changes
- ⚠️ Doesn't drop tables (safe for production)

**For production, consider:**
- Using Flyway or Liquibase for migrations
- Setting `ddl-auto=validate` after initial setup

## Monitoring & Logs

### View Logs
1. Go to your service in Railway
2. Click "Deployments" tab
3. Select active deployment
4. View real-time logs

### Monitor Health
```bash
# Check application logs for:
- "Started EcommerceApiApplication"
- Database connection status
- Any error stack traces
```

## Troubleshooting

### Common Issues

**1. Build Fails**
```bash
# Check Java version in pom.xml matches Railway
# Current: Java 17
```

**2. Database Connection Error**
```bash
# Verify DATABASE_URL is set correctly
# Check PostgreSQL addon is linked to service
```

**3. Application Won't Start**
```bash
# Check logs for port binding issues
# Ensure PORT environment variable is used
```

**4. CORS Errors**
```bash
# Update frontend URL in SecurityConfig if needed
# Current: localhost:5173, localhost:3000
# Add your production frontend URL
```

## Security Checklist

Before going live:
- ✅ Change `JWT_SECRET` to a strong random value
- ✅ Set `spring.jpa.show-sql=false` (already set)
- ✅ Disable H2 console (already set)
- ✅ Use strong admin password with BCrypt
- ✅ Enable HTTPS (Railway provides this automatically)
- ✅ Review CORS allowed origins for production frontend
- ✅ Set up database backups in Railway

## Cost Optimization

Railway pricing:
- **Free Tier:** $5 credit/month (good for testing)
- **Hobby Plan:** $5/month per service
- **Pro Plan:** Pay-as-you-go

**Recommendations:**
- Start with Hobby plan for production
- Monitor usage in Railway dashboard
- Set up spending limits

## Rollback Strategy

If deployment fails:
1. Go to "Deployments" tab
2. Find last working deployment
3. Click "Redeploy"

## Support

- Railway Docs: https://docs.railway.app
- Railway Discord: https://discord.gg/railway
- Project Issues: GitHub Issues tab

---

**Your app is now production-ready!** 🚀
