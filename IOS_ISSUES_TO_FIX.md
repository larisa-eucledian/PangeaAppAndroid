# iOS Issues Pendientes

Este documento lista los problemas identificados en la app de iOS que necesitan ser corregidos en una sesión futura.

## 1. Lista de eSIMs - Animación de carga doble ⚠️

**Problema:**
La lista de eSIMs se visualiza como si cargara doble, mostrando una animación extraña que resulta molesta para el usuario.

**Síntoma:**
- Al abrir la pantalla de "Mis eSIMs", la lista parece cargarse dos veces
- Puede causar un parpadeo o animación repetida
- Afecta la experiencia de usuario

**Posible causa:**
- Podría ser un problema con el `UICollectionView` o `UITableView` reload
- Probablemente relacionado con el fetch de datos inicial y el refresh automático
- Revisar si hay múltiples llamadas a `reloadData()` o `apply()` en snapshots

**Archivos a revisar:**
- `/PangeaApp/Features/ESims/ESimsViewController.swift`
- Buscar múltiples llamadas a reload o update de la lista

---

## 2. Búsqueda de países dejó de funcionar 🔍

**Problema:**
El search bar de países dejó de funcionar correctamente. Antes funcionaba bien, pero ahora no filtra adecuadamente.

**Comportamiento esperado:**
- Para geografía **LOCAL**: Buscar en `countryName` y en `coverage`
- Para geografía **REGIONAL/GLOBAL**: Buscar en `countryName` y en `coverage`

**Comportamiento actual:**
- La búsqueda no funciona o filtra incorrectamente

**Nota importante:**
✅ En Android funciona correctamente
❌ En iOS dejó de funcionar

**Archivos a revisar:**
- `/PangeaApp/Features/Countries/CountriesViewController.swift`
- Método de filtrado/búsqueda
- Comparar con la implementación de Android que sí funciona:
  - Android: `/app/src/main/java/com/example/pangeaapp/ui/countries/CountriesViewModel.kt`

---

## 3. Package Info en eSIM Detail muestra datos incorrectos 📦

**Problema:**
Los detalles del paquete (package details) mostrados en el eSIM detail screen no coinciden con el paquete que se compró.

**Síntoma:**
- Muestra información del paquete **actual** en la base de datos
- No muestra la información del paquete **en el momento de compra**
- Ejemplo: Compró "1 GB" pero ahora muestra "5120 MB" (paquete actualizado)

**Comportamiento esperado:**
- Debería mostrar la información del paquete tal como estaba cuando se compró el eSIM
- La información debe ser una "snapshot" del momento de compra

**Nota importante:**
✅ El **eSIM Usage** funciona correctamente en iOS
❌ Solo los **Package Details** muestran datos incorrectos

**Solución aplicada en Android:**
- Se eliminó completamente la sección de package details
- Solo se muestra el `packageName` en el header (que ya contiene toda la info)
- Se muestra solo el **usage** (que sí es correcto)

**Archivos a revisar:**
- `/PangeaApp/Features/ESims/ESimDetailViewController.swift`
- Método `addPackageFeatures()`
- Considerar aplicar la misma solución que en Android: eliminar los package details

---

## 4. Video en Countries no carga al 100% del ancho inicialmente 📹

**Problema:**
El video en la pantalla de Countries no se carga al 100% del width en la primera carga.

**Síntoma:**
- Al abrir la pantalla de Countries por primera vez, el video no ocupa todo el ancho
- Al cambiar de vista y regresar, entonces sí carga correctamente al 100% del ancho

**Posible causa:**
- Problema con Auto Layout constraints
- El frame del video player no se calcula correctamente en `viewDidLoad`
- Podría necesitar un `layoutIfNeeded()` o esperar a `viewDidLayoutSubviews`

**Archivos a revisar:**
- `/PangeaApp/Features/Countries/CountriesViewController.swift`
- Setup del video player
- Constraints del video view

---

## Prioridad de Resolución

1. **Alta:** Búsqueda de países (funcionalidad rota)
2. **Alta:** Package info incorrecto (datos erróneos mostrados al usuario)
3. **Media:** Video no carga correctamente (afecta UX pero es visual)
4. **Baja:** Animación de carga doble en lista (molesto pero no crítico)

---

## Notas para la Siguiente Sesión

- Android ya tiene resueltos estos problemas (usar como referencia)
- El custom TypeAdapter en Android resolvió el issue de Gson con nested "status" fields
- Considerar el mismo enfoque: eliminar package details en iOS y solo mostrar usage
- Revisar el data flow completo en iOS comparado con Android
