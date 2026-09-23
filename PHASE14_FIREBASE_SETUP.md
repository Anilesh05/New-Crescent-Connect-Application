# CrescentConnect Phase 14: Firebase Setup Guide

To fully enable the web dashboard, file analysis, and AI analytics features added in Phase 14, you must configure a real Firebase project. This Android application and Web dashboard rely on Firebase as the shared cloud synchronization layer.

## 1. Create Firebase Project
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Click **Create a project**.
3. Name it "CrescentConnect".
4. Disable Google Analytics (optional, or enable if you prefer).

## 2. Enable Authentication
1. Go to **Build > Authentication**.
2. Click **Get Started**.
3. Under **Sign-in providers**, click **Email/Password**.
4. Enable **Email/Password** and click **Save**.

## 3. Enable Firestore
1. Go to **Build > Firestore Database**.
2. Click **Create database**.
3. Choose **Start in production mode** (we will configure rules later).
4. Choose a region close to your users and click **Enable**.

## 4. Enable Storage
1. Go to **Build > Storage**.
2. Click **Get Started**.
3. Start in **Production mode**.
4. Choose a location and click **Done**.

## 5. Configure Web App
1. Go to **Project Settings** (gear icon) > **General**.
2. Scroll to **Your apps** and click the **Web `</>`** icon.
3. Register app with name "CrescentConnect Web".
4. Copy the `firebaseConfig` object and populate your `.env` variables in the `web/` directory.

```env
VITE_FIREBASE_API_KEY=your_api_key
VITE_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=your_project
VITE_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
VITE_FIREBASE_APP_ID=your_app_id
```

## 6. Configure Android App
1. Go to **Project Settings** > **General** > **Your apps**.
2. Click **Add app** > **Android**.
3. Set Android package name to `com.example` (or match your `build.gradle.kts` namespace/applicationId).
4. Click **Register app**.

## 7. Download `google-services.json`
1. Download the `google-services.json` file generated in the previous step.
2. Place this file inside the `app/` directory of your Android codebase.

## 8. Configure Firestore Rules
Go to **Firestore Database > Rules** and apply these rules to enforce role-based access:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == userId;
    }
    match /courses/{courseId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role in ['ADMIN', 'STAFF'];
    }
    match /attendanceSessions/{sessionId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role in ['ADMIN', 'STAFF', 'CR'];
      
      match /records/{studentId} {
        allow read: if request.auth != null;
        allow write: if request.auth != null && (request.auth.uid == studentId || get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role in ['ADMIN', 'STAFF', 'CR']);
      }
    }
    match /{document=**} {
      allow read, write: if request.auth != null; // Refine for production
    }
  }
}
```

## 9. Configure Storage Rules
Go to **Storage > Rules** and apply:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /materials/{userId}/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 10. Configure Gemini API securely
Add your Gemini API Key to your `.env` (web) and AI Studio Secrets (Android) so the AI Assistant and AI File Analysis tools work securely without exposing the key in source control.
