# Batalla Naval

Juego de estrategia "Batalla Naval" (Battleship) desarrollado en JavaFX, donde un
jugador humano se enfrenta a la máquina. Cada jugador despliega una flota de 10 barcos
en un tablero de 10x10 e intenta hundir la flota del oponente.

---

## Descripción

El juego se desarrolla sobre dos tableros de 10x10:

- **Tablero de Posición:** el territorio del jugador humano. Muestra su flota y los
  disparos recibidos del oponente.
- **Tablero Principal:** el territorio de la máquina, donde el jugador humano realiza
  sus disparos para hundir la flota enemiga.

La flota de cada jugador se compone de 10 barcos: 1 portaaviones (4 casillas),
2 submarinos (3 casillas), 3 destructores (2 casillas) y 4 fragatas (1 casilla).

El jugador coloca su flota manualmente arrastrando los barcos al tablero, mientras que
la máquina la coloca de forma aleatoria. Los turnos siguen la regla clásica: un disparo
al agua pasa el turno al oponente, mientras que un impacto o hundimiento permite volver
a disparar. Gana quien hunda primero toda la flota enemiga.

## Funcionalidades

- Colocación manual de la flota mediante arrastrar y soltar, con validación de
  superposición y límites del tablero.
- Colocación automática de la flota de la máquina.
- Inteligencia artificial de la máquina que dispara de forma autónoma sin repetir
  casillas.
- Representación visual de agua, barco, tocado y hundido mediante figuras 2D.
- Opción de verificación para revelar el tablero de la máquina.
- Guardado automático tras cada jugada y reanudación de la partida.
- Temporizador de partida y turnos de la máquina ejecutados en hilos independientes.

## Tecnologías

- **Lenguaje:** Java 17
- **Interfaz gráfica:** JavaFX 21 (FXML + Scene Builder)
- **Gestión de dependencias:** Maven
- **Pruebas unitarias:** JUnit 6
- **Documentación:** Javadoc
- **IDE:** IntelliJ IDEA
- **Control de versiones:** Git y GitHub

## Requisitos previos

- JDK 17 o superior instalado.
- Maven instalado (o usar el wrapper de Maven incluido en el proyecto).

## Cómo obtener el proyecto

```bash
git clone https://github.com/jorgebelalcazar/miniproyecto-batallanaval.git
cd miniproyecto-batallanaval
```

## Cómo compilar y ejecutar

Compilar el proyecto:

```bash
mvn clean compile
```

Ejecutar la aplicación:

```bash
mvn javafx:run
```

Ejecutar las pruebas unitarias:

```bash
mvn test
```

Generar la documentación Javadoc (se genera en `target/site/apidocs`):

```bash
mvn javadoc:javadoc
```

## Cómo jugar

1. Ingresa un nickname en la pantalla de inicio y pulsa **Nuevo Juego**.
2. Coloca tu flota en el tablero: arrastra cada barco desde la bandeja al tablero.
   Usa **Rotar** para cambiar la orientación entre horizontal y vertical, o
   **Colocar aleatorio** para ubicar toda la flota automáticamente.
3. Pulsa **Empezar partida** cuando toda la flota esté colocada.
4. Dispara haciendo clic en las casillas del Tablero Principal. Si aciertas, vuelves a
   disparar; si es agua, el turno pasa a la máquina.
5. La partida se guarda automáticamente. Puedes salir con **Volver al menú** y
   retomarla más tarde con **Continuar**.
6. Gana quien hunda primero toda la flota enemiga.

## Estructura del proyecto

El código sigue la arquitectura Modelo-Vista-Controlador (MVC):

- `model` — entidades del dominio: tablero, barcos, coordenadas, celdas y estados.
- `service` — lógica del juego: gestión de la partida y colocación de flotas.
- `strategy` — estrategia de disparo de la máquina (patrón Strategy).
- `factory` — creación de barcos (patrón Factory).
- `persistence` — guardado y carga de la partida (archivos serializables y planos).
- `concurrency` — hilos del turno de la máquina y del temporizador.
- `controller` — controladores de las vistas (inicio, colocación y juego).
- `view` — componentes visuales y gestión de escenas.

## Patrones de diseño

- **Factory** (creacional): centraliza la creación de barcos.
- **Strategy** (de comportamiento): encapsula el algoritmo de disparo de la máquina,
  permitiendo cambiarlo sin modificar la lógica del juego.
- **Singleton** (de apoyo): gestión centralizada de las vistas.

## Autor

**Jorge Iván Belalcázar**<br>
Universidad del Valle<br>
Fundamentos de Programación Orientada a Eventos (750014C)