# ChampionCart 🛒

> A modern Android grocery price comparison app for the Israeli market

ChampionCart helps users find the best prices across major supermarket chains, create smart shopping lists, and track their savings with an intuitive, Hebrew-first user experience.

## Features

### Core Functionality
- 🔐 **JWT Authentication** with guest mode support
- 🏠 **Smart Home Dashboard** with city selection and featured deals
- 🔍 **Real-time Product Search** across multiple supermarket chains
- 📱 **Barcode Scanner** using CameraX and ML Kit
- 🛒 **Intelligent Shopping Cart** with best store calculator
- 💰 **Price Comparison** with visual indicators
- 🌙 **Hebrew/RTL Support** throughout the entire app

### User Experience
- **Material Design 3** with custom "Electric Harmony" theme
- **Dark/Light Mode** automatic switching
- **Responsive Design** for tablets and phones
- **Smooth Animations** with accessibility support
- **Offline Cart Management** with cloud sync

## Screenshots

*Add your app screenshots here*

## Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material3 |
| **Architecture** | MVVM + Clean Architecture |
| **Dependency Injection** | Hilt |
| **Networking** | Retrofit + OkHttp |
| **Local Storage** | SharedPreferences |
| **Navigation** | Navigation Compose |
| **Camera** | CameraX + ML Kit |
| **Image Loading** | Coil |
| **Animations** | Lottie + Compose |

## Project Structure

```
app/src/main/java/com/example/championcart/
├── data/
│   ├── api/              # Retrofit API interfaces
│   ├── local/            # Local storage managers
│   ├── mappers/          # Data mapping functions
│   ├── models/           # API data models
│   └── repository/       # Repository implementations
├── domain/
│   ├── models/           # Domain models
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Business logic
├── presentation/
│   ├── components/       # Reusable UI components
│   ├── navigation/       # Navigation setup
│   └── screens/          # Screen composables & ViewModels
├── ui/theme/             # Design system & styling
├── di/                   # Dependency injection
└── utils/                # Utility functions
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 26+ (minimum) / 35 (target)
- Kotlin 2.0.21
- Java 17

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/ChampionCart.git
   cd ChampionCart
   ```

2. **Open in Android Studio**
   - Import the project
   - Wait for Gradle sync to complete

3. **Build and run**
   ```bash
   ./gradlew assembleDebug
   ```

### Configuration

The app integrates with backend APIs for price data. Key endpoints include:

- Authentication: `/api/auth/register`, `/api/auth/login`
- Product Search: `/api/products/search`
- Price Comparison: `/api/prices/by-item/{city}/{item_name}`
- Cart Management: `/api/carts/save`

## Architecture

### MVVM + Clean Architecture

The app follows Clean Architecture principles with clear separation of concerns:

```kotlin
// Repository Pattern
interface ProductRepository {
    suspend fun searchProducts(query: String, city: String): List<Product>
    suspend fun getProductByBarcode(barcode: String, city: String): Product?
}

// Domain Models
data class Product(
    val id: String,
    val barcode: String?,
    val name: String,
    val category: String,
    val bestPrice: Double,
    val bestStore: String,
    val stores: List<StorePrice>
)
```

### State Management

ViewModels use StateFlow for reactive UI updates:

```kotlin
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    fun searchProducts(query: String) {
        // Implementation
    }
}
```

## Design System

### Color Palette

| Color | Hex | Usage |
|-------|-----|-------|
| Electric Mint | `#00D9A3` | Primary actions, CTAs |
| Cosmic Purple | `#7B3FF2` | Secondary, Premium features |
| Neon Coral | `#FF6B9D` | Deals, Urgent actions |
| Success Green | `#00E676` | Best prices, Success states |
| Warning Amber | `#FFB300` | Mid-range prices |
| Error Red | `#FF5252` | High prices, Errors |

### Typography

- **Display**: Space Grotesk Bold (Latin) / Heebo Black (Hebrew)
- **Headlines**: Inter Variable (300-800 weight)
- **Body**: Inter Variable / Rubik (Hebrew)
- **Prices**: JetBrains Mono (tabular numbers)

## Key Features

### 1. Barcode Scanner
Real-time barcode scanning with camera integration:
- CameraX for camera management
- ML Kit for barcode recognition
- Automatic product lookup
- Flash toggle support

### 2. Price Comparison
Visual price comparison across stores:
- Color-coded price levels (Best/Mid/High)
- Real-time price updates
- Store-specific information
- Savings calculation

### 3. Smart Shopping Cart
Intelligent cart management:
- Local persistence with cloud sync
- Best store recommendation
- Quantity management
- Total cost optimization

### 4. Hebrew Support
Complete RTL language support:
- Right-to-left layout
- Hebrew typography
- Cultural adaptations
- Bilingual interface

## API Integration

### Core Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/login` | POST | User authentication |
| `/api/products/search` | GET | Search products by name |
| `/api/products/barcode/{barcode}` | GET | Get product by barcode |
| `/api/prices/by-item/{city}/{item}` | GET | Compare prices by city |
| `/api/carts/save` | POST | Save shopping cart |
| `/api/cities` | GET | Available cities |

### Sample Response

```json
{
  "item_name": "חלב תנובה 3%",
  "category": "חלב ומוצרי חלב",
  "prices": [
    {
      "store_name": "שופרסל",
      "price": 5.90,
      "unit": "1L"
    },
    {
      "store_name": "רמי לוי",
      "price": 5.20,
      "unit": "1L"
    }
  ]
}
```

## Testing

### Running Tests

```bash
# Unit tests
./gradlew test

# UI tests
./gradlew connectedAndroidTest

# Generate test coverage
./gradlew jacocoTestReport
```

### Test Structure

- **Unit Tests**: Repository and ViewModel logic
- **UI Tests**: Compose UI components
- **Integration Tests**: API integration

## Dependencies

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

## Development Guidelines

### Code Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Write self-documenting code with minimal comments
- Maintain consistent formatting with ktlint

### Architecture Rules
- Keep ViewModels focused on UI state
- Use Repository pattern for data access
- Implement proper error handling
- Follow unidirectional data flow

### UI/UX Guidelines
- Support RTL layouts for all new components
- Use Material 3 design tokens
- Implement proper accessibility features
- Test with reduced motion settings

## Roadmap

### Phase 2 (Next Release)
- [ ] Price alerts and notifications
- [ ] Store locator with maps
- [ ] Advanced savings analytics
- [ ] Social sharing features
- [ ] Offline data caching

### Phase 3 (Future)
- [ ] AI-powered recommendations
- [ ] Voice search integration
- [ ] Wear OS companion app
- [ ] Multi-language support
- [ ] Widget support

## Contributing

We welcome contributions! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Contribution Guidelines

- Ensure all tests pass
- Follow the existing code style
- Update documentation for new features
- Test RTL layout compatibility
- Include proper error handling

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/ChampionCart/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/ChampionCart/discussions)
- **Email**: support@championcart.app

## Acknowledgments

- [Material Design 3](https://m3.material.io/) - Design system
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - UI toolkit
- [ML Kit](https://developers.google.com/ml-kit) - Machine learning APIs
- Israeli grocery chains for price data

---

**Built with ❤️ for the Israeli shopping community**

*Making grocery shopping smarter, one price comparison at a time.*
