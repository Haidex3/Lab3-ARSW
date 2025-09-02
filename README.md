
## Escuela Colombiana de Ingeniería
### Arquitecturas de Software – ARSW


#### Ejercicio – programación concurrente, condiciones de carrera y sincronización de hilos. EJERCICIO INDIVIDUAL O EN PAREJAS.

##### Parte I – Antes de terminar la clase.

Control de hilos con wait/notify. Productor/consumidor.

1. Revise el funcionamiento del programa y ejecútelo. Mientras esto ocurren, ejecute jVisualVM y revise el consumo de CPU del proceso correspondiente. A qué se debe este consumo?, cual es la clase responsable?
2. Haga los ajustes necesarios para que la solución use más eficientemente la CPU, teniendo en cuenta que -por ahora- la producción es lenta y el consumo es rápido. Verifique con JVisualVM que el consumo de CPU se reduzca.
3. Haga que ahora el productor produzca muy rápido, y el consumidor consuma lento. Teniendo en cuenta que el productor conoce un límite de Stock (cuantos elementos debería tener, a lo sumo en la cola), haga que dicho límite se respete. Revise el API de la colección usada como cola para ver cómo garantizar que dicho límite no se supere. Verifique que, al poner un límite pequeño para el 'stock', no haya consumo alto de CPU ni errores.


##### Parte II. – Antes de terminar la clase.

Teniendo en cuenta los conceptos vistos de condición de carrera y sincronización, haga una nueva versión -más eficiente- del ejercicio anterior (el buscador de listas negras). En la versión actual, cada hilo se encarga de revisar el host en la totalidad del subconjunto de servidores que le corresponde, de manera que en conjunto se están explorando la totalidad de servidores. Teniendo esto en cuenta, haga que:

- La búsqueda distribuida se detenga (deje de buscar en las listas negras restantes) y retorne la respuesta apenas, en su conjunto, los hilos hayan detectado el número de ocurrencias requerido que determina si un host es confiable o no (_BLACK_LIST_ALARM_COUNT_).
- Lo anterior, garantizando que no se den condiciones de carrera.

##### Parte III. – Avance para el martes, antes de clase.

Sincronización y Dead-Locks.

![](http://files.explosm.net/comics/Matt/Bummed-forever.png)

1. Revise el programa “highlander-simulator”, dispuesto en el paquete edu.eci.arsw.highlandersim. Este es un juego en el que:

	* Se tienen N jugadores inmortales.
	* Cada jugador conoce a los N-1 jugador restantes.
	* Cada jugador, permanentemente, ataca a algún otro inmortal. El que primero ataca le resta M puntos de vida a su contrincante, y aumenta en esta misma cantidad sus propios puntos de vida.
	* El juego podría nunca tener un único ganador. Lo más probable es que al final sólo queden dos, peleando indefinidamente quitando y sumando puntos de vida.

2. Revise el código e identifique cómo se implemento la funcionalidad antes indicada. Dada la intención del juego, un invariante debería ser que la sumatoria de los puntos de vida de todos los jugadores siempre sea el mismo(claro está, en un instante de tiempo en el que no esté en proceso una operación de incremento/reducción de tiempo). Para este caso, para N jugadores, cual debería ser este valor?.

3. Ejecute la aplicación y verifique cómo funcionan las opción ‘pause and check’. Se cumple el invariante?.

4. Una primera hipótesis para que se presente la condición de carrera para dicha función (pause and check), es que el programa consulta la lista cuyos valores va a imprimir, a la vez que otros hilos modifican sus valores. Para corregir esto, haga lo que sea necesario para que efectivamente, antes de imprimir los resultados actuales, se pausen todos los demás hilos. Adicionalmente, implemente la opción ‘resume’.

5. Verifique nuevamente el funcionamiento (haga clic muchas veces en el botón). Se cumple o no el invariante?.

6. Identifique posibles regiones críticas en lo que respecta a la pelea de los inmortales. Implemente una estrategia de bloqueo que evite las condiciones de carrera. Recuerde que si usted requiere usar dos o más ‘locks’ simultáneamente, puede usar bloques sincronizados anidados:

	```java
	synchronized(locka){
		synchronized(lockb){
			…
		}
	}
	```

7. Tras implementar su estrategia, ponga a correr su programa, y ponga atención a si éste se llega a detener. Si es así, use los programas jps y jstack para identificar por qué el programa se detuvo.

8. Plantee una estrategia para corregir el problema antes identificado (puede revisar de nuevo las páginas 206 y 207 de _Java Concurrency in Practice_).

9. Una vez corregido el problema, rectifique que el programa siga funcionando de manera consistente cuando se ejecutan 100, 1000 o 10000 inmortales. Si en estos casos grandes se empieza a incumplir de nuevo el invariante, debe analizar lo realizado en el paso 4.

10. Un elemento molesto para la simulación es que en cierto punto de la misma hay pocos 'inmortales' vivos realizando peleas fallidas con 'inmortales' ya muertos. Es necesario ir suprimiendo los inmortales muertos de la simulación a medida que van muriendo. Para esto:
	* Analizando el esquema de funcionamiento de la simulación, esto podría crear una condición de carrera? Implemente la funcionalidad, ejecute la simulación y observe qué problema se presenta cuando hay muchos 'inmortales' en la misma. Escriba sus conclusiones al respecto en el archivo RESPUESTAS.txt.
	* Corrija el problema anterior __SIN hacer uso de sincronización__, pues volver secuencial el acceso a la lista compartida de inmortales haría extremadamente lenta la simulación.

11. Para finalizar, implemente la opción STOP.

<!--
### Criterios de evaluación

1. Parte I.
	* Funcional: La simulación de producción/consumidor se ejecuta eficientemente (sin esperas activas).

2. Parte II. (Retomando el laboratorio 1)
	* Se modificó el ejercicio anterior para que los hilos llevaran conjuntamente (compartido) el número de ocurrencias encontradas, y se finalizaran y retornaran el valor en cuanto dicho número de ocurrencias fuera el esperado.
	* Se garantiza que no se den condiciones de carrera modificando el acceso concurrente al valor compartido (número de ocurrencias).


2. Parte III.
	* Diseño:
		- Coordinación de hilos:
			* Para pausar la pelea, se debe lograr que el hilo principal induzca a los otros a que se suspendan a sí mismos. Se debe también tener en cuenta que sólo se debe mostrar la sumatoria de los puntos de vida cuando se asegure que todos los hilos han sido suspendidos.
			* Si para lo anterior se recorre a todo el conjunto de hilos para ver su estado, se evalúa como R, por ser muy ineficiente.
			* Si para lo anterior los hilos manipulan un contador concurrentemente, pero lo hacen sin tener en cuenta que el incremento de un contador no es una operación atómica -es decir, que puede causar una condición de carrera- , se evalúa como R. En este caso se debería sincronizar el acceso, o usar tipos atómicos como AtomicInteger).

		- Consistencia ante la concurrencia
			* Para garantizar la consistencia en la pelea entre dos inmortales, se debe sincronizar el acceso a cualquier otra pelea que involucre a uno, al otro, o a los dos simultáneamente:
			* En los bloques anidados de sincronización requeridos para lo anterior, se debe garantizar que si los mismos locks son usados en dos peleas simultánemante, éstos será usados en el mismo orden para evitar deadlocks.
			* En caso de sincronizar el acceso a la pelea con un LOCK común, se evaluará como M, pues esto hace secuencial todas las peleas.
			* La lista de inmortales debe reducirse en la medida que éstos mueran, pero esta operación debe realizarse SIN sincronización, sino haciendo uso de una colección concurrente (no bloqueante).

	

	* Funcionalidad:
		* Se cumple con el invariante al usar la aplicación con 10, 100 o 1000 hilos.
		* La aplicación puede reanudar y finalizar(stop) su ejecución.
		
		-->

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />Este contenido hace parte del curso Arquitecturas de Software del programa de Ingeniería de Sistemas de la Escuela Colombiana de Ingeniería, y está licenciado como <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.



### Solucion 

**parte 1.1**
El `Producer` tiene un `while(true)` que siempre está produciendo un nuevo número cada segundo y ademas, el `Consumer` también tiene un `while(true)`, pero no tiene ningún sleep() ni espera real. Simplemente:

	while (true) {
		if (queue.size() > 0) {
			int elem = queue.poll();
			System.out.println("Consumer consumes " + elem);
		}
	}

Por lo tanto al ejecutar Visualvm, podemos observar que el consumo de cpu es bastante elevado para lo que realmente se esta haciendo.

**imagen 1**

**parte 1.2**
Antes de arreglar:

El `Consumer` revisaba la cola en un bucle sin fin lo cual producia un consumo de CPU.

Ahora:

El `Consumer` se queda bloqueado con `queue.wait()` cuando la cola está vacía.

El `Producer` notifica al consumidor para que vuela a reanudar su operacion con `queue.notifyAll()` al agregar un elemento.

Con esto arreglos podemos observar que el consumo de la aplicacion bajo casi a cero:

**imagen 2**

**parte 1.3**

Para poder resolver esto podemos hacer una `BlockingQueue` de Java `(java.util.concurrent)`, que ya maneja internamente el bloqueo con `put()` y `take()`.

`put(E e)`:bloquea al Productor si la cola está llena.

`take()`: bloquea al Consumidor si la cola está vacía.

Esto elimina la necesidad de manejar `wait()`/`notify()` manualmente.



**parte 3.2**

Al observar un poco en el codigo nos podemos dar cuenta de que todos los jugadores empiezan con 100 puntos de vida gracias a la linea:

	DEFAULT_IMMORTAL_HEALTH = 100

Encontrada en la clase `Inmortal`

Por lo cual el invariante en este caso seria el numero de jugadores multiplicado por 100:

`Salud total inicial=N×100`

**parte 3.3**

Al ejecutar el codigo y hacer el probar el boton `pause and check` nos damos cuenta que el invariante no se cumple ya que cada vez que se pulsa el boton nos da un resultado distinto

**parte 3.4**

Ahora para resolver el error segun nuestra primera teoria lo que hicimos fue que se pausen todos los hilos al momento de hacer el check, ademas de implementar lo necesario para el "resume", para esto agregamos:

En la clase `Immortal`

- Se introdujimos una variable estática `paused` y un objeto de sincronización pauseLock.

- Dentro del ciclo `run()`, antes de ejecutar la lógica de pelea, cada hilo verifica si está en pausa, si lo está, se bloquea en `pauseLock.wait()` hasta que sea reanudado.

Ademas agregamos los métodos estáticos `pauseAll()` y `resumeAll()` para activar la pausa o reanudar todos los hilos a la vez.

En la clase `ControlFrame`

En el botón “Pause and check” ahora se llama a `Immortal.pauseAll()`, garantizando que todos los hilos sean detenidos antes de calcular la suma de las vidas, de esta forma, cuando se hace la suma, ningún hilo puede estar modificando la salud.

En el botón “Resume” se implementó la llamada a `Immortal.resumeAll()`, que libera a los hilos y les permite continuar su ejecución normal.

**Parte 3.5**

Al probar nuevamente con las modificaciones del punto anterior podemos observar que a pesar de pausarse correctamente no se esta cumpliendo el invariante

**Parte 3.6**

Identificamos como region critica el uan parte del metodo `fight()` donde un inmortal resta vida a otro y al mismo tiempo incrementa la suya.

Para poder resolver la inconsistencia del invariante:

- Asignamos un lock individual a cada inmortal, de modo que cualquier operación de lectura o escritura sobre su salud quedo protegida.

- Sincronizamos el bloque de pelea tomando los locks de los dos inmortales involucrados, asegurando que la transferencia de vida sea una operación indivisible.

Definimos un orden global para adquirir los locks (usando `System.identityHashCode`) con el fin de evitar interbloqueos cuando dos hilos intentan pelear al mismo tiempo, es decir aplicar la estrategia de bloqueos por "orden".

Protegimos los métodos `getHealth()`, `changeHealth()` y `toString()` con el lock de cada inmortal.


