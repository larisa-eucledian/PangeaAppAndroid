# 🌍 Pangea App - Módulo 7

## ⚠️ Nota Importante para Pruebas

**Para probar la funcionalidad completa de paquetes de datos, es necesario buscar y seleccionar el país "México"**, ya que es el único país que actualmente tiene paquetes cargados en el backend. Los demás países están disponibles para exploración, pero no tienen paquetes asociados aún.

---

## Descripción del Proyecto

**Pangea App** es una aplicación Android que permite a los usuarios explorar planes de eSIM para diferentes países y regiones del mundo. La aplicación incluye autenticación de usuarios, navegación entre países y paquetes de datos, y funcionalidad offline.

## 📱 Funcionalidades Principales

### 🔐 Sistema de Autenticación
- Login y registro de usuarios con validación en tiempo real
- Recuperación de contraseña mediante diálogo modal
- Gestión de sesión persistente usando SharedPreferences
- Validaciones de email, contraseña y confirmación de contraseña

### 🗺️ Exploración de Países
- Listado de países con banderas, regiones y geografía (local/regional)
- Búsqueda en tiempo real por nombre de país
- Indicadores visuales para distinguir países locales y regionales
- Navegación hacia paquetes específicos de cada país

### 📦 Paquetes de Datos
- Catálogo de paquetes eSIM con filtro "Solo Datos"
- Información detallada: precio, duración y cantidad de datos
- Cobertura para planes regionales
- **Actualmente disponible para México** (otros países en el backend pendientes de carga)

### 🎨 Interfaz de Usuario
- Tema oscuro/claro automático según preferencias del sistema
- Banner de conectividad que indica estado offline
- Bottom Navigation para navegación principal

### ⚙️ Configuraciones
- Cerrar sesión

---

## 🏗️ Arquitectura y Conceptos del Módulo 7

### 1. 💾 Persistencia de Datos

#### **Room Database**
Implementación de base de datos local con las siguientes características:

- **Entidades**: `CountryEntity`, `PackageEntity`
- **DAOs**: `CountryDao`, `PackageDao` con operaciones CRUD y queries complejas
- **Converters**: Manejo de tipos complejos (listas, objetos JSON) con Gson

#### **SharedPreferences (SessionManager)**
Gestión de sesión de usuario que almacena:
- Token de autenticación
- Email del usuario
- Estado de sesión

### 2. 🌐 Backend Web

#### **API Service (Retrofit)**
Interfaz para consumir la API REST con endpoints para:
- Obtener listado de países
- Obtener paquetes filtrados por país

#### **Interceptor de Autenticación**
El `AuthInterceptor` agrega automáticamente el token de autenticación a todas las peticiones HTTP mediante el header Authorization.

### 3. 🏛️ Arquitectura MVVM con ViewModels

#### **ViewModels Implementados**

**AuthViewModel**
- Maneja el estado de login y registro
- Valida credenciales en tiempo real
- Gestiona errores de autenticación

**CountriesViewModel**
- Gestiona el listado de países
- Implementa búsqueda en tiempo real
- Observa estado de conectividad
- Maneja estados de carga y errores

**PackagesViewModel**
- Carga paquetes filtrados por país
- Mantiene referencia al país seleccionado
- Gestiona estados de la UI

#### **Patrón Repository**
El `RealPlansRepository` implementa la estrategia NetworkBoundResource con cache-first y sincronización de red automática.

#### **Programación Reactiva**
- StateFlow para estados UI reactivos
- Flow para flujos de datos asíncronos
- Coroutines para operaciones en background
- Recolección de estados con `collectAsStateWithLifecycle()` en Compose

### 4. 🧭 Navigation Component Type-Safe

#### **Navigation Graph**
La aplicación utiliza Navigation Component con un grafo que incluye:
- LoginFragment como pantalla inicial
- CountriesFragment para el listado de países
- PackagesFragment con argumentos type-safe para recibir información del país

#### **Safe Args**
Se utiliza el tipo `CountryArg` (Parcelable) para pasar información del país seleccionado entre fragmentos de manera segura, con validación en tiempo de compilación.

### 5. 💉 Inyección de Dependencias (Hilt)

#### **Módulos Implementados**

**NetworkModule**
Proporciona instancias de Retrofit configuradas con interceptors de autenticación y conectividad.

**DatabaseModule**
Proporciona la instancia singleton de la base de datos Room y los DAOs.

**RepositoryModule**
Vincula las implementaciones concretas de repositorios con sus interfaces.

**AuthModule**
Proporciona componentes relacionados con autenticación como SessionManager.

---

## 🛡️ Manejo de Errores

### Tipos de Errores Manejados

1. **Errores de Red**
   - Sin conexión a internet → Banner offline + cache local
   - Timeout de servidor → Mensaje específico al usuario
   - Errores HTTP (4xx, 5xx) → Mensajes contextuales

2. **Errores de Base de Datos**
   - Fallos en escritura/lectura → Logging y fallback
   - Corrupción de datos → Recreación de tablas

3. **Errores de Validación**
   - Email inválido → Mensaje en tiempo real
   - Contraseña débil → Indicaciones de requisitos
   - Campos vacíos → Validación antes de submit

4. **Errores de Autenticación**
   - Credenciales incorrectas → Mensaje claro
   - Sesión expirada → Redirect a login
   - Usuario no encontrado → Sugerencia de registro

### Implementación

La aplicación maneja errores mediante try-catch en ViewModels y repositorios, con estados específicos que se reflejan en la UI. Los errores se comunican al usuario mediante mensajes claros y contextuales.

---

## 🌐 Internacionalización

### Idiomas Soportados
- 🇪🇸 Español (values-es)
- 🇩🇪 Alemán (values-de)
- 🇬🇧 Inglés (default)

### Estrategia
- Cero strings hard-coded en el código
- Todos los textos en archivos `strings.xml`
- Nombres de recursos descriptivos y consistentes

---

## 🔧 Tecnologías y Librerías

### Core
- **Kotlin** - Lenguaje principal
- **Coroutines & Flow** - Programación asíncrona y reactiva
- **Jetpack Compose** - UI moderna (parcial, componentes específicos)
- **View Binding** - Binding seguro de vistas XML

### Arquitectura
- **Hilt** - Inyección de dependencias
- **ViewModel & LiveData** - Gestión de estado UI
- **Room** - Persistencia local
- **Navigation Component** - Navegación type-safe
- **Safe Args** - Paso de argumentos tipados

### Networking
- **Retrofit** - Cliente HTTP
- **OkHttp** - Interceptors y logging
- **Gson** - Serialización JSON

### UI
- **Material Design 3** - Componentes UI
- **Glide** - Carga de imágenes
- **RecyclerView** - Listas eficientes

### Testing
- **JUnit** - Unit testing
- **Espresso** - UI testing

---

**Desarrollado con ❤️ usando Kotlin y Android Jetpack**
