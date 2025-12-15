# 🌍 Pangea App - Android eSIM Marketplace

## 📖 Descripción del Proyecto

**Pangea App** es una aplicación Android nativa que permite a los usuarios comprar y gestionar planes de datos eSIM para diferentes países y regiones del mundo. La aplicación ofrece autenticación segura, navegación intuitiva, compras mediante Stripe, y gestión completa del ciclo de vida de eSIMs, todo con soporte offline mediante caché local.

---

## 🎯 Funcionalidades Implementadas (Módulo)

### 1. 🎬 Integración de Elementos Multimedia

#### **Video Hero en Pantalla de Paquetes**
- Video de fondo dinámico que se reproduce automáticamente
- Posicionamiento como sección hero que empuja contenido hacia abajo
- Soporte para modo silencioso y loop automático
- Implementado con `VideoView` nativo de Android
- Mejora la experiencia visual y engagement del usuario

```kotlin
// Ubicación: PackagesFragment.kt
binding.videoView.apply {
    setVideoURI(Uri.parse("android.resource://" + context.packageName + "/" + R.raw.hero_video))
    setOnPreparedListener { mediaPlayer ->
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0f, 0f)
    }
    start()
}
```

#### **Códigos QR para Activación de eSIM**
- Códigos QR cargados desde URL del backend
- Visualización de QR con **Coil** (image loading library)
- Los QR contienen datos de activación LPA (Local Profile Assistant)
- Interfaz dedicada para mostrar y compartir códigos QR
- Copia de datos de activación al portapapeles

```kotlin
// ESimDetailFragment.kt - Carga de QR desde URL
binding.qrCodeImage.load(esim.qrCodeUrl) {
    crossfade(true)
    placeholder(R.drawable.placeholder_qr)
    error(R.drawable.error_qr)
}
```

### 2. 🔐 Sistema de Autenticación Robusto

#### **Autenticación con JWT**
- Login y registro de usuarios con validación en tiempo real
- Tokens JWT almacenados de forma segura
- Interceptor automático que agrega token a todas las peticiones HTTP
- Refresh token automático en caso de expiración
- Recuperación de contraseña mediante modal

#### **Encriptación con Google Tink**
- Implementación de **Google Tink** para encriptación AES256-GCM
- Integración con **Android Keystore** para almacenamiento seguro de claves
- Migración automática desde EncryptedSharedPreferences legacy
- Protección de datos sensibles (JWT, user info) en SharedPreferences
- Exclusión de backup para prevenir restauración de datos encriptados en nuevos dispositivos

```kotlin
// TinkManager.kt - Encriptación de datos de sesión
class TinkManager(private val context: Context) {
    private val aead: Aead by lazy {
        AeadConfig.register()
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
        keysetHandle.getPrimitive(Aead::class.java)
    }

    fun encryptToBase64(plaintext: String): String
    fun decryptFromBase64(base64Ciphertext: String): String
}
```

**Características de Seguridad:**
- AES-256-GCM (Galois/Counter Mode) - cifrado autenticado
- Android Keystore - claves protegidas por hardware (TEE/StrongBox)
- Associated Data para prevenir ataques de manipulación
- Exclusión de archivos sensibles del backup automático de Android
- Nuevo dispositivo = requiere nuevo login (correcto para eSIM)

### 3. 🔄 Procesos en Segundo Plano y Multiprocesamiento

#### **Coroutines y Flow**
- **ViewModelScope** para operaciones asíncronas vinculadas al ciclo de vida
- **Flow** para streams de datos reactivos (países, paquetes, eSIMs)
- **StateFlow** para gestión de estado UI reactiva
- **Dispatchers.IO** para operaciones de red y base de datos

```kotlin
// Ejemplo: NetworkBoundResource con cache-first
viewModelScope.launch {
    networkBoundResource(
        query = { plansDao.getAllCountries() },
        fetch = { apiService.getCountries() },
        saveFetchResult = { countries -> plansDao.insertCountries(countries) }
    ).collect { resource ->
        _countries.value = resource
    }
}
```

#### **Conectividad en Segundo Plano**
- **ConnectivityObserver** que monitorea estado de red en tiempo real
- **NetworkCallback** para detectar cambios de conectividad
- Banner offline UI que se muestra/oculta automáticamente
- Reintentos automáticos cuando la conexión se restaura

#### **Estrategia Cache-First (NetworkBoundResource)**
- **Prioridad a caché**: Muestra datos locales inmediatamente
- **Actualización en segundo plano**: Fetch de red sin bloquear UI
- **Sincronización automática**: Actualiza caché con datos frescos
- **Soporte offline completo**: Funciona sin internet

```kotlin
// NetworkBoundResource optimizado
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit
) = flow {
    val data = query().firstOrNull()  // Cache primero
    emit(Resource.Loading(data))       // UI muestra cache mientras carga

    try {
        val fetchedData = fetch()       // Fetch en background
        saveFetchResult(fetchedData)    // Actualiza cache
        emitAll(query().map { Resource.Success(it) })
    } catch (e: Exception) {
        emitAll(query().map { Resource.Error(e.message) })
    }
}
```

### 4. 💳 Integración de Stripe Checkout

#### **Proceso de Pago Completo**
- Integración con Stripe SDK para pagos seguros
- Flujo de checkout con validación de tarjetas
- Soporte para múltiples métodos de pago (Visa, Mastercard, Amex)
- Confirmación de pago y creación automática de eSIM
- Manejo de errores de pago con mensajes claros

---

## 📱 Funcionalidades Principales

### 🗺️ Exploración de Países
- Listado de 200+ países con banderas y datos de cobertura
- Búsqueda en tiempo real por nombre de país
- Indicadores visuales para planes locales vs. regionales
- Filtrado y ordenamiento de países
- Navegación hacia paquetes específicos

### 📦 Catálogo de Paquetes eSIM
- Filtrado por tipo de plan (Solo Datos, Voz + Datos)
- Información detallada: precio, duración, GB incluidos
- Cobertura de países para planes regionales
- Video hero para mejor presentación
- Búsqueda y filtros en tiempo real

### 📱 Gestión de eSIMs
- Listado de eSIMs activas, expiradas y pendientes
- Estados: Instalada, Activa, Expirada, Pendiente de Instalación
- Códigos QR para activación LPA
- Detalles de consumo y expiración
- Botón para instalar eSIM directamente

### 🎨 Interfaz Adaptativa
- **Tema oscuro/claro** automático según preferencias del sistema
- **Logos adaptativos**: Logo con texto negro (light) / texto blanco (dark)
- **Splash Screen** personalizado con logo y slogan
- **Material Design 3** con componentes modernos
- **Bottom Navigation** para navegación principal
- **Swipe to Refresh** en todas las listas

### ⚙️ Configuración
- Perfil de usuario
- Cerrar sesión con confirmación

---

## 🏗️ Arquitectura y Patrones de Diseño

### Arquitectura MVVM (Model-View-ViewModel)

```
┌─────────────────────────────────────────────────────┐
│                    UI Layer                          │
│  Fragments/Activities + Compose Components          │
│  (CountriesFragment, PackagesFragment, etc.)        │
└─────────────────┬───────────────────────────────────┘
                  │ observes StateFlow/Flow
┌─────────────────▼───────────────────────────────────┐
│                 ViewModel Layer                      │
│  (AuthViewModel, CountriesViewModel, etc.)          │
│  - Maneja estado UI                                 │
│  - Procesa eventos del usuario                      │
│  - Expone StateFlow/Flow                            │
└─────────────────┬───────────────────────────────────┘
                  │ calls
┌─────────────────▼───────────────────────────────────┐
│               Repository Layer                       │
│  (RealPlansRepository, RealAuthRepository)          │
│  - Lógica de negocio                                │
│  - Coordinación entre fuentes de datos              │
│  - NetworkBoundResource (cache-first)               │
└─────────────┬─────────────────┬─────────────────────┘
              │                 │
    ┌─────────▼────┐    ┌──────▼────────┐
    │  Local DB    │    │  Remote API   │
    │  (Room)      │    │  (Retrofit)   │
    └──────────────┘    └───────────────┘
```

### Componentes Principales

#### **1. UI Layer**
- **Fragments con ViewBinding**: CountriesFragment, PackagesFragment, ESimsFragment, etc.
- **Jetpack Compose**: Componentes específicos (OfflineBanner, estados de carga)
- **RecyclerView Adapters**: Listas eficientes con DiffUtil
- **Navigation Component**: Navegación type-safe con Safe Args

#### **2. ViewModel Layer**
- **AuthViewModel**: Autenticación, validación de credenciales
- **CountriesViewModel**: Gestión de países, búsqueda, filtrado
- **PackagesViewModel**: Catálogo de paquetes, filtros
- **ESimsViewModel**: Gestión de eSIMs, estados
- **ESimDetailViewModel**: Detalles y activación de eSIM
- **CheckoutViewModel**: Proceso de pago con Stripe

#### **3. Repository Layer**
- **RealAuthRepository**: Autenticación, sesión, tokens
- **RealPlansRepository**: Países y paquetes (cache-first)
- **RealESimsRepository**: Gestión de eSIMs

#### **4. Data Sources**

**Local (Room Database)**
```kotlin
@Database(
    entities = [CountryEntity::class, PackageEntity::class, ESimEntity::class],
    version = 3
)
abstract class PangeaDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
    abstract fun packageDao(): PackageDao
    abstract fun eSimDao(): ESimDao
}
```

**Remote (Retrofit API)**
```kotlin
interface PangeaApiService {
    @GET("countries")
    suspend fun getCountries(): List<CountryDto>

    @GET("packages")
    suspend fun getPackages(@Query("country") country: String): List<PackageDto>

    @POST("esims/purchase")
    suspend fun purchaseESim(@Body request: PurchaseRequest): ESimDto

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): AuthResponse
}
```

### 💉 Inyección de Dependencias (Hilt)

#### **Módulos Implementados**

**NetworkModule**
- Proporciona Retrofit configurado con:
  - Base URL desde BuildConfig
  - Interceptor de autenticación (JWT)
  - Interceptor de conectividad
  - Logging interceptor (solo debug)
  - Conversor Gson

**DatabaseModule**
- Singleton de Room Database
- DAOs (CountryDao, PackageDao, ESimDao)
- Migraciones automáticas

**RepositoryModule**
- Vincula interfaces a implementaciones concretas
- Scope @Singleton para repositorios

**SecurityModule**
- TinkManager para encriptación
- SessionManager para gestión de sesión

**AuthModule**
- Componentes de autenticación
- Interceptores HTTP

---

## 🛡️ Manejo Integral de Errores

### Estrategia de Manejo de Errores

La aplicación implementa un manejo robusto de errores en múltiples capas:

#### **1. Errores de Red**

```kotlin
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val data: T? = null) : Resource<T>()
    data class Loading<T>(val data: T? = null) : Resource<T>()
}
```

**Tipos:**
- **Sin conexión**: Banner offline + cache local automático
- **Timeout**: Mensaje "El servidor tardó demasiado en responder"
- **HTTP 4xx**: Mensajes específicos (401: sesión expirada, 404: no encontrado)
- **HTTP 5xx**: "Error del servidor, intenta más tarde"

**Implementación:**
- Try-catch en repositorios
- Estados específicos en ViewModels
- UI reactiva a estados de error
- Reintentos automáticos para errores temporales

#### **2. Errores de Base de Datos**

```kotlin
try {
    plansDao.insertCountries(countries)
} catch (e: SQLiteException) {
    Log.e(TAG, "Error al guardar países: ${e.message}")
    // Fallback: continuar con datos en memoria
}
```

**Manejo:**
- Logging detallado para debugging
- Fallback a datos en memoria
- Recreación de tablas en caso de corrupción
- Migraciones automáticas entre versiones

#### **3. Errores de Validación (Tiempo Real)**

**AuthViewModel - Validación de Email:**
```kotlin
private fun validateEmail(email: String): Boolean {
    return email.isNotEmpty() &&
           Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
```

**Validaciones Implementadas:**
- Email: formato válido, no vacío
- Contraseña: mínimo 6 caracteres
- Confirmación de contraseña: coincidencia exacta
- Campos requeridos: validación antes de submit

**Feedback en UI:**
- Mensajes de error en tiempo real bajo cada campo
- Botones deshabilitados hasta que validaciones pasen
- Estados visuales claros (rojo para error, verde para válido)

#### **4. Errores de Autenticación**

```kotlin
when (response.code()) {
    401 -> "Credenciales incorrectas"
    403 -> "Cuenta bloqueada o no confirmada"
    404 -> "Usuario no encontrado. ¿Deseas registrarte?"
    else -> "Error de autenticación"
}
```

**Manejo:**
- Credenciales incorrectas: Mensaje claro
- Sesión expirada: Redirect automático a login
- Token inválido: Refresh automático
- Usuario no encontrado: Sugerencia de registro

#### **5. Errores de Pago (Stripe)**

```kotlin
stripe.confirmPayment(intent) { result ->
    when {
        result.isSuccess -> // Procesar compra
        result.isCancelled -> showMessage("Pago cancelado")
        result.error != null -> showMessage("Error: ${result.error.message}")
    }
}
```

**Tipos:**
- Tarjeta rechazada: Mensaje específico del banco
- Fondos insuficientes: "Fondos insuficientes en tu tarjeta"
- Pago cancelado: "Has cancelado el pago"
- Error de red: "Verifica tu conexión e intenta nuevamente"

#### **6. Errores de eSIM**

**Estados:**
- Instalación fallida: "Error al instalar eSIM, contacta soporte"
- Código QR inválido: "Código QR corrupto, solicita nuevo"
- eSIM expirada: "Tu plan ha expirado"

### Mensajes Claros y Contextuales

Todos los mensajes de error:
- ✅ Son específicos y contextuales
- ✅ Sugieren acciones a tomar
- ✅ Están en el idioma del usuario (i18n)
- ✅ No exponen detalles técnicos al usuario
- ✅ Se registran en logs para debugging

---

## 🌐 Internacionalización (i18n)

### Idiomas Soportados
- 🇬🇧 **Inglés** (default - values/)
- 🇪🇸 **Español** (values-es/)
- 🇩🇪 **Alemán** (values-de/)

### Implementación

**Cero Hardcoded Strings** ✅
```kotlin
// ❌ MAL
textView.text = "Welcome to Pangea"

// ✅ CORRECTO
textView.text = getString(R.string.welcome_message)
```

**Archivos strings.xml:**
- `strings.xml` (inglés - default)
- `strings-es.xml` (español)
- `strings-de.xml` (alemán)

**Recursos Localizados:**
- Textos de UI (labels, botones, mensajes)
- Mensajes de error
- Validaciones
- Splash screen slogan
- Nombres de pantallas

**Nombres Descriptivos:**
```xml
<!-- values/strings.xml -->
<string name="auth_email_hint">Email</string>
<string name="auth_password_hint">Password</string>
<string name="error_invalid_email">Invalid email format</string>
<string name="packages_filter_data_only">Data Only</string>
```

---

## 🔧 Tecnologías y Dependencias

### Core
```gradle
// Kotlin
kotlin = "2.0.21"
kotlinx-coroutines = "1.7.3"

// AndroidX
androidx-core-ktx = "1.15.0"
androidx-appcompat = "1.7.0"
androidx-lifecycle-runtime-ktx = "2.8.7"
androidx-activity = "1.9.3"
```

### Arquitectura
```gradle
// Hilt (Dependency Injection)
hilt-android = "2.51.1"
hilt-compiler = "2.51.1"

// Room (Database)
androidx-room-runtime = "2.6.1"
androidx-room-ktx = "2.6.1"
androidx-room-compiler = "2.6.1"

// Navigation
androidx-navigation-fragment-ktx = "2.8.4"
androidx-navigation-ui-ktx = "2.8.4"

// ViewModel & Flow
androidx-lifecycle-viewmodel-ktx = "2.8.7"
androidx-lifecycle-runtime-compose = "2.8.7"
```

### Networking
```gradle
// Retrofit
retrofit = "2.9.0"
retrofit-converter-gson = "2.9.0"
okhttp-logging-interceptor = "4.12.0"

// Gson
gson = "2.10.1"
```

### UI
```gradle
// Compose
compose-bom = "2024.11.00"
compose-ui = "1.7.5"
compose-material3 = "1.3.1"
activity-compose = "1.9.3"

// Material Design
material = "1.12.0"

// Image Loading
coil = "2.5.0"

// SwipeRefreshLayout
swiperefreshlayout = "1.1.0"
```

### Security
```gradle
// Tink (Encryption)
tink-android = "1.15.0"

// Security Crypto (Legacy)
androidx-security-crypto = "1.1.0-alpha06"
```

### Payments
```gradle
// Stripe
stripe-android = "20.49.0"
```

### Firebase
```gradle
// Firebase
firebase-bom = "32.7.4"
firebase-analytics-ktx
```

### Testing
```gradle
// Unit Testing
junit = "4.13.2"

// Android Testing
androidx-junit = "1.2.1"
androidx-espresso-core = "3.6.1"
```

---

## 📂 Estructura del Proyecto

```
app/
├── src/main/
│   ├── java/com/example/pangeaapp/
│   │   ├── core/
│   │   │   ├── di/                      # Hilt Modules
│   │   │   │   ├── AuthModule.kt
│   │   │   │   ├── DatabaseModule.kt
│   │   │   │   ├── NetworkModule.kt
│   │   │   │   ├── RepositoryModule.kt
│   │   │   │   └── SecurityModule.kt
│   │   │   ├── network/
│   │   │   │   ├── AuthInterceptor.kt
│   │   │   │   ├── ConnectivityInterceptor.kt
│   │   │   │   └── ConnectivityObserver.kt
│   │   │   └── security/
│   │   │       └── TinkManager.kt       # Encryption
│   │   ├── data/
│   │   │   ├── auth/
│   │   │   │   ├── AuthRepository.kt
│   │   │   │   ├── RealAuthRepository.kt
│   │   │   │   └── SessionManager.kt
│   │   │   ├── esim/
│   │   │   │   ├── ESimsRepository.kt
│   │   │   │   └── RealESimsRepository.kt
│   │   │   ├── local/
│   │   │   │   ├── PangeaDatabase.kt
│   │   │   │   ├── dao/
│   │   │   │   │   ├── CountryDao.kt
│   │   │   │   │   ├── PackageDao.kt
│   │   │   │   │   └── ESimDao.kt
│   │   │   │   └── entities/
│   │   │   │       ├── CountryEntity.kt
│   │   │   │       ├── PackageEntity.kt
│   │   │   │       └── ESimEntity.kt
│   │   │   ├── remote/
│   │   │   │   ├── PangeaApiService.kt
│   │   │   │   └── dto/
│   │   │   │       ├── CountryDto.kt
│   │   │   │       ├── PackageDto.kt
│   │   │   │       ├── ESimDto.kt
│   │   │   │       └── AuthDto.kt
│   │   │   ├── NetworkBoundResource.kt
│   │   │   └── Resource.kt
│   │   ├── ui/
│   │   │   ├── auth/
│   │   │   │   ├── AuthViewModel.kt
│   │   │   │   ├── LoginFragment.kt
│   │   │   │   └── ForgotPasswordDialog.kt
│   │   │   ├── countries/
│   │   │   │   ├── CountriesViewModel.kt
│   │   │   │   ├── CountriesFragment.kt
│   │   │   │   └── CountryAdapter.kt
│   │   │   ├── packages/
│   │   │   │   ├── PackagesViewModel.kt
│   │   │   │   ├── PackagesFragment.kt
│   │   │   │   └── PackageAdapter.kt
│   │   │   ├── esims/
│   │   │   │   ├── ESimsViewModel.kt
│   │   │   │   ├── ESimsFragment.kt
│   │   │   │   ├── ESimAdapter.kt
│   │   │   │   ├── ESimDetailFragment.kt
│   │   │   │   └── ESimDetailViewModel.kt
│   │   │   ├── checkout/
│   │   │   │   ├── CheckoutViewModel.kt
│   │   │   │   └── CheckoutFragment.kt
│   │   │   ├── settings/
│   │   │   │   └── SettingsFragment.kt
│   │   │   └── components/
│   │   │       └── OfflineBanner.kt     # Compose
│   │   ├── SplashActivity.kt
│   │   ├── MainActivity.kt
│   │   └── PangeaApp.kt                 # Application Class
│   └── res/
│       ├── drawable/                     # Logos, icons, shapes
│       │   ├── logo_header.png          # Light mode logo
│       │   └── search_field_background.xml
│       ├── drawable-night/               # Dark mode assets
│       │   └── logo_header.png          # Dark mode logo
│       ├── layout/                       # XML layouts
│       │   ├── activity_main.xml
│       │   ├── activity_splash.xml
│       │   ├── fragment_countries.xml
│       │   ├── fragment_packages.xml
│       │   └── ...
│       ├── navigation/
│       │   └── nav_graph.xml            # Navigation graph
│       ├── values/
│       │   ├── colors.xml
│       │   ├── strings.xml              # English (default)
│       │   └── themes.xml
│       ├── values-es/
│       │   └── strings.xml              # Spanish
│       ├── values-de/
│       │   └── strings.xml              # German
│       ├── values-night/                 # Dark theme colors
│       │   └── colors.xml
│       └── xml/
│           ├── backup_rules.xml         # Exclude sensitive data
│           └── data_extraction_rules.xml
└── build.gradle.kts
```

---

## 🚀 Instalación y Configuración

### Prerrequisitos
- Android Studio Ladybug | 2024.2.1 o superior
- JDK 11 o superior
- Android SDK API 36 (compileSdk)
- Dispositivo/Emulador con API 25+ (minSdk 25)

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/larisa-eucledian/PangeaAppAndroid.git
cd PangeaAppAndroid
```

2. **Configurar API Keys**

Crear archivo `local.properties` en la raíz del proyecto:
```properties
STRIPE_PUBLISHABLE_KEY=pk_test_51QNGDuKxqD1Y3GG3Qksx0H9eEGCmO0tSCNf3Q0pNVP5u11HYKoxSb47qPi2iTRCWVdjuL4KEBa42Wv5RZjlZrfow00XN8pfPIr
TENANT_API_KEY=VsXl6LmtxwvqPztPBTaqDwbT3YB9hcYSBb7qdacmslS
```

3. **Sincronizar dependencias**
```bash
./gradlew build
```

4. **Ejecutar la aplicación**
- Conectar dispositivo Android o iniciar emulador
- Ejecutar desde Android Studio o:
```bash
./gradlew installDebug
```

### Configuración de Firebase (Opcional)
1. Descargar `google-services.json` desde Firebase Console
2. Colocarlo en `app/google-services.json`
3. Firebase Analytics está configurado automáticamente

---

## 💳 Tarjetas de Prueba (Stripe)

Para probar la funcionalidad de pagos, usa las siguientes tarjetas de prueba de Stripe:

### Tarjetas que Funcionan

**Tarjeta de Prueba Principal:**
```
Número: 4242 4242 4242 4242
Fecha: Cualquier fecha futura (ej: 12/34)
CVC: Cualquier 3 dígitos (ej: 123)
```

**Otras Tarjetas de Prueba:**
```
Visa:           4000 0566 5566 5556
Mastercard:     5555 5555 5555 4444
American Express: 3782 822463 10005
```

### Tarjetas que Fallan (para probar errores)

```
Tarjeta Rechazada:        4000 0000 0000 0002
Fondos Insuficientes:     4000 0000 0000 9995
CVC Inválido:             4000 0000 0000 0127
Tarjeta Expirada:         4000 0000 0000 0069
```

---

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Pruebas Manuales Recomendadas

1. **Autenticación**
   - Login con credenciales válidas/inválidas
   - Registro de nuevo usuario
   - Recuperación de contraseña
   - Cierre de sesión

2. **Exploración**
   - Búsqueda de países
   - Navegación entre pantallas
   - Filtrado de paquetes
   - Modo offline (airplane mode)

3. **Compra de eSIM**
   - Selección de paquete
   - Checkout con Stripe (usa tarjetas de prueba)
   - Visualización de código QR
   - Instalación de eSIM

4. **Temas y Localización**
   - Cambio entre dark/light mode
   - Verificar logos adaptativos
   - Cambio de idioma del sistema
   - Verificar ausencia de hardcoded strings

---

**Desarrollado con ❤️ usando Kotlin y Android Jetpack**

*Última actualización: 15 Diciembre 2024*
