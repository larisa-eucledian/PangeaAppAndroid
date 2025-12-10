# Firebase Setup Instructions

## ⚠️ IMPORTANT: Replace Placeholder Config

The current `app/google-services.json` is a **PLACEHOLDER** and will not work in production.

You MUST replace it with your real Firebase configuration file.

## Steps to Setup Firebase:

### 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or select existing project
3. Enter project name (e.g., "Pangea App")
4. Follow the setup wizard

### 2. Add Android App to Firebase
1. In Firebase Console, click "Add app" → Android
2. Enter package name: `com.example.pangeaapp`
3. Enter app nickname: "Pangea App Android"
4. (Optional) Enter SHA-1 certificate fingerprint for debugging
5. Click "Register app"

### 3. Download google-services.json
1. Firebase will generate your `google-services.json` file
2. Download it
3. **Replace** the placeholder file at `app/google-services.json` with your downloaded file

### 4. Enable Services

#### Analytics (Already Configured)
- Analytics is automatically enabled
- No additional setup required
- Events will start logging automatically

#### Cloud Messaging / Push Notifications (Configured but not implemented)
1. In Firebase Console → Project Settings → Cloud Messaging
2. Note your Server Key (for backend)
3. Implementation is pending - dependencies are ready

### 5. Verify Setup
1. Build the app
2. Run on a device or emulator
3. Check Firebase Console → Analytics → Dashboard
4. You should see your app session within 24 hours

## Current Firebase Integration:

### ✅ Implemented:
- Firebase BOM (Bill of Materials) for version management
- Firebase Analytics SDK
- Firebase Messaging SDK (for future push notifications)
- Google Services plugin
- PangeaApp application class with Firebase init

### ⏳ Pending Implementation:
- Analytics event tracking (user actions, purchases, etc.)
- Push notification handling
- FCM token management
- Backend integration for push notifications

## Firebase Analytics Events (To Implement):

Recommended events to track:
- `user_login` - When user logs in
- `user_register` - When user creates account
- `package_view` - When user views a package
- `package_purchase_start` - When user starts checkout
- `package_purchase_complete` - When purchase is completed
- `esim_activate` - When user activates eSIM
- `esim_view` - When user views eSIM details

## Security Notes:

- ✅ `google-services.json` contains public API keys (safe to commit for Android)
- ❌ Never commit backend service account keys
- ✅ Use Firebase App Check for additional security (future)
- ✅ Configure Firebase Security Rules for any Firebase services you use

## Troubleshooting:

### Build fails with "File google-services.json is missing"
- Make sure `google-services.json` exists in `app/` directory
- Check that the plugin is applied: `id("com.google.gms.google-services")`

### Analytics not showing data
- Wait up to 24 hours for first data
- Check that app is running in non-debug mode for some events
- Verify `google-services.json` is the correct file from Firebase Console

### App crashes on startup
- Check that package name in `google-services.json` matches `build.gradle.kts`
- Verify all Firebase dependencies are properly synced

## Next Steps After Replacing google-services.json:

1. Sync Gradle
2. Clean and rebuild project
3. Run app and verify Firebase init in logs: `FirebaseApp initialization successful`
4. Implement analytics events (see IMPLEMENTATION_PLAN.md)
5. Test push notifications setup (backend integration required)
