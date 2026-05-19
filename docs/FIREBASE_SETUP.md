# Firebase setup steps

1. Create a Firebase project at https://console.firebase.google.com/.
2. Add Android app package `com.example.top`.
3. Download `google-services.json` and place it in `app/google-services.json`.
4. In Firebase Console, enable **Authentication** with Email/Password sign-in.
5. Create **Cloud Firestore** database and allow authenticated users to read/write their own user doc.
6. Enable **Cloud Storage** for profile images.
7. Sync Android Studio so Google Services + Firebase BOM dependencies are installed.
8. Run app and create a real account from Create Account screen.

## Current architecture
- `FirebaseAuthService` handles login/create-account/password reset and session.
- `FirestoreService` stores and loads user profile data.
- `FirebaseStorageService` provides profile image upload structure.
- `FirebaseAuthRepository` is the repository layer used by `AuthViewModel`.
- `AuthViewModel` manages auth state (`Loading`, `Authenticated`, `Unauthenticated`) and connectivity state.
