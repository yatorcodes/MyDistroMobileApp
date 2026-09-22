# MyDistro

Supply-chain distribution platform for the Kenyan market — connecting manufacturers, distributors, hardware stores, and delivery drivers.

This repository is the **Android mobile app** (Jetpack Compose). Factories, distributors, and administrators use a separate Angular web console. The backend is Spring Boot + PostgreSQL (not in this repo).

## Roles

| Role | App |
|------|-----|
| Driver | This Android app |
| Customer / Hardware store | This Android app (next vertical slice) |
| Distributor / Factory / Admin | Angular web app |

## Current status

**Driver vertical slice is implemented** (mock data — not connected to a live backend):

```
Splash → Role selection → Driver login → Trip list → Trip details → Delivery confirmation
```

Customer browsing, M-Pesa, and live tracking are intentionally deferred. Mock credentials and OTP validation are labelled clearly in the UI.

### Demo credentials (prototype only)

| Field | Value |
|-------|--------|
| Phone | `0712345678` |
| Password | `driver123` |
| Delivery OTP | `184279` |

## Tech stack

- Kotlin 1.9 / Jetpack Compose / Material 3
- Hilt + KSP
- Navigation Compose
- Retrofit + OkHttp (wired; unused until API contract exists)
- Room (trip cache)
- Coil (ready for product images)
- MVVM + StateFlow + repository interfaces

## Architecture

Package-by-feature:

```
app/src/main/java/com/emmanuelyator/mydistro/
├── core/
│   ├── common/          # UiState, DataResult, validators
│   ├── designsystem/    # Theme, colours, shared components
│   ├── model/           # Domain models
│   ├── network/         # Retrofit, auth interceptor, token store
│   └── database/        # Room
├── feature/
│   ├── auth/            # AuthRepository (+ mock)
│   ├── driver/          # Login, trips, details, confirmation
│   ├── splash/
│   ├── rolepicker/
│   └── placeholder/
└── navigation/
```

Mock repositories implement the same interfaces as future Retrofit ones. Swapping to the real API is a Hilt `@Binds` change — UI and ViewModels stay put.

## Design system

Brand palette (starting points):

- Navy `#0B1930`
- Orange `#FF641F`
- Background `#F7F8FA`
- Surface `#FFFFFF`

Screens use `MyDistroTheme` / MaterialTheme — avoid hardcoding colours in feature screens.

## Requirements

- Android Studio Hedgehog+ (or compatible)
- JDK 17 or 21 for Android Studio / Gradle (JDK 26 is not supported by this Gradle version)
- minSdk 24, targetSdk 34
- Physical device or emulator

## Build & run

1. Open the project in Android Studio.
2. Sync Gradle.
3. Connect a phone (USB or [wireless debugging](https://developer.android.com/tools/adb#wireless-debugging)) or start an emulator.
4. Run the `app` configuration.

Command line (PowerShell), if `JAVA_HOME` points at JDK 17/21:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
```

## Tests

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

## Security notes

- Do **not** commit API secrets, JWT signing keys, or M-Pesa credentials into this app.
- Token storage is in-memory for the prototype — replace with EncryptedSharedPreferences / DataStore before production auth.
- Authentication is **not** secure until the Spring Boot login + JWT flow is wired and validated.

## Roadmap (high level)

1. ~~Driver login → trips → details → OTP confirmation~~ (done, mock)
2. Customer signup / browse / cart / order
3. Backend integration (Phase 7) when the API contract is ready
4. M-Pesa payment
5. Live tracking (driver location → backend → WebSocket → map)

## License

Private / proprietary — all rights reserved.

