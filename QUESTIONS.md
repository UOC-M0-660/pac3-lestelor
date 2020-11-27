# PARTE TEORICA

### Lifecycle

#### Explica el ciclo de vida de una Activity.

##### ¿Por qué vinculamos las tareas de red a los componentes UI de la aplicación?
Las tareas de red son asíncronas y requieren de un tiempo para ser procesadas. Además, normalmente requieren de información validada por el usuario. 
Es fundamental que las app sean procesadas en backgruound, dentro del ciclo de vida de la propia activity, siendo destruidas las llamadas cuando la activity es destruida. Existe un ciclo de vida optimizado para las llamadas de red, en concreto Dispatchers.IO.
Otras actividades en background tienen otros lifecycles óptimos (https://medium.com/androiddevelopers/coroutines-on-android-part-i-getting-the-background-3e0e54d20bb):
+-----------------------------------+
| Dispatchers.Main |
+-----------------------------------+
| Main thread on Android, interact |
| with the UI and perform light |
| work |
+-----------------------------------+
| - Calling suspend functions |
| - Call UI functions |
| - Updating LiveData |
+-----------------------------------+

+-----------------------------------+
| Dispatchers.IO |
+-----------------------------------+
| Optimized for disk and network IO |
| off the main thread |
+-----------------------------------+
| - Database* |
| - Reading/writing files |
| - Networking** |
+-----------------------------------+

+-----------------------------------+
| Dispatchers.Default |
+-----------------------------------+
| Optimized for CPU intensive work |
| off the main thread |
+-----------------------------------+
| - Sorting a list |
| - Parsing JSON |
| - DiffUtils |
+-----------------------------------+


##### ¿Qué pasaría si intentamos actualizar la recyclerview con nuevos streams después de que el usuario haya cerrado la aplicación?
En caso de no destruir proceso, la app seguiría funcionando en segundo plano y probablemente daría un error al intentar actualizar un recurso de la interfaz cuando no existe un contexto, o el mismo se ha destruido.

##### Describe brevemente los principales estados del ciclo de vida de una Activity.
La Activity proporciona un conjunto básico de seis llamadas:
1.	onCreate(): La actividad es inicializada y es momento para inicializar la interfaz de usuario. Cuando finish() se llama a onDestroy (). En el caso de fragments, la view es inflada en onCreateView().

2.	OnStart(): Llamado después de onCreate (Bundle) - o después de onRestart () cuando la actividad se había detenido, pero ahora se muestra nuevamente al usuario. Por lo general, irá seguido de onResume (). Este es un buen lugar para comenzar a dibujar elementos visuales, ejecutar animaciones, etc.

Puede llamar a finish () desde esta función, en cuyo caso onStop () se llamará inmediatamente después de onStart () sin que se ejecuten las transiciones del ciclo de vida intermedias (onResume (), onPause (), etc.).

3.	OnResume(): Se llama después de onRestoreInstanceState (Bundle), onRestart () o onPause (), para que su actividad comience a interactuar con el usuario. Este es un indicador de que la actividad se volvió activa y lista para recibir información. Está encima de una pila de actividades y es visible para el usuario.

4.	OnPause(): Se llama como parte del ciclo de vida de la actividad cuando el usuario ya no interactúa activamente con la actividad, pero aún está visible en la pantalla. La contraparte de onResume ().

5.	OnStop(): Llamado cuando ya no es visible para el usuario. A continuación, recibirá onRestart (), onDestroy () o nada, según la actividad posterior del usuario. Este es un buen lugar para dejar de actualizar la interfaz de usuario, ejecutar animaciones y otras cosas visuales.

6.	OnDestroy(): Realiza limpieza final antes de que se destruya una actividad. Esto puede suceder porque la actividad está terminando (alguien llamó a finish() en ella) o porque el sistema está destruyendo temporalmente esta instancia de la actividad para ahorrar espacio. Puede distinguir entre estos dos escenarios con el método isFinishing ().

---

### Paginación 

#### Explica el uso de paginación en la API de Twitch.

##### ¿Qué ventajas ofrece la paginación a la aplicación?
Permite ir descargando contenido a medida que se necesita, evitando así ocupar a la app con la búsqueda de infinitos resultados 

##### ¿Qué problemas puede tener la aplicación si no se utiliza paginación?
La aplicación tendría un comportamiento indeseado al quedar continuamente actualizando la recyclreview, por lo que mostrar los resultados de manera escalonada es la opción elegida por la mayoría de las API.

##### Lista algunos ejemplos de aplicaciones que usan paginación.
Pinterest
Instagram
Google Fotos
Etc.

