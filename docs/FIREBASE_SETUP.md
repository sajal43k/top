# Firebase setup steps

1. Open <https://console.firebase.google.com/> and create a Firebase project.
2. Add an Android app to the Firebase project.
3. Use this package name: `com.example.top`.
4. Download `google-services.json` from Firebase.
5. Put `google-services.json` inside the `app/` folder.
6. Enable Authentication in Firebase Console. Start with Email/Password or Phone Auth.
7. Create a Firestore database in production or test mode while learning.
8. Enable Firebase Storage if profile pictures and prize photos will be uploaded.
9. Add Firebase Gradle dependencies in the next milestone.
10. Replace `InMemoryAuthRepository` with a Firebase-backed repository.

Suggested Firestore collections are already listed in `FirebaseConfig`: users, groups, scores, and attendance.
