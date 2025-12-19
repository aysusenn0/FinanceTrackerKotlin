# Finance Tracker - Android App

Multi-currency budget tracking application built with Kotlin and MVVM architecture.

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **Async:** Coroutines & StateFlow
- **Networking:** Retrofit & OkHttp
- **JSON Parser:** Gson
- **UI:** ViewBinding & RecyclerView

## Prerequisites

This Android app requires a backend API to function. You need both components running:

**Backend Requirements:**
- .NET 8 Web API
- MS SQL Server (Express)
- Redis Cache

## Setup & Run

### Step 1: Start Backend API

1. Open the backend project in Visual Studio 2022
2. Ensure SQL Server and Redis are running
3. Start the API project
4. Verify it's running at `http://localhost:5000/swagger`

### Step 2: Configure Android App

1. Open this project in Android Studio
2. Update `BASE_URL` in `network/RetrofitClient.kt`:

```kotlin
// For Android Emulator
private const val BASE_URL = "http://10.0.2.2:5000/"

// For Physical Device (use your PC's IP)
private const val BASE_URL = "http://192.168.1.XXX:5000/"
```

3. Run the app on your emulator or device

## Network Security

The app uses `network_security_config.xml` to allow HTTP connections in development:
- HTTP is enabled only for localhost/emulator
- All other domains require HTTPS

## Features

- Track income and expenses
- Multi-currency support
- Real-time exchange rates
- Transaction history
- Budget overview

## License

This project is developed for educational purposes.
