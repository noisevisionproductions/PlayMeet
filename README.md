# PlayMeet 🎾⚽🏀

A native Android application written in Java that connects sports enthusiasts to play together in
real life.

## 📖 The Story Behind PlayMeet

One boring day, I had the urge to play tennis, but nobody from my friends list was available. At the
time, I was also struggling to find portfolio ideas for my first job application as a Java Software
Developer. That's when the idea struck me: **PlayMeet**.

This application is my first major independent project, started around October. The goal was simple:
help users find people for sports games in real-life, whether they are first-timers, amateurs, or
advanced players.

The road was bumpy - I had zero prior experience with the Android SDK or front-end development - but
it
was an incredible learning journey. This project represents my belief that with enough persistence (
and a bit of Googling), anything can be built.

## ✨ Features

* **User Accounts:** Secure registration and login handling via Firebase Authentication.
* **Create & Manage Games:** Users can create posts detailing the sport, location, date, and number
  of players needed.
* **Advanced Filtering:** Filter games by specific requirements (sport type, location) to find the
  perfect match.
* **Real-time Chat:** Integrated chat functionality allowing users to message each other instantly
  regarding games.
* **Smart Registration:** Users can sign up for posts they find interesting.
* **Profile Customization:** Upload profile pictures and manage user details.

## 📱 Download

The app was published on the Google Play Store, but no longer because of a lack of users.

## 🛠 Tech Stack

I built this application using native Android development tools, focusing on a clean architecture
and robust backend integration.

### Core

* **Language:** Java 17
* **SDK:** Android SDK (Min 26, Target 34)
* **Build System:** Gradle Kotlin DSL

### Architecture & UI

* **UI Toolkit:** XML Layouts, Material Design Components, BottomSheetDialogFragments
* **Binding:** ViewBinding & DataBinding
* **Navigation:** Android Navigation Component
* **Image Loading:** Glide (with CircleImageView for profiles)

### Backend & Data (Firebase)

* **Authentication:** For secure user login/signup
* **Realtime Database:** Stores user profiles and handles live chat messages
* **Firestore:** Handles heavy lifting for game posts and complex filtering queries
* **Storage:** Stores user-uploaded images (profile pictures)
* **Messaging (FCM):** Push notifications

## 🧠 Development Journey & Technical Decisions

This project was a massive learning curve. Here are the key technical challenges I overcame:

### 1. The Database Dilemma (Realm vs. Firebase)

Initially, I chose **Realm** because I loved its simple filtering. However, I quickly realized it
was designed primarily for offline-first usage, and syncing data across devices was becoming a
nightmare.

**Pivot 1:** I migrated to Firebase Realtime Database. It worked great for chat, but filtering logic
became a bottleneck. I was filtering posts on the client side, which is a performance killer for
large datasets.

**Pivot 2 (Final):** I adopted a hybrid approach. I kept Realtime Database for the chat (where speed
is key) but migrated the Posts and Registrations to Cloud Firestore. Firestore allows for complex
chaining of queries server-side, meaning the app only downloads exactly what the user asks for.

### 2. Mastering the UI

I had to learn XML layout files from scratch. My first designs were rigid, but I learned to use
**ConstraintLayout** for responsiveness. I also realized that standard dialogs were too small for
event details, leading me to implement **BottomSheetDialogFragment** for a modern, slide-up
experience.

## ⚖️ License and Use Restrictions

This project is created for educational and experimental purposes. All rights reserved.