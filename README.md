# ChampionCart 🛒

<p align="center">
  <strong>Smart Grocery Shopping for Israeli Consumers</strong>
</p>

ChampionCart is a modern Android application that helps Israeli shoppers find the best grocery prices across major supermarket chains. With real-time price comparison, smart shopping lists, and an intuitive Hebrew-first interface, ChampionCart makes saving money on groceries effortless.

## ✨ Features

### 🔍 Smart Price Comparison
- **Real-time price updates** from major Israeli supermarket chains
- **Visual price indicators** - instantly see best, mid, and high prices
- **Store comparison** - find which store offers the best total cart price
- **Barcode scanning** - quick product lookup using ML Kit

### 🛒 Intelligent Shopping Cart
- **Smart cart management** - add items and track quantities
- **Best store calculator** - automatically finds the cheapest store for your entire cart
- **Save carts** - save and reload shopping lists for future use
- **Offline support** - cart persists locally with cloud sync

### 🎨 Modern User Experience
- **Electric Harmony Design System** - vibrant, engaging UI with glassmorphic effects
- **Time-based theming** - UI adapts to time of day (morning, afternoon, evening, night)
- **Hebrew-first design** - complete RTL support throughout the app
- **Smooth animations** - spring physics with accessibility support
- **Dark/Light modes** - automatic theme switching

### 🏙️ Location-Aware
- **City selection** - prices adjusted based on your location
- **Store locator** - find nearby stores (coming soon)
- **Regional deals** - discover local promotions

### 🔐 User Management
- **JWT authentication** - secure login/registration
- **Guest mode** - try the app without creating an account
- **Profile management** - save preferences and view shopping history

## 🛠️ Tech Stack

### Core Technologies
| Category | Technology |
|----------|------------|
| **Language** | [Kotlin](https://kotlinlang.org/) 2.0.21 |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) + Material3 |
| **Architecture** | MVVM + Clean Architecture |
| **Dependency Injection** | [Hilt](https://dagger.dev/hilt/) 2.51 |
| **Networking** | [Retrofit](https://square.github.io/retrofit/) 2.9.0 + OkHttp 4.12.0 |
| **Local Storage** | SharedPreferences (TokenManager, CartManager) |
| **Navigation** | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) 2.9.0 |

### Additional Libraries
- **Camera**: CameraX 1.4.2
- **Barcode Scanning**: ML Kit 17.3.0
- **Image Loading**: Coil 2.5.0
- **Animations**: Lottie 6.3.0
- **Location**: Google Play Services 21.3.0
- **Permissions**: Accompanist 0.32.0
- **Biometric**: AndroidX Biometric 1.4.0

## 📁 Project Structure

```
app/src/main/java/com/example/championcart/
├── data/
│   ├── api/              # API interfaces (Retrofit)
│   ├── local/            # Local storage (TokenManager, CartManager)
│   ├── mappers/          # Data model mappers
│   ├── models/           # API request/response models
│   └── repository/       # Repository implementations
├── domain/
│   ├── models/           # Business logic models
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Business use cases
├── presentation/
│   ├── components/       # Reusable UI components
│   ├── navigation/       # Navigation setup
│   ├── screens/          # Screen composables & ViewModels
│   └── theme/            # Custom theme (Electric Harmony)
├── di/                   # Dependency injection modules
└── utils/                # Utility functions
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK** 17 or later
- **Android SDK** API 35
- **Minimum Android Version**: API 26 (Android 8.0)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/ChampionCart.git
   cd ChampionCart
   ```

2. **Open in Android Studio**
   - File → Open → Select the project directory
   - Wait for Gradle sync to complete

3. **Configure the backend URL** (optional)
   - The app is pre-configured to work with the default backend
   - To use a custom backend, update the base URL in `NetworkModule.kt`

4. **Build and run**
   ```bash
   ./gradlew assembleDebug
   # Or use Android Studio's Run button
   ```

### Running on Device/Emulator

1. **Enable developer options** on your device
2. **Connect via USB** or start an emulator
3. **Run the app** from Android Studio

## 🏗️ Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (UI, ViewModels, Navigation)           │
├─────────────────────────────────────────┤
│           Domain Layer                  │
│  (Use Cases, Repository Interfaces)     │
├─────────────────────────────────────────┤
│            Data Layer                   │
│  (API, Local Storage, Repositories)     │
└─────────────────────────────────────────┘
```

### Key Architecture Components

#### Repository Pattern
```kotlin
interface ProductRepository {
    suspend fun searchProducts(query: String, city: String): Flow<Result<List<Product>>>
    suspend fun getProductByBarcode(barcode: String): Flow<Result<Product>>
}
```

#### ViewModel with StateFlow
```kotlin
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
}
```

## 📡 API Documentation

### Base URL
```
https://your-backend-url.com/api/
```

### Authentication Endpoints

| Endpoint | Method | Description | Request Body |
|----------|---------|-------------|--------------|
| `/auth/register` | POST | Register new user | `{email, password, name}` |
| `/auth/login` | POST | User login | `{email, password}` |

### Product Endpoints

| Endpoint | Method | Description | Parameters |
|----------|---------|-------------|------------|
| `/products/search` | GET | Search products | `query, city, limit` |
| `/products/barcode/{barcode}` | GET | Get product by barcode | `barcode, city` |
| `/prices/by-item/{city}/{item}` | GET | Get prices by item | `city, item_name` |

### Cart Endpoints

| Endpoint | Method | Description | Request Body |
|----------|---------|-------------|--------------|
| `/carts/save` | POST | Save shopping cart | `{cartName, city, items}` |
| `/carts/saved` | GET | Get saved carts | - |
| `/carts/{id}` | GET | Get cart details | - |
| `/cheapest-cart` | POST | Find cheapest store | `{city, items}` |

## 🎨 Design System

### Electric Harmony Theme

Our custom design system creates an engaging, modern shopping experience:

#### Color Palette
- **Primary**: Electric Mint `#00D9A3` - CTAs and primary actions
- **Secondary**: Cosmic Purple `#7B3FF2` - Premium features
- **Tertiary**: Neon Coral `#FF6B9D` - Deals and urgent actions
- **Success**: Green `#00E676` - Best prices
- **Warning**: Amber `#FFB300` - Mid-range prices
- **Error**: Red `#FF5252` - High prices

#### Typography
- **Display**: Space Grotesk Bold / Heebo Black (Hebrew)
- **Headlines**: Inter Variable (300-800)
- **Body**: Inter / Rubik (Hebrew)
- **Prices**: JetBrains Mono (tabular numbers)

#### Key Design Features
- **Glassmorphic effects** with blur and transparency
- **Spring animations** for natural motion
- **Time-based theming** that adapts throughout the day
- **Responsive layouts** for phones and tablets
- **Accessibility-first** with proper contrast and touch targets

## 🧪 Testing

### Running Tests

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate test coverage report
./gradlew jacocoTestReport
```

### Test Coverage
- **Unit Tests**: ViewModels, Repositories, Use Cases
- **UI Tests**: Composable components, Navigation
- **Integration Tests**: API integration, Database

## 🤝 Contributing

### Quick Start for Contributors

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable names
- Write self-documenting code
- Add KDoc comments for public APIs

## 📄 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- [Material Design 3](https://m3.material.io/) for the design system
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI toolkit
- Israeli supermarket chains for price data
- The open-source community for amazing libraries

## 📞 Contact

For questions, suggestions, or issues:
- Yarin Manoah - yarinmanoah1443@gmail.com
- Itay Ostraich - itayost1@gmail.com

---

<p align="center">
  Made with ❤️ for the Israeli shopping community
  <br>
  <i>Making grocery shopping smarter, one comparison at a time</i>
</p>
