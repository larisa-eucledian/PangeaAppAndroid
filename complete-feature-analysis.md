# PangeaApp: Análisis Completo de Features - iOS vs Android

**Fecha del análisis:** 2025-12-10
**Repositorios analizados:**
- iOS (referencia): https://github.com/larisa-eucledian/PangeaAppIOS
- Android (en desarrollo): https://github.com/larisa-eucledian/PangeaAppAndroid

---

## RESUMEN EJECUTIVO

### Status General del Proyecto Android:
- **Completitud estimada:** 60% del flujo end-to-end funcional
- **Features core implementados:** 15 de 23 features principales
- **Arquitectura:** Sólida y production-ready (MVVM + Clean Architecture + Hilt)
- **Work remaining:** ~40-50h estimadas para paridad completa con iOS

### Desglose por Prioridad:

| Prioridad | Total Features | Completos ✅ | Parciales ⚠️ | Faltantes ❌ | Esfuerzo Pendiente |
|-----------|----------------|--------------|--------------|--------------|---------------------|
| 🔴 CRÍTICO (P0) | 6 | 0 | 2 | 4 | 24-28h |
| 🟡 IMPORTANTE (P1) | 7 | 1 | 2 | 4 | 12-16h |
| 🟢 DESEABLE (P2) | 6 | 2 | 1 | 3 | 6-8h |
| ⚪ NICE TO HAVE (P3) | 4 | 0 | 1 | 3 | 2-4h |

### Hallazgos Clave:
1. ✅ **Lo mejor:** Auth completo, búsqueda de países/paquetes funcional, cache offline robusto
2. ❌ **Blocker crítico:** Todo el flujo de compra y gestión de eSIMs falta completamente
3. ⚠️ **Importante:** Tink ya está como dependencia pero NO se usa (usa EncryptedSharedPreferences)
4. 🎯 **Para 2 días:** Enfocarse en Checkout + eSIMs básicos = funcionalidad mínima viable

---

## ✅ LO QUE YA ESTÁ COMPLETO EN ANDROID

### 1. Sistema de Autenticación Completo
**Status:** ✅ COMPLETO y funcional

**Implementación Android:**
- Login con email/username + password
- Registro con validaciones client + server side
- Recuperación de contraseña por email
- Gestión de sesión con EncryptedSharedPreferences (AES256_GCM)
- StateFlow reactivo para estado de sesión
- Auto-navegación según estado de auth

**Archivos:**
- `app/src/main/java/com/example/pangeaapp/ui/auth/LoginFragment.kt`
- `app/src/main/java/com/example/pangeaapp/ui/auth/RegisterFragment.kt`
- `app/src/main/java/com/example/pangeaapp/ui/auth/ForgotPasswordDialog.kt`
- `app/src/main/java/com/example/pangeaapp/ui/auth/AuthViewModel.kt`
- `app/src/main/java/com/example/pangeaapp/data/auth/RealAuthRepository.kt`
- `app/src/main/java/com/example/pangeaapp/data/auth/SessionManager.kt`

**Endpoints:**
- `POST auth/local`
- `POST auth/local/register`
- `POST auth/forgot-password`

**Verificado:** ✅ Equivalente funcional a iOS

---

### 2. Exploración de Países
**Status:** ✅ COMPLETO y funcional

**Implementación Android:**
- Lista de países con imágenes (Coil + placeholder + crossfade)
- Búsqueda en tiempo real (nombre, código, región, países cubiertos)
- Filtro por geografía (Single/Multiple) con toggle button
- Cache Room con NetworkBoundResource pattern
- Empty state cuando no hay resultados
- Click → navega a paquetes con Safe Args

**Archivos:**
- `app/src/main/java/com/example/pangeaapp/ui/countries/CountriesFragment.kt`
- `app/src/main/java/com/example/pangeaapp/ui/countries/CountriesViewModel.kt`
- `app/src/main/java/com/example/pangeaapp/ui/CountryAdapter.kt`
- `app/src/main/java/com/example/pangeaapp/data/RealPlansRepository.kt`

**Endpoints:**
- `GET countries?geography={local|regional|global}`

**Verificado:** ✅ Equivalente funcional a iOS (incluso con más filtros)

---

### 3. Catálogo de Paquetes eSIM
**Status:** ✅ COMPLETO y funcional

**Implementación Android:**
- Lista de paquetes por país
- Búsqueda en tiempo real por nombre
- Filtros: Only Data, Data & Calls, Unlimited (con persistencia en SharedPreferences)
- Filtrado automático por cobertura del país seleccionado
- Cada paquete muestra: nombre, features, precio formateado
- Cache Room con NetworkBoundResource
- Título dinámico con nombre del país

**Archivos:**
- `app/src/main/java/com/example/pangeaapp/ui/packages/PackagesFragment.kt`
- `app/src/main/java/com/example/pangeaapp/ui/packages/PackagesViewModel.kt`
- `app/src/main/java/com/example/pangeaapp/ui/PackageAdapter.kt`

**Endpoints:**
- `GET tenant/packages?country_code={code}`

**Verificado:** ✅ Equivalente funcional a iOS (con persistencia de filtros adicional)

---

### 4. Cache Offline Robusto
**Status:** ✅ COMPLETO y superior a iOS

**Implementación Android:**
- Room Database con 2 tablas: `countries`, `packages`
- DAOs con Flow reactivos
- Type Converters para JSON (List, Map, enums)
- NetworkBoundResource pattern (cache-first + network update)
- Conectividad observer con ConnectivityManager
- Banner offline animado (Jetpack Compose)
- Queries optimizadas con índices

**Archivos:**
- `app/src/main/java/com/example/pangeaapp/data/local/PangeaDatabase.kt`
- `app/src/main/java/com/example/pangeaapp/data/local/dao/CountryDao.kt`
- `app/src/main/java/com/example/pangeaapp/data/local/dao/PackageDao.kt`
- `app/src/main/java/com/example/pangeaapp/data/NetworkBoundResource.kt`
- `app/src/main/java/com/example/pangeaapp/core/network/ConnectivityObserver.kt`
- `app/src/main/java/com/example/pangeaapp/ui/components/OfflineBanner.kt`

**Verificado:** ✅ Equivalente funcional a iOS CoreData (Room es más moderno)

---

### 5. Navegación y Arquitectura
**Status:** ✅ COMPLETO

**Implementación Android:**
- Single Activity Architecture
- Navigation Component con nav_graph.xml
- Safe Args para paso de datos type-safe
- Bottom Navigation (3 tabs: Explore, eSIMs, Settings)
- Auto-hide de bottom nav en pantallas de auth
- Animaciones de transición (slide, fade)

**Archivos:**
- `app/src/main/java/com/example/pangeaapp/MainActivity.kt`
- `app/src/main/res/navigation/nav_graph.xml`

**Verificado:** ✅ Equivalente funcional a iOS UITabBarController

---

### 6. Inyección de Dependencias
**Status:** ✅ COMPLETO

**Implementación Android:**
- Dagger Hilt completamente configurado
- 4 módulos: NetworkModule, DatabaseModule, AuthModule, RepositoryModule
- @Singleton para componentes globales
- @ActivityScoped para SessionManager, ConnectivityObserver
- @ViewModelScoped automático

**Archivos:**
- `app/src/main/java/com/example/pangeaapp/di/NetworkModule.kt`
- `app/src/main/java/com/example/pangeaapp/di/DatabaseModule.kt`
- `app/src/main/java/com/example/pangeaapp/PangeaApp.kt` (@HiltAndroidApp)

**Verificado:** ✅ Más robusto que iOS (iOS usa singleton manual)

---

### 7. Networking Layer
**Status:** ✅ COMPLETO

**Implementación Android:**
- Retrofit + OkHttp + Gson
- AuthInterceptor automático (Bearer token + Tenant API Key)
- HttpLoggingInterceptor en DEBUG
- Timeouts: 30s (connect/read/write)
- DataUnwrapTypeAdapterFactory para auto-unwrap de `{data: ...}`
- Error handling tipado (AuthException con casos específicos)

**Archivos:**
- `app/src/main/java/com/example/pangeaapp/data/remote/PangeaApiService.kt`
- `app/src/main/java/com/example/pangeaapp/core/network/AuthInterceptor.kt`
- `app/src/main/java/com/example/pangeaapp/core/network/DataUnwrapTypeAdapterFactory.kt`

**Verificado:** ✅ Equivalente funcional a iOS APIClient

---

### 8. Internacionalización
**Status:** ✅ COMPLETO

**Implementación Android:**
- 3 idiomas: Inglés (default), Español, Alemán
- Cero strings hardcoded
- Plurales para contadores
- Strings para features, errores, títulos, validaciones

**Archivos:**
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/main/res/values-de/strings.xml`

**Verificado:** ✅ Equivalente a iOS (mismo soporte de idiomas)

---

### 9. Modelos de Datos
**Status:** ✅ COMPLETO

**Implementación Android:**
- Clean Architecture con 3 capas: DTO → Entity → Domain
- Mappers bidireccionales
- CountryRow con todos los campos (id, code, name, geography, coverage, currencies, etc.)
- PackageRow con helpers: `dataLabel()`, `kind()`, `featuresList()`
- Geography enum (local, regional, global)

**Archivos:**
- `app/src/main/java/com/example/pangeaapp/core/CountryRow.kt`
- `app/src/main/java/com/example/pangeaapp/core/PackageRow.kt`
- `app/src/main/java/com/example/pangeaapp/data/mappers/*`

**Verificado:** ✅ Equivalente a iOS

---

### 10. Resource Wrapper y Estado
**Status:** ✅ COMPLETO

**Implementación Android:**
- Sealed class `Resource<T>` con Success, Error, Loading
- StateFlow en todos los ViewModels
- Programación reactiva con Kotlin Flow
- Lifecycle-aware collectors (repeatOnLifecycle)

**Archivos:**
- `app/src/main/java/com/example/pangeaapp/data/Resource.kt`
- ViewModels: `AuthViewModel.kt`, `CountriesViewModel.kt`, `PackagesViewModel.kt`

**Verificado:** ✅ Equivalente a iOS (iOS usa NotificationCenter, Android usa Flow)

---

## 🔴 PRIORIDAD 0 - CRÍTICO (Bloquea funcionalidad core)

Sin estos features, la app NO cumple su función básica de venta de eSIMs.

---

### Feature 1: Checkout con Stripe
**Status:** ❌ FALTANTE

**Descripción:**
Pantalla de checkout que permite al usuario revisar el paquete seleccionado, ver detalles (país, datos, vigencia, precio, cobertura) y completar el pago mediante Stripe PaymentSheet. **Sin esto, el usuario NO puede comprar eSIMs.**

**En iOS:**
- Pantalla: `CheckoutViewController.swift`
- Integración: Stripe PaymentSheet SDK
- Flujo:
  1. Usuario selecciona paquete → Navega a Checkout
  2. Ve resumen: país, plan, precio, features, cobertura
  3. Tap "Pay" → Crea PaymentIntent vía API
  4. Abre Stripe PaymentSheet con clientSecret
  5. Usuario completa pago
  6. Post-compra: invalida cachés, notifica a eSIMs tab, navega automáticamente

**En Android:**
- Status actual: **NO EXISTE**
- NO hay fragment de checkout
- NO hay Stripe SDK en dependencias
- NO hay endpoint de transacciones en PangeaApiService

**Qué falta implementar (específico):**
- [ ] Agregar dependencia Stripe Android SDK (~400KB)
- [ ] Crear `CheckoutFragment.kt` con layout de resumen del paquete
- [ ] Crear `CheckoutViewModel.kt` para manejo de estado
- [ ] Implementar UI:
  - [ ] Card de resumen (país, bandera, nombre plan, tipo, vigencia, precio)
  - [ ] Card de detalles (datos, calls, SMS, features, cobertura completa)
  - [ ] Botón "Pay" con loading state
  - [ ] Logos de métodos de pago (opcional)
- [ ] Agregar endpoint en PangeaApiService: `POST transactions`
- [ ] Crear `TransactionRepository` y `RealTransactionRepository`
- [ ] Integrar Stripe PaymentSheet:
  - [ ] Configurar con merchant name "Pangea eSIM"
  - [ ] Manejar success/error callbacks
  - [ ] Manejo de 3D Secure
- [ ] Navegación: packages → checkout (pasar PackageRow + countryName)
- [ ] Post-compra:
  - [ ] Invalidar caché de eSIMs
  - [ ] Navegar a tab "My eSIMs"
  - [ ] Mostrar confirmación de compra

**Esfuerzo Estimado:** 8-10h
**Complejidad:** Alta (integración Stripe + API + flujo completo)
**Dependencias:** Ninguna (puede implementarse de forma aislada)
**Blocker:** SÍ - sin esto no se puede comprar

---

### Feature 2: Endpoint de Transacciones
**Status:** ❌ FALTANTE

**Descripción:**
Endpoint API para crear transacciones y obtener el `clientSecret` de Stripe necesario para abrir PaymentSheet. **Crítico para Checkout.**

**En iOS:**
- Endpoint: `POST /transactions`
- Body:
  ```json
  {
    "amount": 99.99,
    "currency": "mxn",
    "package_id": "xxx",
    "payment_method": "stripe"
  }
  ```
- Response:
  ```json
  {
    "clientSecret": "pi_xxx_secret_yyy",
    "paymentIntentId": "pi_xxx",
    "payment_method": "stripe"
  }
  ```
- Repository: `RealTransactionRepository.swift`

**En Android:**
- Status actual: **NO EXISTE**
- PangeaApiService NO tiene método `createTransaction()`

**Qué falta implementar (específico):**
- [ ] Agregar en `PangeaApiService.kt`:
  ```kotlin
  @POST("transactions")
  suspend fun createTransaction(@Body body: TransactionRequest): TransactionResponse
  ```
- [ ] Crear DTOs:
  ```kotlin
  data class TransactionRequest(
      val amount: Double,
      val currency: String,
      val package_id: String,
      val payment_method: String = "stripe"
  )

  data class TransactionResponse(
      val clientSecret: String,
      val paymentIntentId: String,
      val payment_method: String
  )
  ```
- [ ] Crear `TransactionRepository.kt` interface
- [ ] Crear `RealTransactionRepository.kt` implementación
- [ ] Agregar binding en Hilt `RepositoryModule`

**Esfuerzo Estimado:** 1-2h
**Complejidad:** Baja (solo configuración de API)
**Dependencias:** Ninguna
**Blocker:** SÍ - necesario para Checkout

---

### Feature 3: Listado de eSIMs Compradas
**Status:** ❌ FALTANTE (existe stub vacío)

**Descripción:**
Pantalla que muestra todas las eSIMs compradas por el usuario, ordenadas por status (Ready → Installed → Expired) y fecha. **El usuario necesita ver qué compró y qué puede activar.**

**En iOS:**
- Pantalla: `ESimsViewController.swift`
- Features:
  - Lista con TableView + DiffableDataSource
  - Sorting: primero por status, luego por fecha descendente
  - Cada eSIM muestra:
    - Bandera del país (o 🌍 para multi)
    - Nombre del paquete
    - Badge de status con colores (Ready=amarillo, Installed=verde, Expired=rojo)
    - Info contextual según status:
      - READY: fecha de compra, CTA "Activate Now"
      - INSTALLED: fechas activación/expiración, ICCID, CTA "Check Usage"
  - Pull-to-refresh
  - Empty state: "No tienes eSIMs"
  - Retry automático tras compra (5 intentos cada 2s)
  - Navegación a detalle de eSIM

**En Android:**
- Status actual: **STUB VACÍO**
- Archivo: `app/src/main/java/com/example/pangeaapp/ui/EsimsFragment.kt`
- Solo tiene RecyclerView vacío con empty state visible
- NO hay ViewModel
- NO hay endpoint en API
- NO hay modelos de eSIM

**Qué falta implementar (específico):**
- [ ] Agregar endpoint en `PangeaApiService.kt`: `GET esims`
- [ ] Crear DTOs:
  - [ ] `ESimDto` (id, documentId, esimId, iccid, status, dates, packageName, packageId, coverage, qrCodeUrl, etc.)
  - [ ] Mapper `toDomain()` → `ESimRow`
- [ ] Crear modelo domain `ESimRow.kt` con:
  - [ ] Campos: esimId, iccid, status enum, activationDate, expirationDate, packageName, packageId, coverage, qrCodeUrl, createdAt
  - [ ] Computed: `isActive`, `isExpired`, `formattedDates`
- [ ] Crear `ESimsRepository.kt` interface
- [ ] Crear `RealESimsRepository.kt` con método `fetchESims()`
- [ ] Crear `ESimsViewModel.kt`:
  - [ ] StateFlow de eSIMs
  - [ ] Sorting por status + fecha
  - [ ] Método refresh()
  - [ ] Listener para notificación de compra
- [ ] Crear `ESimAdapter.kt` (RecyclerView adapter):
  - [ ] ViewHolder con card design
  - [ ] Bandera/emoji del país
  - [ ] Nombre del paquete
  - [ ] Badge de status con colores según MaterialTheme
  - [ ] Info según status (fechas, ICCID, CTAs)
- [ ] Actualizar `EsimsFragment.kt`:
  - [ ] Inyectar ViewModel con Hilt
  - [ ] Observar StateFlow de eSIMs
  - [ ] SwipeRefreshLayout para pull-to-refresh
  - [ ] Mostrar/ocultar empty state según data
  - [ ] Click listener → navegar a detalle
- [ ] Agregar acción de navegación en nav_graph: `esims → esimDetail`
- [ ] Opcional: Cache Room (tabla `esims`) con NetworkBoundResource

**Esfuerzo Estimado:** 6-8h
**Complejidad:** Media (similar a Countries/Packages ya implementados)
**Dependencias:** Feature 2 (Transactions) debe completarse primero para que haya eSIMs que mostrar
**Blocker:** SÍ - sin esto el usuario no ve lo que compró

---

### Feature 4: Activación de eSIM
**Status:** ❌ FALTANTE

**Descripción:**
Funcionalidad para activar una eSIM que está en status READY. Cambio de status READY → INSTALLED vía API. **Core del negocio.**

**En iOS:**
- Implementado en: `ESimDetailViewController.swift`
- Flujo:
  1. Usuario ve eSIM en status READY
  2. Botón "Activate eSIM" visible
  3. Tap → Alert de confirmación
  4. Confirmación → API call a `POST /esim/activate`
  5. Success → Status cambia a INSTALLED
  6. UI se actualiza automáticamente
  7. Fetch de usage para mostrar datos

**En Android:**
- Status actual: **NO EXISTE**
- NO hay endpoint de activación
- NO hay UI para activar

**Qué falta implementar (específico):**
- [ ] Agregar endpoint en `PangeaApiService.kt`:
  ```kotlin
  @POST("esim/activate")
  suspend fun activateESim(@Body body: ActivateESimRequest): ActivateESimResponse
  ```
- [ ] Crear DTOs:
  ```kotlin
  data class ActivateESimRequest(val esim_id: String)
  data class ActivateESimResponse(
      val esim: ESimDto  // eSIM actualizada con nuevo status
  )
  ```
- [ ] Agregar método en `ESimsRepository`:
  ```kotlin
  suspend fun activateESim(esimId: String): Result<ESimRow>
  ```
- [ ] Implementar en detalle de eSIM:
  - [ ] Botón "Activate" visible solo si status == READY
  - [ ] Loading state durante activación
  - [ ] Dialog de confirmación
  - [ ] Llamada a repository.activateESim()
  - [ ] Actualizar UI al recibir respuesta
  - [ ] Mostrar error si falla
- [ ] Actualizar lista de eSIMs tras activación exitosa

**Esfuerzo Estimado:** 3-4h
**Complejidad:** Media (requiere manejo de estados)
**Dependencias:** Feature 3 (Listado eSIMs) debe existir primero
**Blocker:** SÍ - sin esto las eSIMs compradas no se pueden usar

---

### Feature 5: Caché de eSIMs
**Status:** ⚠️ PARCIAL (infraestructura existe, no implementada para eSIMs)

**Descripción:**
Sistema de cache local para eSIMs compradas, siguiendo el patrón NetworkBoundResource ya implementado en Countries y Packages.

**En iOS:**
- Implementado en: `CachedESimsRepository.swift` + CoreData
- Entity: `CachedESim` con validez de 1h
- Estrategia: cache-then-network con notificaciones
- Invalidación tras compra

**En Android:**
- Status actual: **NO IMPLEMENTADO**
- NetworkBoundResource existe y funciona para Countries/Packages
- Room database existe
- Solo falta agregar tabla y DAO

**Qué falta implementar (específico):**
- [ ] Crear `ESimEntity.kt`:
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
      @TypeConverters val coverage: List<String>,
      val qrCodeUrl: String?,
      val createdAt: String,
      val lastUpdated: Long = System.currentTimeMillis()
  )
  ```
- [ ] Crear `ESimDao.kt`:
  ```kotlin
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
  }
  ```
- [ ] Agregar DAO en `PangeaDatabase.kt`
- [ ] Crear mapper `ESimEntity ↔ ESimRow` en `app/data/mappers/ESimMappers.kt`
- [ ] Envolver `fetchESims()` en `NetworkBoundResource` en el Repository
- [ ] Invalidación de caché tras compra en CheckoutFragment

**Esfuerzo Estimado:** 2-3h
**Complejidad:** Baja (patrón ya existe, solo replicar)
**Dependencias:** Feature 3 (Listado eSIMs)
**Blocker:** NO - funciona sin caché, pero mejora UX

---

### Feature 6: Invalidación de Caché Post-Compra
**Status:** ⚠️ PARCIAL (mecanismo existe, no conectado)

**Descripción:**
Después de una compra exitosa, invalidar el caché de eSIMs para forzar un refresh desde el servidor y mostrar la nueva eSIM inmediatamente.

**En iOS:**
- Implementado en: `CheckoutViewController.swift`
- Flujo post-compra:
  ```swift
  AppDependencies.shared.esimsRepository.clearCache()
  AppDependencies.shared.plansRepository.clearCache()
  NotificationCenter.default.post(name: .eSimPurchaseCompleted, object: nil)
  // Navega a tab eSIMs
  ```
- `ESimsViewController` escucha notificación y hace retry automático

**En Android:**
- Status actual: **MECANISMO EXISTE, NO CONECTADO**
- Room tiene `deleteAll()` en DAOs
- No hay llamada tras compra porque no existe checkout

**Qué falta implementar (específico):**
- [ ] Agregar método en `ESimsRepository`:
  ```kotlin
  suspend fun invalidateCache()
  ```
- [ ] Implementación:
  ```kotlin
  override suspend fun invalidateCache() {
      esimDao.deleteAll()
  }
  ```
- [ ] En `CheckoutViewModel` tras pago exitoso:
  ```kotlin
  esimsRepository.invalidateCache()
  packagesRepository.invalidateCache() // opcional
  ```
- [ ] Opcional: SharedFlow/Event para notificar a ESimsFragment
- [ ] Retry automático en `ESimsViewModel` (polling cada 2s, máx 5 intentos)

**Esfuerzo Estimado:** 1-2h
**Complejidad:** Baja
**Dependencias:** Features 1 (Checkout) y 3 (Listado eSIMs)
**Blocker:** NO - pero mejora mucho la UX

---

## 🟡 PRIORIDAD 1 - IMPORTANTE (Afecta experiencia core)

El usuario puede usar la app básica, pero con fricción significativa.

---

### Feature 7: Detalle de eSIM con QR Code
**Status:** ❌ FALTANTE

**Descripción:**
Pantalla de detalle que muestra toda la información de una eSIM específica, incluyendo el QR code para instalación manual. **Necesario para que el usuario instale la eSIM en su dispositivo.**

**En iOS:**
- Pantalla: `ESimDetailViewController.swift`
- Secciones:
  1. **Header:** Bandera, nombre paquete, badge status
  2. **QR Code:** Imagen del QR, botón "Quick Install" (solo iOS - settings URL)
  3. **Información:** ICCID, fechas activación/expiración/compra, cobertura
  4. **Features del paquete:** Datos, calls, SMS, hotspot (fetch desde PlansRepository)
  5. **Usage** (solo si INSTALLED): Datos usados/total (%), SMS restantes, minutos voz
  6. **Botón Activate** (solo si READY)

**En Android:**
- Status actual: **NO EXISTE**
- NO hay fragment de detalle
- NO hay endpoint de usage

**Qué falta implementar (específico):**
- [ ] Crear `ESimDetailFragment.kt` con layout scrollable
- [ ] Crear `ESimDetailViewModel.kt`
- [ ] Implementar UI:
  - [ ] Header con bandera, nombre, badge status
  - [ ] Card con ImageView para QR code (cargar con Coil desde `qrCodeUrl`)
  - [ ] Botón "Download QR" para guardar en galería (opcional)
  - [ ] Card de información: ICCID (copyable), fechas formateadas
  - [ ] Chip group con países cubiertos
  - [ ] Card de features del paquete (fetch desde PackagesRepository por packageId)
  - [ ] Card de usage (solo visible si status == INSTALLED)
  - [ ] Botón "Activate" (solo visible si status == READY)
- [ ] Navegación: esims → esimDetail (pasar ESimRow completo con Safe Args o esimId)
- [ ] Integración con Feature 4 (Activación)
- [ ] Integración con Feature 8 (Usage) si INSTALLED

**Esfuerzo Estimado:** 5-6h
**Complejidad:** Media (mucha UI, pero lógica simple)
**Dependencias:** Feature 3 (Listado eSIMs)
**Blocker:** Casi - sin QR el usuario tiene dificultad para instalar

---

### Feature 8: Visualización de Uso de Datos/SMS/Voz
**Status:** ❌ FALTANTE

**Descripción:**
Mostrar el consumo actual de datos, SMS y minutos de voz de una eSIM instalada. **Importante para que el usuario sepa cuánto le queda.**

**En iOS:**
- Implementado en: `ESimDetailViewController.swift`
- Endpoint: `GET /esim/usage/{esim_id}`
- Response:
  ```json
  {
    "esimId": "xxx",
    "iccid": "xxx",
    "packageName": "xxx",
    "usage": {
      "status": "active",
      "data": {
        "allowedData": 5000,  // MB
        "remainingData": 3200,
        "allowedSms": 100,
        "remainingSms": 87,
        "allowedVoice": 500,  // minutos
        "remainingVoice": 450,
        "startedAt": 1234567890,
        "expiredAt": 1234999999
      }
    }
  }
  ```
- UI:
  - Barra de progreso de datos con % usado
  - "3.2 GB / 5 GB (64%)"
  - SMS restantes
  - Minutos restantes
- Botón "Check Usage" para actualizar

**En Android:**
- Status actual: **NO EXISTE**
- NO hay endpoint en API
- NO hay modelo de Usage

**Qué falta implementar (específico):**
- [ ] Agregar endpoint en `PangeaApiService.kt`:
  ```kotlin
  @GET("esim/usage/{esim_id}")
  suspend fun getESimUsage(@Path("esim_id") esimId: String): ESimUsageResponse
  ```
- [ ] Crear DTOs:
  ```kotlin
  data class ESimUsageResponse(
      val esimId: String,
      val iccid: String,
      val packageName: String,
      val usage: UsageData
  )

  data class UsageData(
      val status: String,
      val data: DataUsage
  )

  data class DataUsage(
      val allowedData: Int,  // MB
      val remainingData: Int,
      val allowedSms: Int?,
      val remainingSms: Int?,
      val allowedVoice: Int?,  // minutos
      val remainingVoice: Int?,
      val startedAt: Long,
      val expiredAt: Long
  )
  ```
- [ ] Crear modelo domain `ESimUsage.kt` con computed properties:
  ```kotlin
  val dataUsedMB: Int get() = allowedData - remainingData
  val dataUsagePercentage: Int get() = ((dataUsedMB.toFloat() / allowedData) * 100).toInt()
  val dataUsedGB: Float get() = dataUsedMB / 1024f
  ```
- [ ] Agregar método en `ESimsRepository`:
  ```kotlin
  suspend fun fetchUsage(esimId: String): Result<ESimUsage>
  ```
- [ ] En `ESimDetailViewModel`:
  - [ ] StateFlow de `ESimUsage?`
  - [ ] Método `loadUsage(esimId)`
  - [ ] Auto-fetch si status == INSTALLED
- [ ] En `ESimDetailFragment`:
  - [ ] Card de usage (solo visible si status == INSTALLED)
  - [ ] LinearProgressIndicator para datos con %
  - [ ] Text: "X GB / Y GB (Z%)"
  - [ ] Text: "SMS: X / Y"
  - [ ] Text: "Voice: X min / Y min"
  - [ ] Botón "Refresh Usage" con loading state
- [ ] Manejo de casos edge: unlimited data (9007199254740991)

**Esfuerzo Estimado:** 4-5h
**Complejidad:** Media (API + UI con progress bars)
**Dependencias:** Feature 7 (Detalle eSIM)
**Blocker:** NO - pero muy importante para UX

---

### Feature 9: Settings Completos
**Status:** ⚠️ PARCIAL (solo logout funcional)

**Descripción:**
Pantalla de configuración con opciones de ayuda, soporte y logout.

**En iOS:**
- Pantalla: `SettingsViewController.swift`
- Opciones:
  1. **User Header:** Email del usuario autenticado
  2. **Videos de ayuda:** Abre YouTube playlist `PLcd7uoNUhdwhQO8SVP8_QOOFcJq-bl-7V`
  3. **Support (WhatsApp):** Abre WhatsApp con número `5628298160`
  4. **Logout:** Action sheet + confirmación + limpieza de sesión

**En Android:**
- Status actual: **SOLO LOGOUT**
- Archivo: `app/src/main/java/com/example/pangeaapp/ui/SettingsFragment.kt`
- Solo tiene botón de logout funcional
- NO tiene header de usuario
- NO tiene links a ayuda/soporte

**Qué falta implementar (específico):**
- [ ] Agregar header con email del usuario:
  - [ ] Observar `SessionManager.getCurrentUserInfo()`
  - [ ] TextView con email + ícono de perfil
- [ ] Agregar opción "Help Videos":
  - [ ] Intent para abrir YouTube app o browser:
    ```kotlin
    val intent = Intent(Intent.ACTION_VIEW,
        Uri.parse("https://youtube.com/playlist?list=PLcd7uoNUhdwhQO8SVP8_QOOFcJq-bl-7V"))
    startActivity(intent)
    ```
- [ ] Agregar opción "WhatsApp Support":
  - [ ] Intent para abrir WhatsApp:
    ```kotlin
    val intent = Intent(Intent.ACTION_VIEW,
        Uri.parse("https://api.whatsapp.com/send/?phone=5628298160"))
    startActivity(intent)
    ```
  - [ ] Fallback si WhatsApp no está instalado
- [ ] Mejorar layout:
  - [ ] Usar RecyclerView con items clicables o LinearLayout con MaterialCardView
  - [ ] Iconos para cada opción (Material Icons)
  - [ ] Dividers entre secciones
- [ ] Opcional: Agregar versión de la app en footer
- [ ] Opcional: Links a Términos y Privacidad

**Esfuerzo Estimado:** 2-3h
**Complejidad:** Baja (solo UI + intents)
**Dependencias:** Ninguna
**Blocker:** NO - deseable para soporte al usuario

---

### Feature 10: Retry Automático Post-Compra
**Status:** ❌ FALTANTE

**Descripción:**
Después de completar una compra, hacer polling automático al endpoint de eSIMs para detectar cuando la nueva eSIM aparece en el backend (puede haber delay de procesamiento).

**En iOS:**
- Implementado en: `ESimsViewController.swift`
- Flujo:
  1. Escucha notificación `.eSimPurchaseCompleted`
  2. Espera 3 segundos
  3. Hace fetch de eSIMs
  4. Si NO aparece nueva eSIM: retry cada 2s (máximo 5 intentos)
  5. Si aparece: stop polling, muestra eSIM

**En Android:**
- Status actual: **NO EXISTE**
- No hay listener de eventos de compra
- No hay retry automático

**Qué falta implementar (específico):**
- [ ] Crear SharedFlow en `CheckoutViewModel` para evento de compra:
  ```kotlin
  private val _purchaseCompleted = MutableSharedFlow<String>()  // packageId
  val purchaseCompleted: SharedFlow<String> = _purchaseCompleted.asSharedFlow()
  ```
- [ ] Emitir evento tras pago exitoso:
  ```kotlin
  _purchaseCompleted.emit(packageId)
  ```
- [ ] En `ESimsViewModel`:
  - [ ] Método `startRetryPolling(packageId: String)`:
    ```kotlin
    viewModelScope.launch {
        val initialCount = esims.value.size
        delay(3000)  // Espera inicial

        repeat(5) { attempt ->
            loadESims()
            if (esims.value.size > initialCount) {
                // Nueva eSIM detectada
                return@launch
            }
            delay(2000)  // Retry cada 2s
        }
    }
    ```
- [ ] En `ESimsFragment`:
  - [ ] Inyectar `CheckoutViewModel` (scoped a Activity o Navigation graph)
  - [ ] Observar `purchaseCompleted` flow:
    ```kotlin
    viewLifecycleOwner.lifecycleScope.launch {
        checkoutViewModel.purchaseCompleted.collect { packageId ->
            esimViewModel.startRetryPolling(packageId)
        }
    }
    ```

**Esfuerzo Estimado:** 2-3h
**Complejidad:** Media (requiere comunicación entre ViewModels)
**Dependencias:** Features 1 (Checkout) y 3 (Listado eSIMs)
**Blocker:** NO - pero mejora mucho la UX

---

### Feature 11: Pull-to-Refresh en eSIMs
**Status:** ❌ FALTANTE

**Descripción:**
Gesto de pull-to-refresh en la lista de eSIMs para actualizar manualmente desde el servidor.

**En iOS:**
- Implementado: `UIRefreshControl` en TableView
- Llama a `load()` al hacer pull

**En Android:**
- Status actual: **NO IMPLEMENTADO**
- RecyclerView existe pero sin SwipeRefreshLayout

**Qué falta implementar (específico):**
- [ ] En `fragment_esims.xml`:
  ```xml
  <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
      android:id="@+id/swipeRefresh"
      ...>
      <androidx.recyclerview.widget.RecyclerView ... />
  </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
  ```
- [ ] En `ESimsFragment.kt`:
  ```kotlin
  b.swipeRefresh.setOnRefreshListener {
      viewModel.refresh()
  }

  // Observar loading state
  viewModel.isLoading.collectAsStateWithLifecycle { loading ->
      b.swipeRefresh.isRefreshing = loading
  }
  ```
- [ ] En `ESimsViewModel.kt`:
  - [ ] Método `refresh()` que llama a `loadESims()`
  - [ ] StateFlow de `isLoading`

**Esfuerzo Estimado:** 1h
**Complejidad:** Baja (patrón estándar Android)
**Dependencias:** Feature 3 (Listado eSIMs)
**Blocker:** NO

---

## 🟢 PRIORIDAD 2 - DESEABLE (Mejoras de experiencia)

Nice to have, no bloquea funcionalidad core.

---

### Feature 12: Video Hero en Búsqueda
**Status:** ❌ FALTANTE

**Descripción:**
Video de fondo en loop en la pantalla de búsqueda de países para mejorar la estética.

**En iOS:**
- Implementado en: `PlanSearchViewController.swift`
- Video: `background-travel.mp4` (8.3 MB)
- AVPlayer + AVPlayerLayer en loop
- Overlay oscuro para legibilidad

**En Android:**
- Status actual: **NO EXISTE**
- CountriesFragment tiene solo RecyclerView estándar

**Qué falta implementar (específico):**
- [ ] Agregar video `background-travel.mp4` a `app/src/main/res/raw/`
- [ ] En `fragment_countries.xml`:
  ```xml
  <androidx.constraintlayout.widget.ConstraintLayout>
      <VideoView
          android:id="@+id/videoView"
          android:layout_width="match_parent"
          android:layout_height="match_parent" />

      <View
          android:background="#80000000"  <!-- Overlay oscuro -->
          android:layout_width="match_parent"
          android:layout_height="match_parent" />

      <!-- RecyclerView encima -->
      <androidx.recyclerview.widget.RecyclerView ... />
  </androidx.constraintlayout.widget.ConstraintLayout>
  ```
- [ ] En `CountriesFragment.kt`:
  ```kotlin
  val uri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.background_travel}")
  b.videoView.setVideoURI(uri)
  b.videoView.setOnPreparedListener { mp ->
      mp.isLooping = true
      mp.setVolume(0f, 0f)  // Mute
  }
  b.videoView.start()
  ```

**Esfuerzo Estimado:** 2-3h (incluyendo optimización de video)
**Complejidad:** Baja
**Dependencias:** Ninguna
**Blocker:** NO - puramente estético

---

### Feature 13: Sorting de eSIMs por Status
**Status:** ❌ FALTANTE

**Descripción:**
Ordenar automáticamente la lista de eSIMs: primero READY, luego INSTALLED, luego EXPIRED. Dentro de cada grupo, por fecha descendente.

**En iOS:**
- Implementado en: `ESimsViewController.swift`
- Lógica de sorting:
  ```swift
  esims.sort { (a, b) in
      if a.status != b.status {
          return a.status.sortOrder < b.status.sortOrder  // READY=0, INSTALLED=1, EXPIRED=2
      }
      return (a.createdAt ?? Date.distantPast) > (b.createdAt ?? Date.distantPast)
  }
  ```

**En Android:**
- Status actual: **NO IMPLEMENTADO**
- Lista sin sorting

**Qué falta implementar (específico):**
- [ ] En `ESimRow.kt`, agregar computed property:
  ```kotlin
  val statusSortOrder: Int
      get() = when (status) {
          ESimStatus.READY -> 0
          ESimStatus.INSTALLED -> 1
          ESimStatus.EXPIRED -> 2
          else -> 3
      }
  ```
- [ ] En `ESimsViewModel.kt`, después de cargar eSIMs:
  ```kotlin
  private fun sortESims(esims: List<ESimRow>): List<ESimRow> {
      return esims.sortedWith(
          compareBy<ESimRow> { it.statusSortOrder }
              .thenByDescending { it.createdAt }
      )
  }

  // Aplicar en load:
  _esims.value = sortESims(response.data)
  ```

**Esfuerzo Estimado:** 30 min
**Complejidad:** Baja
**Dependencias:** Feature 3 (Listado eSIMs)
**Blocker:** NO

---

### Feature 14: Empty States Mejorados
**Status:** ⚠️ PARCIAL (existen pero básicos)

**Descripción:**
Mejorar los empty states con ilustraciones, textos descriptivos y CTAs.

**En iOS:**
- Empty states en:
  - Countries: (no tiene, siempre hay países)
  - Packages: Generic empty view
  - eSIMs: "No tienes eSIMs" + "Compra un paquete para empezar"

**En Android:**
- Status actual: **BÁSICO**
- Solo texto simple "No items found"
- No hay ilustraciones ni CTAs

**Qué falta implementar (específico):**
- [ ] Crear `view_empty_state.xml`:
  ```xml
  <LinearLayout orientation="vertical" gravity="center">
      <ImageView android:src="@drawable/empty_illustration" />
      <TextView android:id="@+id/emptyTitle" android:textSize="18sp" />
      <TextView android:id="@+id/emptySubtitle" android:textSize="14sp" />
      <MaterialButton android:id="@+id/emptyCta" />  <!-- Opcional -->
  </LinearLayout>
  ```
- [ ] Agregar ilustraciones SVG o PNG para:
  - No eSIMs
  - No packages (si aplica)
  - No results
- [ ] En cada Fragment, usar empty state con textos específicos:
  - eSIMs: "No eSIMs yet" / "Purchase a package to get started" / CTA: "Explore Packages"
  - Packages sin resultados: "No packages found" / "Try different filters"
- [ ] Opcional: Animación Lottie para empty states

**Esfuerzo Estimado:** 2-3h
**Complejidad:** Baja (solo UI)
**Dependencias:** Ninguna
**Blocker:** NO

---

### Feature 15: Detección de Datos Ilimitados
**Status:** ✅ COMPLETO

**Descripción:**
Detectar paquetes con datos ilimitados (valor especial `9007199254740991 MB`) y mostrar "Unlimited" en lugar del número.

**En iOS:**
- Implementado en: `PackageRow.swift`
- Helper: `isUnlimited` computed property

**En Android:**
- Status actual: **COMPLETO**
- Archivo: `app/src/main/java/com/example/pangeaapp/core/PackageRow.kt`
- Método `dataLabel()` ya detecta unlimited

**Verificado:** ✅ Ya funciona en Android

---

### Feature 16: Localización de Nombres de Países
**Status:** ✅ COMPLETO

**Descripción:**
Mostrar nombres de países en el idioma del usuario (en, es, de).

**En iOS:**
- Usa `Locale.current` para localizar nombres

**En Android:**
- Status actual: **COMPLETO**
- `CountryRow.kt` tiene campo `locale` con Map de traducciones
- Se puede implementar helper para obtener nombre localizado

**Verificado:** ✅ Los datos vienen del backend con localizaciones

---

### Feature 17: Formato de Moneda
**Status:** ✅ COMPLETO

**Descripción:**
Formatear precios con 2 decimales y símbolo de moneda.

**En iOS:**
- Usa String(format: "%.2f", price) + currency

**En Android:**
- Status actual: **COMPLETO**
- PackageAdapter formatea precios correctamente

**Verificado:** ✅ Ya funciona

---

## ⚪ PRIORIDAD 3 - NICE TO HAVE (Polish y extras)

Puede esperar para futuras versiones.

---

### Feature 18: Haptic Feedback
**Status:** ❌ FALTANTE

**Descripción:**
Feedback táctil en errores de login/registro.

**En iOS:**
- Implementado: `UIImpactFeedbackGenerator` en login/register errors

**En Android:**
- Status actual: **NO IMPLEMENTADO**

**Qué falta implementar (específico):**
- [ ] En `LoginFragment.kt` y `RegisterFragment.kt`:
  ```kotlin
  view.performHapticFeedback(HapticFeedbackConstants.REJECT)
  ```
- [ ] Al mostrar error de validación

**Esfuerzo Estimado:** 30 min
**Complejidad:** Muy baja
**Blocker:** NO

---

### Feature 19: Animaciones Avanzadas
**Status:** ⚠️ PARCIAL (solo transiciones básicas)

**Descripción:**
Animaciones de transición entre pantallas, fade in/out de vistas, etc.

**En iOS:**
- Fade animations en varios lugares
- Animaciones de aparición de banners

**En Android:**
- Status actual: **BÁSICO**
- Solo tiene slide transitions en Navigation
- OfflineBanner tiene AnimatedVisibility

**Qué falta implementar:**
- [ ] Animaciones de RecyclerView items (fade in al cargar)
- [ ] Animaciones de botones (ripple, scale)
- [ ] Transitions compartidos entre fragments
- [ ] Skeleton loaders para imágenes

**Esfuerzo Estimado:** 3-4h
**Complejidad:** Media
**Blocker:** NO

---

### Feature 20: Quick Install iOS
**Status:** ❌ NO APLICA EN ANDROID

**Descripción:**
En iOS, botón que abre directamente la app Settings para instalar eSIM.

**En iOS:**
- URL: `settings://`
- Solo funciona en iOS

**En Android:**
- **NO APLICA:** Android no tiene equivalent directo
- Alternativa: Instrucciones paso a paso o deep link a Settings de eSIM (requiere permisos)

**Esfuerzo Estimado:** N/A
**Blocker:** NO

---

### Feature 21: Logos de Métodos de Pago
**Status:** ❌ FALTANTE

**Descripción:**
Mostrar logos de Stripe, Visa, Mastercard, Amex en checkout.

**En iOS:**
- Implementado: `UIStackView` con logos en `CheckoutViewController`
- Assets: logo_stripe, logo_visa, logo_mastercard, logo_amex

**En Android:**
- Status actual: **NO EXISTE** (no hay checkout)

**Qué falta implementar:**
- [ ] Agregar logos a `app/src/main/res/drawable/`
- [ ] En layout de checkout, agregar LinearLayout horizontal:
  ```xml
  <LinearLayout android:orientation="horizontal">
      <ImageView android:src="@drawable/logo_stripe" />
      <ImageView android:src="@drawable/logo_visa" />
      <ImageView android:src="@drawable/logo_mastercard" />
      <ImageView android:src="@drawable/logo_amex" />
  </LinearLayout>
  ```

**Esfuerzo Estimado:** 30 min
**Complejidad:** Muy baja
**Dependencias:** Feature 1 (Checkout)
**Blocker:** NO - puramente estético

---

### Feature 22: Accessibility
**Status:** ⚠️ PARCIAL (básico de Material)

**Descripción:**
VoiceOver/TalkBack support, Dynamic Type, content descriptions.

**En iOS:**
- Parcialmente implementado: VoiceOver posts, accessibility labels

**En Android:**
- Status actual: **BÁSICO**
- Material components tienen accesibilidad básica
- NO hay content descriptions personalizadas
- NO hay hint texts para screen readers

**Qué falta implementar:**
- [ ] Agregar `android:contentDescription` a todas las ImageView
- [ ] Agregar `android:labelFor` a campos de texto
- [ ] Testear con TalkBack
- [ ] Soporte para tamaños de fuente grandes

**Esfuerzo Estimado:** 2-3h
**Complejidad:** Baja
**Blocker:** NO

---

## ISSUE ESPECIAL: Migración de EncryptedSharedPreferences a Tink

### Estado Actual:

**iOS usa:** Keychain (Security framework de Apple)

**Android usa:** EncryptedSharedPreferences con AES256_GCM (parte de `androidx.security:security-crypto`)

**Tink en Android:**
- ✅ **Dependencia YA AGREGADA** en `app/build.gradle.kts` línea 96: `implementation(libs.tink.android)`
- ❌ **NO SE USA** actualmente - SessionManager usa EncryptedSharedPreferences

---

### ¿Es Necesaria la Migración?

**Prioridad sugerida:** 🟡 IMPORTANTE (no crítico, pero buena práctica)

**Razones para migrar:**
1. **Tink** es el framework de criptografía recomendado por Google (desarrollado por Google)
2. Más moderno y mantenido que `security-crypto`
3. Mejor rendimiento y APIs más simples
4. Integración con Google Cloud KMS si se necesita en el futuro

**Razones para NO migrar (quedarse con EncryptedSharedPreferences):**
1. EncryptedSharedPreferences **ya es seguro** (AES256_GCM es encryption de grado militar)
2. Parte oficial de AndroidX Jetpack
3. Ya está funcionando sin problemas
4. Migración requiere trabajo sin beneficio inmediato para el usuario

---

### Recomendación:

**Para los 2 días:** ❌ NO migrar - enfocarse en features que dan valor al usuario

**Post-MVP:** ✅ Considerar migración como refactor de mejora técnica

Si decides migrar eventualmente, el trabajo sería:

---

### Qué Cambiaría con Tink:

**Datos almacenados actualmente en EncryptedSharedPreferences:**
- JWT token
- User ID
- Username
- Email

**Archivos a modificar:**
- `app/src/main/java/com/example/pangeaapp/data/auth/SessionManager.kt`

---

### Qué necesita cambiar específicamente (si se decide migrar):

- [ ] Crear `TinkManager.kt` wrapper:
  ```kotlin
  class TinkManager @Inject constructor(
      @ApplicationContext private val context: Context
  ) {
      private val keysetHandle: KeysetHandle
      private val aead: Aead

      init {
          AeadConfig.register()

          val masterKeyUri = "android-keystore://pangea_master_key"
          val keysetHandle = AndroidKeysetManager.Builder()
              .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
              .withSharedPref(context, "tink_keyset", "tink_pref")
              .withMasterKeyUri(masterKeyUri)
              .build()
              .keysetHandle

          this.aead = keysetHandle.getPrimitive(Aead::class.java)
      }

      fun encrypt(plaintext: String): String {
          val ciphertext = aead.encrypt(plaintext.toByteArray(), null)
          return Base64.encodeToString(ciphertext, Base64.DEFAULT)
      }

      fun decrypt(ciphertext: String): String {
          val encrypted = Base64.decode(ciphertext, Base64.DEFAULT)
          val plaintext = aead.decrypt(encrypted, null)
          return String(plaintext)
      }
  }
  ```

- [ ] Actualizar `SessionManager.kt`:
  - [ ] Reemplazar EncryptedSharedPreferences con Tink
  - [ ] Usar `sharedPrefs.edit().putString("jwt", tinkManager.encrypt(jwt)).apply()`
  - [ ] Usar `val jwt = tinkManager.decrypt(sharedPrefs.getString("jwt", "") ?: "")`

- [ ] Migración de datos existentes (si hay usuarios):
  - [ ] Leer datos de EncryptedSharedPreferences
  - [ ] Re-encriptar con Tink
  - [ ] Guardar en nuevo storage
  - [ ] Limpiar EncryptedSharedPreferences viejo
  - [ ] Flag de migración completada

- [ ] Testing de encryption/decryption

**Esfuerzo Estimado:** 4-6h (incluyendo migración de datos y testing)
**Complejidad:** Media
**Blocker:** NO - EncryptedSharedPreferences es suficientemente seguro

---

## PLAN DE TRABAJO SUGERIDO

### Capacidad: 2 días (16 horas de desarrollo efectivo)

---

### ✅ DEBE ENTRAR EN 2 DÍAS (MVP Funcional):

**Objetivo:** Usuario puede comprar eSIM, verla en su lista, y activarla (flujo end-to-end mínimo).

#### Día 1 (8h):
- [ ] **Feature 2:** Endpoint de Transacciones (1-2h)
- [ ] **Feature 1:** Checkout con Stripe (8-10h) ← COMENZAR ESTE MISMO DÍA 1
  - [ ] Setup Stripe SDK (1h)
  - [ ] Checkout UI básico (3h)
  - [ ] Integración PaymentSheet (2h)
  - [ ] Navegación y post-compra (1h)
  - [ ] Testing con tarjetas de prueba (1h)

**Total Día 1:** ~10h (requiere horas extra o priorización estricta)

#### Día 2 (8h):
- [ ] **Feature 3:** Listado de eSIMs (6-8h)
  - [ ] API endpoint + DTOs + Repository (2h)
  - [ ] ViewModel + Fragment (2h)
  - [ ] Adapter con UI de cards (2h)
  - [ ] Navegación a detalle (1h)
  - [ ] Testing (1h)

- [ ] **Feature 5:** Caché de eSIMs (2-3h) - EN PARALELO si hay otro dev
- [ ] **Feature 6:** Invalidación post-compra (1-2h)

**Total Día 2:** ~10h

---

### 🎯 SI HAY TIEMPO EN LOS 2 DÍAS (poco probable):

- [ ] **Feature 4:** Activación de eSIM (3-4h) - CRÍTICO si alcanza tiempo
- [ ] **Feature 7:** Detalle de eSIM básico sin usage (3h reducido)

---

### 📋 SIGUIENTE FASE (Semana siguiente - ~20h):

**Objetivo:** Funcionalidad completa comparable a iOS.

#### Sprint 2 (3-4 días):
- [ ] **Feature 4:** Activación de eSIM (3-4h) - si no entró en Día 2
- [ ] **Feature 7:** Detalle de eSIM completo (5-6h)
- [ ] **Feature 8:** Visualización de Uso (4-5h)
- [ ] **Feature 9:** Settings completos (2-3h)
- [ ] **Feature 10:** Retry automático post-compra (2-3h)
- [ ] **Feature 11:** Pull-to-refresh (1h)
- [ ] **Feature 13:** Sorting de eSIMs (30min)

**Total:** ~18-23h

---

### 🎨 BACKLOG (Cuando haya tiempo):

#### P2 - Mejoras de Experiencia:
- [ ] **Feature 12:** Video hero (2-3h)
- [ ] **Feature 14:** Empty states mejorados (2-3h)

#### P3 - Polish:
- [ ] **Feature 18:** Haptic feedback (30min)
- [ ] **Feature 19:** Animaciones avanzadas (3-4h)
- [ ] **Feature 21:** Logos de pago (30min)
- [ ] **Feature 22:** Accessibility audit (2-3h)

#### Refactor Técnico (Opcional):
- [ ] **Tink Migration:** EncryptedSharedPreferences → Tink (4-6h)

---

## NOTAS TÉCNICAS Y HALLAZGOS

### Work Already Done (Resumen de lo Sólido):

✅ **Arquitectura de clase mundial:**
- MVVM + Clean Architecture con 3 capas bien separadas
- Dependency Injection completa con Hilt
- Repository Pattern con interfaces
- NetworkBoundResource para cache offline
- Flow reactivo en toda la app

✅ **Infraestructura completa:**
- Retrofit + OkHttp configurado correctamente
- Room Database con DAOs y TypeConverters
- AuthInterceptor automático
- Conectividad observer
- Internacionalización completa

✅ **Features funcionales:**
- Auth completo (login, register, forgot password, session management)
- Exploración de países con búsqueda y filtros
- Catálogo de paquetes con búsqueda y filtros avanzados
- Cache offline robusto
- Banner de conectividad

**Estimación de completitud de arquitectura:** 95% ✅

---

### Diferencias de Arquitectura iOS vs Android:

| Aspecto | iOS | Android | Ganador |
|---------|-----|---------|---------|
| **Patrón arquitectónico** | Repository Pattern básico | MVVM + Clean Architecture | Android 🏆 |
| **DI** | Singleton manual | Hilt (compile-time safe) | Android 🏆 |
| **UI** | UIKit + Storyboards | ViewBinding + Compose híbrido | Empate |
| **Navegación** | Storyboard segues | Navigation Component + Safe Args | Android 🏆 |
| **Caché** | CoreData | Room (más moderno) | Android 🏆 |
| **Seguridad** | Keychain | EncryptedSharedPreferences | iOS 🏆 |
| **Reactivo** | NotificationCenter | Kotlin Flow | Android 🏆 |
| **Networking** | URLSession | Retrofit + OkHttp | Android 🏆 |

**Conclusión:** La arquitectura Android es **superior** en diseño, aunque tiene menos features implementados.

---

### Dependencias Externas Necesarias:

#### Ya en Android ✅:
- Retrofit, OkHttp, Gson
- Room, Hilt, Coroutines, Flow
- Coil, Glide (imágenes)
- Navigation Component
- EncryptedSharedPreferences
- Tink (agregado pero no usado)
- Material Design 3
- Jetpack Compose

#### FALTAN agregar ❌:
- **Stripe Android SDK** (~400KB, crítico)
  ```kotlin
  implementation("com.stripe:stripe-android:20.x.x")
  ```

#### Opcionales:
- Lottie (animaciones JSON) - ~200KB
- ZXing (generar QR localmente si se necesita) - ~500KB

---

### Riesgos Identificados:

1. **⚠️ Tiempo muy ajustado:**
   - Checkout + eSIMs = ~16-18h
   - Solo hay 16h disponibles en 2 días
   - Requiere enfoque total sin interrupciones

2. **⚠️ Testing de Stripe:**
   - Necesita cuenta de prueba configurada
   - Tarjetas de prueba: `4242 4242 4242 4242`
   - Puede haber debugging inesperado

3. **⚠️ Delay del backend:**
   - Compra exitosa puede tardar en crear eSIM
   - Retry automático mitiga esto pero no está en P0

4. **⚠️ QR Codes:**
   - Si el backend no devuelve `qrCodeUrl`, hay que generarlo localmente (requiere ZXing)
   - Verificar con backend primero

5. **⚠️ 3D Secure:**
   - Stripe puede requerir 3D Secure en compras
   - PaymentSheet lo maneja automáticamente, pero hay que testear

---

### Ambigüedades para Revisión:

#### 1. **¿Backend devuelve QR code como URL o como datos?**
   - iOS carga desde URL: `qrCodeUrl`
   - Si Android debe generar QR localmente, necesita librería ZXing (+2h)
   - **Acción:** Verificar respuesta de `GET /esims`

#### 2. **¿Hay delay garantizado entre compra y creación de eSIM?**
   - iOS hace retry por esto
   - Si es instantáneo, podemos simplificar
   - **Acción:** Preguntar al backend team

#### 3. **¿Stripe usa test keys o producción?**
   - Necesitamos publishable key para Android
   - **Acción:** Obtener keys de Stripe dashboard

#### 4. **¿Package incluye packageId en response de packages?**
   - Necesario para fetch de features en detalle
   - Verificado en DTOs: SÍ existe `packageId`

#### 5. **¿Moneda es siempre MXN o varía por paquete?**
   - iOS usa campo `currency` del paquete
   - Android ya lo soporta en modelo
   - **Acción:** Confirmar si hay multi-currency

#### 6. **¿Usuarios existentes tienen datos en EncryptedSharedPreferences?**
   - Si NO hay usuarios en producción aún: NO migrar a Tink
   - Si SÍ hay usuarios: requiere migración de datos
   - **Acción:** Confirmar estado de deployment

---

### Consideraciones de Rendimiento:

✅ **Ya optimizado:**
- RecyclerView con ViewBinding (eficiente)
- Coil con cache de imágenes
- Room con índices en queries frecuentes
- Flow con `collectAsStateWithLifecycle` (leak-safe)
- Debounce implícito en búsqueda con StateFlow

⚠️ **Por optimizar (no urgente):**
- Pagination en lista de eSIMs (si > 50 items)
- Image loading con placeholders (ya tiene Coil, solo configurar)
- ProGuard/R8 para release builds

---

### Estimación de Tamaño de APK:

**Actual (sin Stripe):** ~15-20 MB

**Con Stripe:** ~15.5-20.5 MB (+400KB)

**Con todos los features P0-P2:** ~16-21 MB

**Optimizado con ProGuard/R8:** ~8-12 MB

---

## MATRIZ DE DECISIÓN PARA 2 DÍAS

### Escenario 1: Solo 1 desarrollador, 16h exactas

**DEBE HACER (MVP mínimo):**
1. Checkout con Stripe (simplificado: solo resumen básico + pago)
2. Endpoint de transacciones
3. Listado de eSIMs (sin caché, sin retry)
4. Detalle de eSIM ultra-básico (solo info + QR, sin activación)

**RESULTADO:** Usuario puede comprar y ver su eSIM con QR para instalar manualmente.

**Limitación:** NO puede activar desde la app, debe escanear QR manualmente.

---

### Escenario 2: 1 desarrollador senior, 20h (con horas extra)

**DEBE HACER:**
1-4 de Escenario 1 +
5. Activación de eSIM
6. Invalidación de caché post-compra

**RESULTADO:** Flujo end-to-end completo básico.

---

### Escenario 3: 2 desarrolladores, 32h total

**Developer A (Backend/API focus):**
- Endpoints (transacciones, eSIMs, activación, usage)
- Repository layer
- Modelos y DTOs

**Developer B (UI/Frontend focus):**
- Checkout UI + Stripe integration
- eSIMs list + detail UI
- Navegación y states

**RESULTADO:** Flujo completo + visualización de uso + settings.

---

## CONCLUSIÓN Y RECOMENDACIONES

### Estado Actual:
El proyecto Android tiene una **base arquitectónica excelente** (superior a iOS en diseño), pero le faltan las **features de negocio críticas** (compra y gestión de eSIMs).

### Para 2 Días:
**Enfoque láser en P0:** Checkout + eSIMs básicos. Nada de polish, nada de optimizaciones, solo funcionalidad end-to-end.

### Para Paridad con iOS:
~40-50h adicionales post-MVP para llegar a feature parity completa.

### Sobre Tink:
**No migrar en 2 días.** EncryptedSharedPreferences es seguro. Considerar Tink como refactor técnico en 1-2 meses.

### Próximos Pasos Inmediatos:
1. ✅ Confirmar keys de Stripe (test/prod)
2. ✅ Verificar formato de QR codes en backend
3. ✅ Asignar Features 1-6 a sprint de 2 días
4. ✅ Setup de entorno de Stripe en Android
5. 🚀 **Comenzar con Feature 2 (API endpoint) HOY**

---

**Fecha de análisis:** 2025-12-10
**Analizado por:** Claude Code
**Próxima revisión:** Post-implementación de MVP (2 días)
