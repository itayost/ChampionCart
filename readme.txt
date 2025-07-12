# ChampionCart 🛒

A modern Android grocery price comparison app built for the Israeli market. ChampionCart helps users find the best prices across major supermarket chains, create smart shopping lists, and track their savings with an intuitive, Hebrew-first user experience.

## ✨ Features

### 🔐 Authentication & User Management
- **JWT-based Authentication** with secure token management
- **Guest Mode** for users who prefer not to register
- **User Registration/Login** with email validation
- **Onboarding Flow** for first-time users

### 🏠 Smart Home Experience
- **City Selection** for localized price comparison
- **Featured Deals** and promotions
- **Quick Stats** showing cart totals and savings
- **Product Categories** for easy browsing

### 🔍 Advanced Product Search
- **Real-time Search** across multiple supermarket chains
- **Hebrew Language Support** with RTL layout
- **Price Comparison** with visual indicators (Best/Mid/High prices)
- **Barcode Scanning** using CameraX and ML Kit
- **Product Details** with store-specific pricing

### 🛒 Intelligent Shopping Cart
- **Local Cart Management** with persistent storage
- **Quantity Management** with intuitive controls
- **Best Store Calculator** to minimize total cost
- **Save Carts** for registered users
- **Share Cart Information** via maps integration

### 📱 Modern UI/UX
- **Material Design 3** with custom "Electric Harmony" theme
- **Dark/Light Mode** support
- **Responsive Design** for different screen sizes
- **Smooth Animations** with spring physics
- **RTL Layout Support** for Hebrew text
- **Glassmorphic Design Elements**

## 🏗️ Technical Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material3
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Local Storage**: SharedPreferences (TokenManager, CartManager, PreferencesManager)
- **Navigation**: Navigation Compose
- **Camera**: CameraX + ML Kit Barcode Scanning
- **Image Loading**: Coil
- **Animations**: Lottie + Compose animations

### Project Structure
```
app/src/main/java/com/example/championcart/
├── data/
│   ├── api/          # Retrofit API interfaces
│   ├── local/        # Local storage managers
│   ├── mappers/      # Data mapping functions
│   ├── models/       # API data models
│   └── repository/   # Repository implementations
├── domain/
│   ├── models/       # Domain models
│   ├── repository/   # Repository interfaces
│   └── usecase/      # Business logic use cases
├── presentation/
│   ├── components/   # Reusable UI components
│   ├── navigation/   # Navigation setup
│   └── screens/      # Screen composables & ViewModels
├── ui/theme/         # Design system & styling
├── di/               # Dependency injection modules
└── utils/            # Utility functions
```

## 🔌 API Integration

### Backend Endpoints
The app integrates with multiple backend services:

- **Authentication**: `/api/auth/register`, `/api/auth/login`
- **Product Search**: `/api/products/search`, `/api/products/barcode/{barcode}`
- **Price Comparison**: `/api/prices/by-item/{city}/{item_name}`
- **Cart Management**: `/api/carts/save`, `/api/carts/saved`
- **Cities**: `/api/cities`
- **Cheapest Cart**: `/api/cheapest-cart`

### Data Models
```kotlin
// Core domain models
data class Product(
    val id: String,
    val barcode: String?,
    val name: String,
    val category: String,
    val bestPrice: Double,
    val bestStore: String,
    val stores: List<StorePrice>,
    val imageUrl: String?
)

data class StorePrice(
    val storeName: String,
    val price: Double,
    val priceLevel: PriceLevel,
    val unit: String?,
    val lastUpdated: String?
)
```

## 🎨 Design System

### "Electric Harmony" Color Palette
- **Primary**: Electric Mint (#00D9A3) - Main actions and CTAs
- **Secondary**: Cosmic Purple (#7B3FF2) - Premium features  
- **Tertiary**: Neon Coral (#FF6B9D) - Deals and urgent items
- **Semantic Colors**: Success Green, Warning Amber, Error Red
- **Price Indicators**: Visual color coding for best/mid/high prices

### Typography
- **Display**: Space Grotesk Bold (Latin) / Heebo Black (Hebrew)
- **Headlines**: Inter Variable (weight 300-800)
- **Body**: Inter Variable / Rubik (Hebrew)
- **Prices**: JetBrains Mono Variable (tabular numbers)

### Key Features
- **RTL Layout Support** for Hebrew
- **Responsive Design** adapting to screen sizes
- **Glassmorphic Effects** for modern aesthetics
- **Material 3 Animations** with reduced motion support
- **Accessibility** built-in with proper contrast ratios

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 26+ (minimum) / 35 (target)
- Kotlin 2.0.21
- Java 17

### Installation
1. Clone the repository:
```bash
git clone https://github.com/yourusername/ChampionCart.git
cd ChampionCart
```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Configure network settings in `app/src/main/res/xml/network_security_config.xml` if needed

5. Build and run the app:
```bash
./gradlew assembleDebug
```

### Configuration
The app requires network connectivity to access the price comparison APIs. Make sure to:
- Set up proper base URLs for your backend services
- Configure authentication endpoints
- Test with real API endpoints or mock services

## 📱 Screen Navigation

### Main Flow
1. **Splash Screen** - App initialization and auth check
2. **Onboarding** - First-time user introduction (if needed)
3. **Authentication** - Login/Register or continue as guest
4. **Home Screen** - Main dashboard with city selection and features
5. **Search** - Product search and price comparison
6. **Cart** - Shopping list management and store optimization
7. **Profile** - User settings and saved carts

### Secondary Screens
- **Scan** - Barcode scanning for products
- **Product Detail** - Detailed product information
- **Terms of Service** - Legal information
- **Privacy Policy** - Privacy guidelines

## 🔧 Key Components

### Local Data Management
- **TokenManager**: Secure JWT token storage and validation
- **CartManager**: Shopping cart state management with persistence
- **PreferencesManager**: User preferences and app settings

### Repository Pattern
Clean separation between data sources and business logic:
- **AuthRepository**: User authentication
- **ProductRepository**: Product search and details
- **PriceRepository**: Price comparison across stores
- **CartRepository**: Shopping cart operations

### ViewModels
MVVM architecture with reactive state management:
- Lifecycle-aware components
- StateFlow for UI state
- Coroutines for async operations
- Error handling and loading states

## 🌐 Internationalization

### Hebrew Support
- **RTL Layout**: Fully supports right-to-left text direction
- **Hebrew Typography**: Optimized fonts for Hebrew text
- **Cultural Adaptation**: Israeli market-specific features
- **Bilingual Support**: Hebrew primary, English secondary

## 🔒 Security Features

- **JWT Token Management**: Secure authentication with automatic refresh
- **Network Security**: HTTPS enforcement and certificate pinning
- **Local Storage**: Encrypted sensitive data storage
- **Guest Mode**: Privacy-focused usage without registration

## 🧪 Testing

### Test Structure
- **Unit Tests**: Domain logic and repository tests
- **UI Tests**: Compose UI testing with Espresso
- **Integration Tests**: API integration testing

### Running Tests
```bash
# Unit tests
./gradlew test

# UI tests
./gradlew connectedAndroidTest
```

## 📦 Dependencies

### Core Dependencies
```kotlin
// Compose
implementation("androidx.compose.ui:ui:$compose_version")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose:1.10.1")

// Architecture
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.1")
implementation("androidx.navigation:navigation-compose:2.9.0")

// Dependency Injection
implementation("com.google.dagger:hilt-android:2.51")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Camera & ML
implementation("androidx.camera:camera-camera2:1.4.2")
implementation("com.google.mlkit:barcode-scanning:17.3.0")
```

## 🚧 Development Guidelines

### Code Style
- **Kotlin Coding Conventions**: Follow official Kotlin style guide
- **Compose Best Practices**: Use state hoisting and unidirectional data flow
- **Clean Architecture**: Maintain clear separation of concerns
- **MVVM Pattern**: Keep ViewModels focused on UI state management

### Architecture Decisions
- **Single Activity**: Navigation Compose with single MainActivity
- **Repository Pattern**: Centralized data access layer
- **StateFlow**: Reactive state management
- **Dependency Injection**: Hilt for dependency management

## 🔮 Future Roadmap

### Phase 2 Features
- [ ] **Price Alerts**: Notifications when products go on sale
- [ ] **Store Locator**: Maps integration with store locations
- [ ] **Advanced Analytics**: Personal savings tracking
- [ ] **Social Features**: Share shopping lists with family
- [ ] **Offline Support**: Cached data for offline usage

### Phase 3 Enhancements
- [ ] **AI Recommendations**: Smart product suggestions
- [ ] **Voice Search**: Voice-activated product search
- [ ] **Widget Support**: Home screen widgets for quick access
- [ ] **Wear OS App**: Companion app for smartwatches
- [ ] **Multi-language**: Full internationalization support

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Setup
- Follow the existing code style and architecture patterns
- Write tests for new features
- Update documentation for significant changes
- Ensure RTL layout compatibility for new UI components

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Material Design 3**: Google's design system
- **Jetpack Compose**: Modern Android UI toolkit
- **ML Kit**: Google's machine learning APIs
- **Israeli Grocery Chains**: Data providers for price comparison

---

**Built with ❤️ for the Israeli shopping community**

*Making grocery shopping smarter, one price comparison at a time.*
