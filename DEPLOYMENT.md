# Deployment

This project is split into:

- `Server`: Spring Boot API, deploy to Render.
- `Client`: Vite React app, deploy to Vercel.

## Render

Use the root `render.yaml` blueprint, or create a Render Web Service manually:

- Root directory: `Server`
- Environment: `Java`
- Build command: `chmod +x mvnw && ./mvnw clean package -DskipTests`
- Start command: `java -jar target/salon-0.0.1-SNAPSHOT.jar`
- Health check path: `/actuator/health`

Set these environment variables in Render:

- `SPRING_PROFILES_ACTIVE=prod`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `FRONTEND_URL=https://your-vercel-app.vercel.app`
- `ALLOWED_ORIGINS=https://your-vercel-app.vercel.app,https://*.vercel.app`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `APP_ADMIN_EMAIL`
- `RAZORPAY_KEY_ID`
- `RAZORPAY_KEY_SECRET`
- `RAZORPAY_WEBHOOK_SECRET`

Render provides `PORT` automatically. The app reads it through `server.port`.

## Vercel

Import the repo in Vercel and set:

- Root directory: `Client`
- Framework preset: `Vite`
- Build command: `npm run build`
- Output directory: `dist`

Set this environment variable in Vercel:

- `VITE_API_BASE_URL=https://your-render-service.onrender.com`

## OAuth

In Google Cloud Console, add this authorized redirect URI:

- `https://your-render-service.onrender.com/login/oauth2/code/google`

Also make sure Render has:

- `FRONTEND_URL=https://your-vercel-app.vercel.app`

## Database

The backend is configured for MySQL. Use a hosted MySQL provider and set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in Render.
