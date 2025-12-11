# Plan de Implementación - PangeaApp Android
**Fecha:** 2025-12-10
**Branch de trabajo:** `claude/compare-ios-android-features-01YaTQgDFDcCKYDDKAQCszXa`
**Última actualización:** 2025-12-10

---

## ✅ COMPLETADO EN ESTA SESIÓN

### Configuración y Setup
- ✅ Firebase Analytics integrado y configurado
- ✅ Stripe SDK 20.49.0 agregado
- ✅ Keys sensibles movidas a local.properties (seguridad)
- ✅ TENANT_API_KEY y STRIPE_PUBLISHABLE_KEY en BuildConfig

### Settings
- ✅ Botón YouTube con video correcto
- ✅ Botón WhatsApp support
- ✅ Todo localizado en 3 idiomas (EN, ES-MX, DE)

### Checkout Flow (COMPLETO)
- ✅ CheckoutViewModel con manejo de estados de pago
- ✅ CheckoutFragment con Stripe PaymentSheet
- ✅ Layout con Material Design 3 (cards con border, no shadow)
- ✅ Navegación desde Packages a Checkout con Safe Args
- ✅ Logos de pago (Stripe, Visa, Mastercard, Amex)
- ✅ Chips de cobertura con bandera + nombre de país
- ✅ Post-purchase navigation a eSIMs screen
- ✅ Pago funcional con tarjetas de prueba Stripe
- ✅ Todo localizado en 3 idiomas
- ✅ Fix de Locale deprecation warning

### Transactions Endpoint
- ✅ TransactionDto (request/response)
- ✅ TransactionRepository
- ✅ RealTransactionRepository con error handling
- ✅ Endpoint POST /transactions agregado a API service

### Packages Screen (COMPLETO - Validado)
- ✅ Loading indicator en PackagesFragment
- ✅ Filter pre-selection fix (siempre inicia en "show all")
- ✅ API parameter fix (country_code → country)
- ✅ Room query fix (search by countryName)
- ✅ Cache clearing fix (deleteAll antes de insertAll)
- ✅ Packages loading correctamente para:
  - ✅ Países locales (Francia, Suiza, México)
  - ✅ Países regionales (Balkans, Europe, Asia)
  - ✅ Países globales (África, Global)
- ✅ Debug logs agregados (pendiente remover después de validación completa)

### UX Improvements
- ✅ Empty state mejorado en eSIMs screen
- ✅ Mensaje de éxito de pago localizado

---

## 🚀 PRÓXIMOS PASOS PRIORITARIOS

### Pendiente Inmediato:
1. **🔴 P0: eSIMs Screen** - Mostrar lista de eSIMs compradas (6-8h)
   - Necesario para completar el flujo de compra
   - Network-first cache strategy
   - Detalles en FASE 2, Task 2.3

2. **🔴 P0: eSIM Detail Screen** - Ver QR code y detalles de activación (3-4h)
   - Mostrar QR code para instalación
   - Información de ICCID, estado, fechas
   - Botón de activación si está READY

3. **🔴 OBLIGATORIO: Migración a Tink** - Requerimiento académico (4-5h)
   - Migrar de EncryptedSharedPreferences a Tink
   - Detalles en FASE 1, Task 1.1

4. **🟡 P1: Video Hero en Countries** - Mejorar UX (2-3h)
   - Background video como iOS
   - Detalles en FASE 1, Task 1.2

5. **🟡 P2: Cleanup** - Remover debug logs (30min)
   - Limpiar logs de PackagesViewModel, RealPlansRepository, NetworkBoundResource
   - Code review final

### Features Completados que Pueden Validarse:
- ✅ Settings completo
- ✅ Firebase Analytics
- ✅ Checkout con Stripe (flujo completo funcional)
- ✅ Packages loading (todos los tipos de geografía)

---

## 🎯 OBJETIVOS ACTUALIZADOS

### Confirmaciones Recibidas:
- ✅ **Moneda:** Siempre MXN
- ✅ **Stripe keys:** Disponibles y listas
- ✅ **QR codes:** Backend devuelve URL (no generar localmente)
- ✅ **Delay eSIM:** Mínimo (pero implementar retry por seguridad)
- ⚠️ **Tink:** OBLIGATORIO (requerimiento académico)
- 🔮 **Firebase:** Preparar para futuro (analytics + push)

### Nuevos Requerimientos:
1. **Migración a Tink** (OBLIGATORIO - no opcional)
2. **Video hero** en pantalla de países Android
3. **Estrategia de caché eSIMs:** Network-first (no cache-first)
4. **Actualizar URLs de YouTube** en ambos proyectos
5. **Preparar Firebase** (dependencies + setup básico)

---

## 📋 PLAN DE TRABAJO REVISADO

### Capacidad: 2 días (16 horas) + Tareas preparatorias

---

## FASE 0: SETUP Y PREPARACIÓN (2-3h)

### Task 0.1: Actualizar URLs de YouTube
**Prioridad:** 🟢 Quick Win
**Esfuerzo:** 15 min

**iOS:**
- [ ] Archivo: `PangeaApp/Features/Settings/SettingsViewController.swift`
- [ ] Cambiar URL de playlist: `[NUEVA_URL_AQUI]`

**Android:**
- [ ] Archivo: `app/src/main/res/values/strings.xml`
- [ ] Agregar string resource: `<string name="youtube_playlist_url">[NUEVA_URL_AQUI]</string>`
- [ ] Actualizar Settings para usar la nueva URL

---

### Task 0.2: Preparar Firebase (Setup Básico)
**Prioridad:** 🟡 Preparación para futuro
**Esfuerzo:** 1-1.5h

**Android:**
- [ ] Agregar Firebase BOM en `app/build.gradle.kts`:
  ```kotlin
  implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
  implementation("com.google.firebase:firebase-analytics-ktx")
  implementation("com.google.firebase:firebase-messaging-ktx")
  ```
- [ ] Agregar plugin: `id("com.google.gms.google-services")`
- [ ] Descargar `google-services.json` de Firebase Console
- [ ] Colocar en `app/google-services.json`
- [ ] Crear `FirebaseService.kt` stub (sin implementar aún):
  ```kotlin
  class FirebaseMessagingService : FirebaseMessagingService() {
      // TODO: Implementar en futuro
  }
  ```
- [ ] NO implementar lógica todavía (solo preparación)

**iOS:**
- [ ] Agregar Firebase SDK via CocoaPods o SPM
- [ ] Descargar `GoogleService-Info.plist`
- [ ] Configurar en AppDelegate
- [ ] Stub básico sin implementación

**Nota:** Esta es preparación. La implementación real de analytics y push queda para después de MVP.

---

### Task 0.3: Agregar Stripe SDK
**Prioridad:** 🔴 CRÍTICO
**Esfuerzo:** 15 min

**Android:**
- [ ] En `app/build.gradle.kts`:
  ```kotlin
  implementation("com.stripe:stripe-android:20.39.0")
  ```
- [ ] Sync Gradle
- [ ] Verificar que compila

---

## FASE 1: MIGRACIONES Y REFACTORS (4-6h)

### Task 1.1: Migración de EncryptedSharedPreferences a Tink
**Prioridad:** 🔴 OBLIGATORIO (requerimiento académico)
**Esfuerzo:** 4-5h
**Archivos:**
- `app/src/main/java/com/example/pangeaapp/data/auth/SessionManager.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/core/security/TinkManager.kt`
- `app/src/main/java/com/example/pangeaapp/di/SecurityModule.kt` (nuevo)

**Implementación:**

**1.1.1 Crear TinkManager (1h)**
```kotlin
package com.example.pangeaapp.core.security

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TinkManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val aead: Aead

    init {
        AeadConfig.register()

        val keysetHandle = AndroidKeysetManager.Builder()
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withSharedPref(context, "pangea_tink_keyset", "tink_prefs")
            .withMasterKeyUri("android-keystore://pangea_master_key")
            .build()
            .keysetHandle

        aead = keysetHandle.getPrimitive(Aead::class.java)
    }

    fun encrypt(plaintext: String): String {
        val ciphertext = aead.encrypt(plaintext.toByteArray(Charsets.UTF_8), null)
        return Base64.encodeToString(ciphertext, Base64.NO_WRAP)
    }

    fun decrypt(ciphertext: String): String {
        val encrypted = Base64.decode(ciphertext, Base64.NO_WRAP)
        val plaintext = aead.decrypt(encrypted, null)
        return String(plaintext, Charsets.UTF_8)
    }
}
```

**1.1.2 Crear SecurityModule para DI (30min)**
```kotlin
package com.example.pangeaapp.di

import android.content.Context
import com.example.pangeaapp.core.security.TinkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideTinkManager(@ApplicationContext context: Context): TinkManager {
        return TinkManager(context)
    }
}
```

**1.1.3 Migrar SessionManager (2h)**
- [ ] Inyectar TinkManager en SessionManager
- [ ] Crear método de migración: `migrateFromEncryptedPrefsToTink()`
- [ ] Leer datos existentes de EncryptedSharedPreferences
- [ ] Re-encriptar con Tink
- [ ] Guardar en SharedPreferences normal (la encriptación es por Tink)
- [ ] Marcar migración como completada (flag)
- [ ] Métodos encrypt/decrypt ahora usan Tink
- [ ] Testing de encriptación/decriptación

**1.1.4 Testing (1h)**
- [ ] Unit tests para TinkManager
- [ ] Test de migración con datos mock
- [ ] Verificar que login/logout funciona después de migración

**Checklist de migración:**
- [ ] TinkManager creado y funcional
- [ ] SecurityModule configurado en Hilt
- [ ] SessionManager actualizado para usar Tink
- [ ] Migración automática al primer uso
- [ ] Tests pasando
- [ ] Documentación de seguridad actualizada

---

### Task 1.2: Video Hero en Pantalla de Países (Android)
**Prioridad:** 🟡 UX Enhancement
**Esfuerzo:** 2-3h
**Archivos:**
- `app/src/main/res/layout/fragment_countries.xml`
- `app/src/main/java/com/example/pangeaapp/ui/countries/CountriesFragment.kt`
- Nuevo video: `app/src/main/res/raw/background_travel.mp4`

**Implementación:**

**1.2.1 Preparar video (30min)**
- [ ] Descargar video de iOS: `/home/user/PangeaAppIOS/PangeaApp/background-travel.mp4`
- [ ] Optimizar para Android (si > 5MB, comprimir con ffmpeg)
- [ ] Copiar a `app/src/main/res/raw/background_travel.mp4`

**1.2.2 Actualizar layout (1h)**
```xml
<!-- fragment_countries.xml -->
<androidx.constraintlayout.widget.ConstraintLayout>

    <!-- Video Background -->
    <VideoView
        android:id="@+id/videoBackground"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Overlay oscuro para legibilidad -->
    <View
        android:id="@+id/videoOverlay"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="#80000000"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Search Bar encima del video -->
    <com.google.android.material.textfield.TextInputLayout ... />

    <!-- Toggle buttons -->
    <LinearLayout ... />

    <!-- RecyclerView con fondo semi-transparente -->
    <androidx.recyclerview.widget.RecyclerView ... />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**1.2.3 Configurar VideoView en Fragment (1h)**
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupVideoBackground()
    // ... resto del código
}

private fun setupVideoBackground() {
    val uri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.background_travel}")

    b.videoBackground.apply {
        setVideoURI(uri)
        setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.setVolume(0f, 0f)  // Mute
            // Ajustar para llenar pantalla
            val videoWidth = mediaPlayer.videoWidth
            val videoHeight = mediaPlayer.videoHeight
            val viewWidth = width
            val viewHeight = height

            val scale = maxOf(
                viewWidth.toFloat() / videoWidth,
                viewHeight.toFloat() / videoHeight
            )

            val scaledWidth = (videoWidth * scale).toInt()
            val scaledHeight = (videoHeight * scale).toInt()

            layoutParams = layoutParams.apply {
                width = scaledWidth
                height = scaledHeight
            }
        }
        start()
    }
}

override fun onResume() {
    super.onResume()
    b.videoBackground.start()
}

override fun onPause() {
    super.onPause()
    b.videoBackground.pause()
}
```

**Checklist:**
- [ ] Video agregado a raw resources
- [ ] Layout actualizado con VideoView + overlay
- [ ] Video se reproduce en loop sin audio
- [ ] RecyclerView visible encima del video
- [ ] Video se pausa/resume con lifecycle
- [ ] Performance aceptable (verificar en dispositivo real)

---

## FASE 2: FEATURES CRÍTICOS P0 (10-12h)

### Task 2.1: Endpoint de Transacciones
**Prioridad:** 🔴 CRÍTICO
**Esfuerzo:** 1-2h

**Archivos:**
- `app/src/main/java/com/example/pangeaapp/data/remote/PangeaApiService.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/data/transaction/TransactionRepository.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/data/transaction/RealTransactionRepository.kt`
- `app/src/main/java/com/example/pangeaapp/di/RepositoryModule.kt`

**Implementación:**

**2.1.1 DTOs (30min)**
```kotlin
// En PangeaApiService.kt o dto/TransactionDto.kt
data class TransactionRequest(
    val amount: Double,
    val currency: String = "MXN",  // Siempre MXN
    val package_id: String,
    val payment_method: String = "stripe"
)

data class TransactionResponse(
    val clientSecret: String,
    val paymentIntentId: String,
    val payment_method: String
)
```

**2.1.2 API Service (15min)**
```kotlin
interface PangeaApiService {
    // ... endpoints existentes

    @POST("transactions")
    suspend fun createTransaction(
        @Body request: TransactionRequest
    ): TransactionResponse
}
```

**2.1.3 Repository (45min)**
```kotlin
interface TransactionRepository {
    suspend fun createStripeTransaction(
        amount: Double,
        packageId: String
    ): Result<TransactionResponse>
}

class RealTransactionRepository @Inject constructor(
    private val apiService: PangeaApiService
) : TransactionRepository {

    override suspend fun createStripeTransaction(
        amount: Double,
        packageId: String
    ): Result<TransactionResponse> = try {
        val request = TransactionRequest(
            amount = amount,
            currency = "MXN",
            package_id = packageId,
            payment_method = "stripe"
        )

        val response = apiService.createTransaction(request)
        Result.success(response)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

**2.1.4 Hilt Binding (15min)**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // ... bindings existentes

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: RealTransactionRepository
    ): TransactionRepository
}
```

---

### Task 2.2: Checkout con Stripe
**Prioridad:** 🔴 CRÍTICO
**Esfuerzo:** 6-8h

**Archivos:**
- Nuevo: `app/src/main/java/com/example/pangeaapp/ui/checkout/CheckoutFragment.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/ui/checkout/CheckoutViewModel.kt`
- Nuevo: `app/src/main/res/layout/fragment_checkout.xml`
- `app/src/main/res/navigation/nav_graph.xml`

**Implementación:**

**2.2.1 Layout XML (2h)**
```xml
<!-- fragment_checkout.xml -->
<ScrollView>
    <LinearLayout orientation="vertical">

        <!-- Header -->
        <TextView
            android:text="@string/checkout_title"
            style="@style/TextAppearance.Material3.HeadlineMedium" />

        <!-- Summary Card -->
        <com.google.android.material.card.MaterialCardView>
            <LinearLayout orientation="vertical">

                <!-- País y bandera -->
                <LinearLayout orientation="horizontal">
                    <TextView android:id="@+id/countryFlag" android:textSize="32sp" />
                    <TextView android:id="@+id/countryName" />
                </LinearLayout>

                <!-- Plan info -->
                <TextView android:id="@+id/planTitle" />
                <TextView android:id="@+id/planSubtitle" />

                <!-- Precio -->
                <TextView
                    android:id="@+id/price"
                    android:textSize="24sp"
                    android:textStyle="bold" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- Details Card -->
        <com.google.android.material.card.MaterialCardView>
            <LinearLayout orientation="vertical">
                <TextView android:text="@string/details" />

                <TextView android:id="@+id/validityInfo" />
                <TextView android:id="@+id/dataInfo" />
                <TextView android:id="@+id/callsInfo" />
                <TextView android:id="@+id/smsInfo" />
                <TextView android:id="@+id/extrasInfo" />

                <!-- Coverage -->
                <TextView android:text="@string/coverage" />
                <com.google.android.material.chip.ChipGroup
                    android:id="@+id/coverageChips" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- Pay Button -->
        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnPay"
            android:text="@string/pay"
            android:enabled="true" />

        <ProgressBar
            android:id="@+id/loadingIndicator"
            android:visibility="gone" />

        <!-- Payment logos (opcional) -->
        <LinearLayout orientation="horizontal">
            <ImageView android:src="@drawable/logo_stripe" />
            <ImageView android:src="@drawable/logo_visa" />
            <ImageView android:src="@drawable/logo_mastercard" />
        </LinearLayout>

    </LinearLayout>
</ScrollView>
```

**2.2.2 ViewModel (2h)**
```kotlin
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val esimsRepository: ESimsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val uiState: StateFlow<CheckoutState> = _uiState.asStateFlow()

    private val _purchaseCompleted = MutableSharedFlow<String>()
    val purchaseCompleted: SharedFlow<String> = _purchaseCompleted.asSharedFlow()

    fun createPaymentIntent(amount: Double, packageId: String) {
        viewModelScope.launch {
            _uiState.value = CheckoutState.Loading

            transactionRepository.createStripeTransaction(amount, packageId)
                .onSuccess { response ->
                    _uiState.value = CheckoutState.PaymentIntentReady(response.clientSecret)
                }
                .onFailure { error ->
                    _uiState.value = CheckoutState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun onPaymentSuccess(packageId: String) {
        viewModelScope.launch {
            // Invalidar caché de eSIMs
            esimsRepository.invalidateCache()

            // Emitir evento de compra completada
            _purchaseCompleted.emit(packageId)

            _uiState.value = CheckoutState.Success
        }
    }

    fun onPaymentFailure(error: String) {
        _uiState.value = CheckoutState.Error(error)
    }
}

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    data class PaymentIntentReady(val clientSecret: String) : CheckoutState()
    object Success : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}
```

**2.2.3 Fragment con Stripe Integration (3h)**
```kotlin
@AndroidEntryPoint
class CheckoutFragment : Fragment() {

    private var _b: FragmentCheckoutBinding? = null
    private val b get() = _b!!

    private val viewModel: CheckoutViewModel by viewModels()
    private val args: CheckoutFragmentArgs by navArgs()

    private lateinit var paymentSheet: PaymentSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar Stripe
        PaymentConfiguration.init(requireContext(), STRIPE_PUBLISHABLE_KEY)

        paymentSheet = PaymentSheet(this) { result ->
            when (result) {
                is PaymentSheetResult.Completed -> {
                    viewModel.onPaymentSuccess(args.packageId)
                    // Navegar a eSIMs tab
                    findNavController().navigate(R.id.esimsFragment)
                }
                is PaymentSheetResult.Canceled -> {
                    Toast.makeText(context, "Payment canceled", Toast.LENGTH_SHORT).show()
                }
                is PaymentSheetResult.Failed -> {
                    viewModel.onPaymentFailure(result.error.message)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentCheckoutBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeState()

        // Crear PaymentIntent automáticamente
        viewModel.createPaymentIntent(args.packagePrice, args.packageId)
    }

    private fun setupUI() {
        // Mostrar info del paquete
        b.countryFlag.text = getFlagEmoji(args.countryCode)
        b.countryName.text = args.countryName
        b.planTitle.text = args.packageName
        b.price.text = getString(R.string.price_mxn, args.packagePrice)

        // ... resto de campos

        b.btnPay.setOnClickListener {
            // PaymentSheet se abrirá cuando tengamos clientSecret
            (viewModel.uiState.value as? CheckoutState.PaymentIntentReady)?.let { state ->
                presentPaymentSheet(state.clientSecret)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is CheckoutState.Loading -> {
                        b.btnPay.isEnabled = false
                        b.loadingIndicator.visibility = View.VISIBLE
                    }
                    is CheckoutState.PaymentIntentReady -> {
                        b.btnPay.isEnabled = true
                        b.loadingIndicator.visibility = View.GONE
                    }
                    is CheckoutState.Success -> {
                        Toast.makeText(context, "Purchase successful!", Toast.LENGTH_SHORT).show()
                    }
                    is CheckoutState.Error -> {
                        b.btnPay.isEnabled = true
                        b.loadingIndicator.visibility = View.GONE
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun presentPaymentSheet(clientSecret: String) {
        val configuration = PaymentSheet.Configuration(
            merchantDisplayName = "Pangea eSIM"
        )

        paymentSheet.presentWithPaymentIntent(clientSecret, configuration)
    }

    private fun getFlagEmoji(countryCode: String): String {
        // Convertir código de país a emoji de bandera
        val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }
}
```

**2.2.4 Navegación (30min)**
```xml
<!-- nav_graph.xml -->
<fragment
    android:id="@+id/checkoutFragment"
    android:name="com.example.pangeaapp.ui.checkout.CheckoutFragment"
    android:label="Checkout">

    <argument
        android:name="packageId"
        app:argType="string" />
    <argument
        android:name="packageName"
        app:argType="string" />
    <argument
        android:name="packagePrice"
        app:argType="float" />
    <argument
        android:name="countryCode"
        app:argType="string" />
    <argument
        android:name="countryName"
        app:argType="string" />
    <!-- Más argumentos según necesites -->
</fragment>

<!-- En packagesFragment -->
<action
    android:id="@+id/action_packages_to_checkout"
    app:destination="@id/checkoutFragment" />
```

**2.2.5 Actualizar PackagesFragment (30min)**
```kotlin
// En PackageAdapter click listener
packageAdapter.onItemClick = { package ->
    val action = PackagesFragmentDirections.actionPackagesToCheckout(
        packageId = package.packageId,
        packageName = package.packageName,
        packagePrice = package.pricePublic.toFloat(),
        countryCode = args.countryCode,
        countryName = args.countryName
    )
    findNavController().navigate(action)
}
```

---

### Task 2.3: Listado de eSIMs con Network-First Cache
**Prioridad:** 🔴 CRÍTICO
**Esfuerzo:** 6-8h

**Archivos:**
- Actualizar: `app/src/main/java/com/example/pangeaapp/ui/EsimsFragment.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/ui/esims/ESimsViewModel.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/ui/esims/ESimAdapter.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/data/esim/ESimsRepository.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/data/esim/RealESimsRepository.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/core/ESimRow.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/data/local/dao/ESimDao.kt`
- Nuevo: `app/src/main/java/com/example/pangeaapp/data/local/entities/ESimEntity.kt`

**Implementación:**

**2.3.1 API + DTOs (1h)**
```kotlin
// En PangeaApiService.kt
@GET("esims")
suspend fun getESims(): List<ESimDto>

@POST("esim/activate")
suspend fun activateESim(@Body request: ActivateESimRequest): ActivateESimResponse

// DTOs
data class ESimDto(
    val id: Int,
    val documentId: String,
    val esimId: String,
    val iccid: String?,
    val status: String,
    val activationDate: String?,
    val expirationDate: String?,
    val packageName: String,
    val packageId: String,
    val coverage: List<String>,
    val qrCodeUrl: String?,
    val lpaCode: String?,
    val smdpAddress: String?,
    val userEmail: String?,
    val paymentIntentId: String?,
    val createdAt: String,
    val updatedAt: String?
)

data class ActivateESimRequest(val esim_id: String)
data class ActivateESimResponse(val esim: ESimDto)
```

**2.3.2 Domain Model (30min)**
```kotlin
data class ESimRow(
    val id: Int,
    val documentId: String,
    val esimId: String,
    val iccid: String?,
    val status: ESimStatus,
    val activationDate: String?,
    val expirationDate: String?,
    val packageName: String,
    val packageId: String,
    val coverage: List<String>,
    val qrCodeUrl: String?,
    val createdAt: String
) {
    val isActive: Boolean
        get() = status == ESimStatus.INSTALLED

    val isExpired: Boolean
        get() = status == ESimStatus.EXPIRED

    val statusSortOrder: Int
        get() = when (status) {
            ESimStatus.READY -> 0
            ESimStatus.INSTALLED -> 1
            ESimStatus.EXPIRED -> 2
            ESimStatus.UNKNOWN -> 3
        }
}

enum class ESimStatus {
    READY,
    INSTALLED,
    EXPIRED,
    UNKNOWN;

    companion object {
        fun fromString(value: String?): ESimStatus {
            return when (value?.uppercase()) {
                "READY", "READY_FOR_ACTIVATION" -> READY
                "INSTALLED", "ACTIVE" -> INSTALLED
                "EXPIRED" -> EXPIRED
                else -> UNKNOWN
            }
        }
    }
}
```

**2.3.3 Room Entity + DAO (1h)**
```kotlin
@Entity(tableName = "esims")
data class ESimEntity(
    @PrimaryKey val id: Int,
    val documentId: String,
    val esimId: String,
    val iccid: String?,
    val status: String,
    val activationDate: String?,
    val expirationDate: String?,
    val packageName: String,
    val packageId: String,
    val coverage: String,  // JSON string
    val qrCodeUrl: String?,
    val createdAt: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Dao
interface ESimDao {
    @Query("SELECT * FROM esims ORDER BY status ASC, createdAt DESC")
    fun getAllESimsFlow(): Flow<List<ESimEntity>>

    @Query("SELECT * FROM esims WHERE esimId = :esimId")
    suspend fun getESimById(esimId: String): ESimEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(esims: List<ESimEntity>)

    @Query("DELETE FROM esims")
    suspend fun deleteAll()

    @Update
    suspend fun update(esim: ESimEntity)
}
```

**2.3.4 Mappers (30min)**
```kotlin
// ESimMappers.kt
fun ESimDto.toEntity(): ESimEntity {
    return ESimEntity(
        id = id,
        documentId = documentId,
        esimId = esimId,
        iccid = iccid,
        status = status,
        activationDate = activationDate,
        expirationDate = expirationDate,
        packageName = packageName,
        packageId = packageId,
        coverage = Gson().toJson(coverage),
        qrCodeUrl = qrCodeUrl,
        createdAt = createdAt
    )
}

fun ESimEntity.toDomain(): ESimRow {
    return ESimRow(
        id = id,
        documentId = documentId,
        esimId = esimId,
        iccid = iccid,
        status = ESimStatus.fromString(status),
        activationDate = activationDate,
        expirationDate = expirationDate,
        packageName = packageName,
        packageId = packageId,
        coverage = Gson().fromJson(coverage, Array<String>::class.java).toList(),
        qrCodeUrl = qrCodeUrl,
        createdAt = createdAt
    )
}
```

**2.3.5 Repository con Network-First (2h)**
```kotlin
interface ESimsRepository {
    fun getESimsFlow(): Flow<Resource<List<ESimRow>>>
    suspend fun activateESim(esimId: String): Result<ESimRow>
    suspend fun invalidateCache()
    suspend fun refresh()
}

class RealESimsRepository @Inject constructor(
    private val apiService: PangeaApiService,
    private val esimDao: ESimDao,
    private val connectivityObserver: ConnectivityObserver
) : ESimsRepository {

    override fun getESimsFlow(): Flow<Resource<List<ESimRow>>> = flow {
        // 1. Emitir Loading con datos de caché (si existen)
        val cachedEsims = esimDao.getAllESimsFlow().first().map { it.toDomain() }
        if (cachedEsims.isNotEmpty()) {
            emit(Resource.Loading(cachedEsims))
        } else {
            emit(Resource.Loading(null))
        }

        // 2. NETWORK FIRST: Siempre intentar fetch de red primero
        if (connectivityObserver.isOnline()) {
            try {
                val freshEsims = apiService.getESims()

                // Guardar en caché
                esimDao.deleteAll()
                esimDao.insertAll(freshEsims.map { it.toEntity() })

                // Emitir datos frescos
                emit(Resource.Success(freshEsims.map { it.toEntity().toDomain() }))
            } catch (e: Exception) {
                // Si falla la red, usar caché
                if (cachedEsims.isNotEmpty()) {
                    emit(Resource.Success(cachedEsims))
                } else {
                    emit(Resource.Error(e.message ?: "Unknown error"))
                }
            }
        } else {
            // Offline: usar caché
            if (cachedEsims.isNotEmpty()) {
                emit(Resource.Success(cachedEsims))
            } else {
                emit(Resource.Error("No internet connection"))
            }
        }
    }

    override suspend fun activateESim(esimId: String): Result<ESimRow> = try {
        val request = ActivateESimRequest(esim_id = esimId)
        val response = apiService.activateESim(request)

        // Actualizar caché
        val entity = response.esim.toEntity()
        esimDao.update(entity)

        Result.success(entity.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun invalidateCache() {
        esimDao.deleteAll()
    }

    override suspend fun refresh() {
        // Trigger manual refresh
        // El Flow se actualizará automáticamente
    }
}
```

**2.3.6 ViewModel (1h)**
```kotlin
@HiltViewModel
class ESimsViewModel @Inject constructor(
    private val esimsRepository: ESimsRepository,
    private val checkoutViewModel: CheckoutViewModel  // Para escuchar compras
) : ViewModel() {

    private val _esims = MutableStateFlow<List<ESimRow>>(emptyList())
    val esims: StateFlow<List<ESimRow>> = _esims.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadESims()
        listenForPurchases()
    }

    private fun loadESims() {
        viewModelScope.launch {
            esimsRepository.getESimsFlow().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                        resource.data?.let { _esims.value = sortESims(it) }
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _esims.value = sortESims(resource.data)
                        _error.value = null
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _error.value = resource.message
                    }
                }
            }
        }
    }

    private fun listenForPurchases() {
        viewModelScope.launch {
            checkoutViewModel.purchaseCompleted.collect { packageId ->
                startRetryPolling(packageId)
            }
        }
    }

    private fun startRetryPolling(packageId: String) {
        viewModelScope.launch {
            val initialCount = esims.value.size
            delay(3000)  // Espera inicial

            repeat(5) { attempt ->
                esimsRepository.refresh()
                delay(500)  // Esperar que el Flow se actualice

                if (esims.value.size > initialCount) {
                    // Nueva eSIM detectada
                    return@launch
                }
                delay(2000)  // Retry cada 2s
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            esimsRepository.refresh()
        }
    }

    private fun sortESims(esims: List<ESimRow>): List<ESimRow> {
        return esims.sortedWith(
            compareBy<ESimRow> { it.statusSortOrder }
                .thenByDescending { it.createdAt }
        )
    }
}
```

**2.3.7 Adapter (1.5h)**
```kotlin
class ESimAdapter(
    private val onItemClick: (ESimRow) -> Unit
) : RecyclerView.Adapter<ESimAdapter.ViewHolder>() {

    private var esims = listOf<ESimRow>()

    fun submitList(newList: List<ESimRow>) {
        esims = newList
        notifyDataSetChanged()  // O usar DiffUtil para mejor performance
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEsimBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(esims[position])
    }

    override fun getItemCount() = esims.size

    inner class ViewHolder(private val binding: ItemEsimBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(esims[adapterPosition])
            }
        }

        fun bind(esim: ESimRow) {
            binding.apply {
                // Bandera
                countryFlag.text = if (esim.coverage.size > 1) "🌍" else getFlagEmoji(esim.coverage.firstOrNull() ?: "")

                // Nombre del paquete
                packageName.text = esim.packageName

                // Badge de status
                statusBadge.text = when (esim.status) {
                    ESimStatus.READY -> "Ready for Activation"
                    ESimStatus.INSTALLED -> "Installed"
                    ESimStatus.EXPIRED -> "Expired"
                    else -> "Unknown"
                }

                // Colores según status
                val (bgColor, textColor) = when (esim.status) {
                    ESimStatus.READY -> R.color.status_ready_bg to R.color.status_ready_text
                    ESimStatus.INSTALLED -> R.color.status_installed_bg to R.color.status_installed_text
                    ESimStatus.EXPIRED -> R.color.status_expired_bg to R.color.status_expired_text
                    else -> R.color.status_unknown_bg to R.color.status_unknown_text
                }

                statusBadge.setBackgroundColor(ContextCompat.getColor(root.context, bgColor))
                statusBadge.setTextColor(ContextCompat.getColor(root.context, textColor))

                // Info según status
                when (esim.status) {
                    ESimStatus.INSTALLED -> {
                        infoLayout.visibility = View.VISIBLE
                        infoLabel1.text = "Activated:"
                        infoValue1.text = esim.activationDate ?: "N/A"
                        infoLabel2.text = "Expires:"
                        infoValue2.text = esim.expirationDate ?: "N/A"
                        ctaButton.text = "Check Usage"
                    }
                    ESimStatus.READY -> {
                        infoLayout.visibility = View.VISIBLE
                        infoLabel1.text = "Purchased:"
                        infoValue1.text = esim.createdAt
                        infoLabel2.visibility = View.GONE
                        infoValue2.visibility = View.GONE
                        ctaButton.text = "Activate Now"
                    }
                    else -> {
                        infoLayout.visibility = View.GONE
                    }
                }
            }
        }

        private fun getFlagEmoji(countryCode: String): String {
            if (countryCode.length != 2) return ""
            val firstLetter = Character.codePointAt(countryCode.uppercase(), 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(countryCode.uppercase(), 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        }
    }
}
```

**2.3.8 Fragment (1h)**
```kotlin
@AndroidEntryPoint
class EsimsFragment : Fragment() {
    private var _b: FragmentEsimsBinding? = null
    private val b get() = _b!!

    private val viewModel: ESimsViewModel by viewModels()
    private lateinit var adapter: ESimAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentEsimsBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        observeState()
    }

    private fun setupRecyclerView() {
        adapter = ESimAdapter { esim ->
            // Navegar a detalle
            val action = EsimsFragmentDirections.actionEsimsToEsimDetail(esim.esimId)
            findNavController().navigate(action)
        }

        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        b.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.esims.collect { esims ->
                    if (esims.isEmpty()) {
                        b.empty.visibility = View.VISIBLE
                        b.recycler.visibility = View.GONE
                    } else {
                        b.empty.visibility = View.GONE
                        b.recycler.visibility = View.VISIBLE
                        adapter.submitList(esims)
                    }
                }
            }

            launch {
                viewModel.isLoading.collect { loading ->
                    b.swipeRefresh.isRefreshing = loading
                }
            }

            launch {
                viewModel.error.collect { error ->
                    error?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
```

---

## FASE 3: TESTING Y REFINAMIENTO (2-3h)

### Task 3.1: Testing de Flujo Completo
**Esfuerzo:** 2h

- [ ] Test manual del flujo completo:
  1. Login
  2. Búsqueda de país
  3. Selección de paquete
  4. Checkout con Stripe (tarjeta test: 4242 4242 4242 4242)
  5. Compra exitosa
  6. Navegación automática a eSIMs
  7. Ver nueva eSIM en lista
  8. Retry automático (verificar que aparece)
- [ ] Test de casos edge:
  - [ ] Payment cancelado
  - [ ] Payment fallido
  - [ ] No hay conexión durante checkout
  - [ ] eSIM tarda en aparecer (retry)
- [ ] Test de caché:
  - [ ] Modo avión → ver eSIMs del caché
  - [ ] Modo avión → intentar comprar (debe fallar)
  - [ ] Volver online → refresh automático

### Task 3.2: Code Review y Cleanup
**Esfuerzo:** 1h

- [ ] Remover logs de debug
- [ ] Verificar que NO hay hardcoded strings
- [ ] Verificar que NO hay API keys en código (usar BuildConfig o local.properties)
- [ ] Formatear código
- [ ] Agregar comentarios donde sea necesario
- [ ] Verificar imports no utilizados

---

## 📊 RESUMEN DEL PLAN

### Distribución de Tiempo:

| Fase | Tareas | Esfuerzo | Prioridad |
|------|--------|----------|-----------|
| **Fase 0: Setup** | URLs, Firebase prep, Stripe SDK | 2-3h | 🟡 |
| **Fase 1: Refactors** | Tink, Video hero | 6-8h | 🔴/🟡 |
| **Fase 2: Features P0** | Transactions, Checkout, eSIMs | 10-12h | 🔴 |
| **Fase 3: Testing** | QA + cleanup | 2-3h | 🟡 |
| **TOTAL** |  | **20-26h** |  |

### Plan para 2 días (16h efectivas):

**Opción A - Solo features críticos:**
- Task 0.3: Stripe SDK (15min)
- Task 1.1: Migración Tink (4-5h) ← OBLIGATORIO
- Task 2.1: Endpoint Transactions (1-2h)
- Task 2.2: Checkout Stripe (6-8h)
- **Total: ~12-16h** → Alcanza justo, sin eSIMs

**Opción B - Con horas extra (20h):**
- Todo de Opción A +
- Task 2.3: Listado eSIMs (6-8h)
- **Total: ~18-24h** → MVP completo

**Opción C - Recomendado (priorizar lo que da valor):**
- Day 1 AM: Task 0.3 + Task 2.1 (2h)
- Day 1 PM: Task 2.2 parte 1 - Checkout UI + integration (6h)
- Day 2 AM: Task 2.3 parte 1 - eSIMs básicos (4h)
- Day 2 PM: Task 1.1 - Tink migration (4h)
- **Total: 16h** → MVP funcional + Tink

**Task 0.1, 0.2, 1.2, 3.x** quedan para después del MVP crítico.

---

## 🎯 CRITERIOS DE ÉXITO (DEFINITION OF DONE)

### MVP Mínimo (DEBE funcionar):
- [ ] Usuario puede hacer login
- [ ] Usuario puede buscar países y paquetes
- [ ] Usuario puede comprar paquete con Stripe
- [ ] Usuario ve su eSIM comprada en lista
- [ ] eSIM tiene QR code visible
- [ ] Sesión usa Tink (migración completa)

### Nice to Have (si hay tiempo):
- [ ] Usuario puede activar eSIM desde app
- [ ] Video hero en países
- [ ] Firebase preparado (sin implementar)
- [ ] URLs de YouTube actualizadas

---

## 🚀 NEXT STEPS

1. ✅ **Crear branch nueva:**
   ```bash
   git checkout -b feature/complete-mvp-implementation
   ```

2. ✅ **Confirmar URL de YouTube**
   (Nota: faltó en el mensaje, necesito la URL completa)

3. ✅ **Confirmar estrategia:**
   - ¿Opción A, B o C?
   - ¿Cuántas horas reales disponibles?
   - ¿Hay otro developer disponible?

4. 🚀 **Comenzar implementación**

---

**¿Listo para empezar? ¿Con cuál opción vamos?**
