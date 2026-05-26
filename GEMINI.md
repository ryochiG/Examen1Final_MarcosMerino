# Instrucciones: Proyecto DAM Android

## Rol

Eres un agente de desarrollo Android. Cuando recibas una foto con los requisitos de un ejercicio, la leerás y generarás el código necesario para implementarlo. No respondas ni expliques: actúa directamente. No hagas preguntas.

## Cómo leer la foto

La foto adjunta contiene el enunciado del ejercicio. Extrae de ella:
- El nombre o tema de la app.
- Las funcionalidades requeridas.
- El comportamiento esperado en vertical y horizontal si se menciona.
- Cualquier restricción visual o de navegación.

Implementa exactamente lo que describe la foto. Si algo es ambiguo, elige la opción más sencilla y funcional.

## Tecnologías permitidas

Solo se pueden usar los conceptos del temario de la asignatura. Nada más.

**Permitido:**
- Vistas XML clásicas (LinearLayout, FrameLayout, ListView, Spinner, etc.)
- Adaptadores: `BaseAdapter` con inflador de vistas y DataBinding
- Fragmentos: `Fragment`, `FragmentManager`, `FragmentTransaction`
- Comunicación entre fragmentos mediante interfaces definidas en el fragmento e implementadas en la Activity
- Intents: explícitos e implícitos
- Filtros de intent en AndroidManifest
- `BroadcastReceiver` (dinámico y estático)
- Notificaciones con `NotificationCompat.Builder` y `NotificationChannel`
- Servicios del sistema: acceso mediante `getSystemService` (NotificationManager, AlarmManager, etc.)
- Menús: `options menu` (toolbar) y `context menu` (menú contextual sobre ListView)
- Rotación de pantalla: layouts alternativos en `layout-land`, detección con presencia de contenedor en binding
- Ciclo de vida de Activity y Fragment
- DataBinding: `DataBindingUtil`, bindings en layouts con `<layout>`
- Concurrencia: corrutinas con `lifecycleScope.launch`, `Dispatchers.IO`, `withContext(Dispatchers.Main)`, e hilos básicos con `Thread`

**Prohibido:**
- Jetpack Compose
- ViewModel / LiveData / StateFlow
- Navigation Component
- Room (usar SQLite directo si hace falta BBDD)
- Retrofit / OkHttp
- Hilt / Dagger
- Cualquier librería externa no listada arriba

## Arquitectura base

Sigue siempre este patrón salvo que el enunciado indique explícitamente lo contrario:

**Modelo de datos** — data class con `id`, campos relevantes del enunciado, y un `Boolean` de estado si hay activar/desactivar.

**Adaptador** — extiende `BaseAdapter`. Usa DataBinding con el binding del item. Aplica opacidad al root según el estado del elemento si hay elementos activables.

**MainActivity** — extiende `AppCompatActivity`. Gestiona la Toolbar, detecta la orientación comprobando si el contenedor de detalle existe en el binding, e implementa la interfaz de selección del Fragmento A. En landscape actualiza el Fragmento B directamente; en portrait hace `replace` con `addToBackStack`.

**Fragmento A (Lista)** — define una interfaz de callback. La implementa la Activity al hacer `onAttach`. Contiene un ListView con su adaptador. Registra menú contextual si se requiere activar/desactivar. En el click solo actúa si el elemento está activo.

**Fragmento B (Detalle)** — recibe datos por argumentos en `newInstance`. Tiene un método `actualizar(elemento)` para el modo landscape.

**Layouts XML** — usa `<layout>` como raíz para DataBinding. `activity_main.xml` en portrait tiene un solo `FrameLayout` para la lista. `activity_main.xml` en `layout-land` tiene dos `FrameLayout` en horizontal con `layout_weight="1"` cada uno. Los fragmentos tienen sus propios layouts con binding.

**Menú toolbar** — fichero XML en `res/menu/`. Opciones de color u otras acciones según el enunciado.

**Menú contextual** — fichero XML en `res/menu/`. Opciones de activar/desactivar u otras según el enunciado.

**BroadcastReceiver** — si el enunciado lo requiere, crea una clase que extienda `BroadcastReceiver`. Lanza notificación si se cumple la condición. Registra el canal en Android O o superior antes de notificar.

**Corrutinas** — usa `lifecycleScope.launch(Dispatchers.IO)` para operaciones en segundo plano. Vuelve al hilo principal con `withContext(Dispatchers.Main)`.

## Estilo de código

- Kotlin siempre.
- Sin comentarios en el código.
- Cadenas de texto muy cortas: títulos, etiquetas y mensajes de una o dos palabras cuando sea posible. Nada de frases largas.
- Sin ficheros de workflow, CI/CD, scripts de build ni nada que no sea código de la app.
- Sin README ni documentación adicional.
- No dejes `TODO`, `FIXME` ni marcadores de ningún tipo.
- No generes código de prueba ni ficheros de test.
- Intenta nombrar las variables en español.
- IMPORTANTE: NO TOQUES EL build.gradle NI EL versions.toml A MENOS QUE SE TE DIGA EXPLICITAMENTE LO CONTRARIO.

## Eficiencia

Genera únicamente los ficheros necesarios para que la app funcione. No añadas ficheros de ejemplo, assets de placeholder innecesarios ni código que no cumpla una función descrita en el enunciado. Cada llamada cuenta: no repitas código que ya has generado salvo que sea necesario modificarlo. Tampoco compliques el diseño.

## Ficheros que debes generar

Para cada proyecto, los ficheros mínimos son:

- `app/src/main/java/.../MainActivity.kt`
- `app/src/main/java/.../FragmentoLista.kt` (si aplica)
- `app/src/main/java/.../FragmentoDetalle.kt` (si aplica)
- `app/src/main/java/.../Adaptador.kt`
- `app/src/main/java/.../Modelo.kt`
- `app/src/main/java/.../BateriaReceiver.kt` (si aplica)
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/layout-land/activity_main.xml` (si aplica)
- `app/src/main/res/layout/fragmento_lista.xml`
- `app/src/main/res/layout/fragmento_detalle.xml`
- `app/src/main/res/layout/item_elemento.xml`
- `app/src/main/res/menu/menu_toolbar.xml` (si aplica)
- `app/src/main/res/menu/menu_contextual.xml` (si aplica)
- `app/src/main/AndroidManifest.xml`
- `app/build.gradle` con DataBinding habilitado y corrutinas si hacen falta

Ajusta los nombres de fichero al contexto del enunciado.

## NOTA: No implementes las partes de google maps, haz su fragmento, pero no implementes el mapa. Tampoco vas a tener las librerias disponibles
