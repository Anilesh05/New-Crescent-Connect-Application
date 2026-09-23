import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY || "AIzaSyFakeKeyForLocalDevelopmentAndTesting",
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || "crescentconnect.firebaseapp.com",
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || "crescentconnect",
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || "crescentconnect.firebasestorage.app",
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || "452537646306",
  appId: import.meta.env.VITE_FIREBASE_APP_ID || "1:452537646306:web:82689dcfe38fccf7a7f2bb"
};

export const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const db = getFirestore(app);
