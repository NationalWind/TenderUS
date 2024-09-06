# Client for TenderUS

This is the Android client for the TenderUs app, written in Kotlin using Jetpack Compose. The app allows users to browse, like, and pass profiles, and includes features such as user matching, chat, and profile management.

## Prerequisites

Before you can run the project, you'll need the following:

- Android Studio installed.
- A Firebase project configured with Firestore, Firebase Authentication, and Firebase Storage.
- A device or emulator running at least Android 6.0 (API level 23).

## How to download and run

### 1. Clone this repository

```sh
git clone https://github.com/NationalWind/TenderUS.git
```

### 2. Go to the Client folder

Navigate to the *client* folder and open it in Android Studio:

```sh
cd ./client
```

In Android Studio:

1. Select **Open an Existing Project**.
2. Choose the *client* directory.
3. Wait for *Gradle* to sync the project.

### 3. Run the application

1. Select a device or emulator from the device dropdown in Android Studio.
2. Press **Run** or use the following command to build and install the app on your connected device:

```sh
./gradlew installDebug
```

