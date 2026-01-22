# Reduce & Conquer

*Reduce. Conquer. Repeat.*

[![medium](https://img.shields.io/badge/Medium-12100E?style=for-the-badge&logo=medium&logoColor=white)](https://medium.com/@numq/reduce-conquer-repeat-how-the-reduce-conquer-architecture-can-improve-your-compose-9fece98a3bb8)

[Reduce, Conquer, Repeat: How the “Reduce & Conquer” Architecture Can Improve Your Compose Application](https://medium.com/@numq/reduce-conquer-repeat-how-the-reduce-conquer-architecture-can-improve-your-compose-9fece98a3bb8)

|                                                                  🖤                                                                   |                  Support this project                   |               
|:-------------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------:|
|  <img src="https://raw.githubusercontent.com/ErikThiart/cryptocurrency-icons/master/32/bitcoin.png" alt="Bitcoin (BTC)" width="32"/>  | <code>bc1qs6qq0fkqqhp4whwq8u8zc5egprakvqxewr5pmx</code> | 
| <img src="https://raw.githubusercontent.com/ErikThiart/cryptocurrency-icons/master/32/ethereum.png" alt="Ethereum (ETH)" width="32"/> | <code>0x3147bEE3179Df0f6a0852044BFe3C59086072e12</code> |
|  <img src="https://raw.githubusercontent.com/ErikThiart/cryptocurrency-icons/master/32/tether.png" alt="USDT (TRC-20)" width="32"/>   |     <code>TKznmR65yhPt5qmYCML4tNSWFeeUkgYSEV</code>     |

___

## Navigation

- [About](#about)
- [Changelog](#changelog)
- [Overview](#overview)
    - [State](#state)
    - [Command](#command)
    - [Event](#Event)
    - [Effect](#effect)
    - [Feature](#feature)
    - [Reducer](#reducer)
    - [Transition](#transition)
    - [Effect System](#effect-system)
- [Mathematical proof](#mathematical-proof)
    - [Definition](#definition)
    - [Proposition](#proposition)
    - [Proof of Associativity](#proof-of-associativity)
    - [Proof of Commutativity](#proof-of-commutativity)
    - [Conclusion](#conclusion)
- [Comparison with popular patterns](#comparison-with-popular-patterns)
    - [Model-View-Controller (MVC)](#model-view-controller-mvc)
    - [Model-View-ViewModel (MVVM)](#model-view-viewmodel-mvvm)
    - [Model-View-Intent (MVI) & Redux](#model-view-intent-mvi--redux)
    - [The Elm Architecture (TEA)](#the-elm-architecture-tea)
    - [Actor Model](#actor-model)
    - [Finite State Machines (FSM)](#finite-state-machines-fsm)
    - [Command Query Responsibility Segregation (CQRS)](#command-query-responsibility-segregation-cqrs)
    - [Event-Driven Architecture (EDA)](#event-driven-architecture-eda)
- [Clean Architecture](#clean-architecture)
    - [Working with data flows](#working-with-data-flows)
    - [Testing](#testing)
- [Proof of concept](#proof-of-concept)
    - [Features](#features)
    - [Libraries](#libraries)
- [More examples](#more-examples)

# About

This repository contains a [proof of concept](#proof-of-concept) of the **Reduce & Conquer** pattern built into
the [Clean Architecture](#clean-architecture), using the example of a cross-platform **Pokédex** application built using
the Compose Multiplatform UI Framework.

![Gif application demonstration](media/demonstration.gif)

# Changelog

## [3.0.0](https://github.com/numq/reduce-and-conquer/releases/tag/3.0.0)

- **Major simplification and optimization**: Removed `Factory`, `Strategy`, `Processor`, `Metrics`.
- **Dual-system architecture**: Separated concerns between `Event` (notifications) and `Effect` (side operations).
- **Structured side effect management**: Effects now handle flow collection, deferred execution, and cancellation.
- **Simplified core**: `BaseFeature` uses a channel for commands and a `scan` to manage state transitions.
- **Composable reducers**: Added `combine` operator for reducer composition.
- **Enhanced type safety**: Clear separation between events and operational effects.

## [2.0.0](https://github.com/numq/reduce-and-conquer/releases/tag/2.0.0)

- Command processing strategies: `Immediate`, `Channel`, `Parallel`
- Enhanced events with lifecycle management
- Built-in metrics collection
- Feature factory for easy creation

## [1.0.0](https://github.com/numq/reduce-and-conquer/releases/tag/1.0.0)

- Initial **Reduce & Conquer** pattern
- Basic `Feature`, `Reducer`, `Transition`
- Pokédex example app

# Overview

**Reduce & Conquer** is an architectural pattern leveraging functional programming principles and pure functions to
create predictable and testable functional components.

```mermaid
classDiagram
    class Feature~State, Command, Event~ {
        <<interface>>
        +StateFlow~State~ state
        +Flow~Event~ events
        +execute(command: Command)*
        +close()
    }
    
    class BaseFeature~State, Command, Event~ {
        -CoroutineScope scope
        -Reducer~State, Command, Event~ reducer
        -Channel~Command~ _commands
        -MutableSharedFlow~Event~ _events
        -Atomic~Map~Any, Job~~ jobs
        -processEffect(effect: Effect)
        -launchManaged(key: Any, block)
        -cancelJob(key: Any)
    }
    
    class Reducer~State, Command, Event~ {
        <<fun interface>>
        +reduce(state: State, command: Command): Transition~State, Event~*
        +transition(state: State)
        +action(key, fallback, block)
        +stream(key, flow, strategy, fallback)
        +cancel(key)
        +combine(other: Reducer)
    }
    
    class Transition~State, Event~ {
        +State state
        +List~Event~ events
        +List~Effect~ effects
    }
    
    class Effect {
        <<sealed interface>>
    }
    
    class Effect_Stream~Command~ {
        +Any key
        +Strategy strategy
        +Flow~Command~ flow
        +((Throwable) -> Command)? fallback
    }
    
    class Effect_Action~Command~ {
        +Any key
        +() -> Command block
        +((Throwable) -> Command)? fallback
    }
    
    class Effect_Cancel {
        +Any key
    }

    Effect <|.. Effect_Stream
    Effect <|.. Effect_Action
    Effect <|.. Effect_Cancel
    
    Feature <|.. BaseFeature
    BaseFeature --> Reducer
    BaseFeature --> Event
    BaseFeature --> Effect
    Reducer --> Transition
   
```

## State

> [!TIP]
> The idempotent nature of deterministic state allows you to implement functionality such as rolling back the state to a
> previous version.

A class or object that describes the current state of the functional unit (or component).

## Command

A class or object that describes an action that entails updating state and/or producing effects.

## Event

An interface that describes **events** caused by the execution of a command and the reduction of the state.

## Effect

> [!IMPORTANT]
> Effects are the primary mechanism for managing side effects in version 3.0.0. They handle reactive streams and
> deferred operations while maintaining separation from UI events.

An interface that describes **side effects** for managing asynchronous operations and reactive streams.

### Effect Types in version 3.0.0:

- `Stream<out Command>` - for collecting reactive data streams with automatic lifecycle management
- `Action<out Command>` - for executing deferred operations with error handling
- `Cancel` - for cancelling ongoing operations by key

### Effect Strategies

The `Effect.Stream` supports two execution strategies to manage how data flows are collected within the `BaseFeature`:

- `Sequential`: Processes emitted commands one by one. It uses the standard collect mechanism, ensuring that every
  command from the flow is executed in the order it was received.

- `Restart`: Implements "latest-only" logic using collectLatest. If a new emission occurs before the previous command
  processing is complete, the previous job is cancelled and replaced by the new one.

### Event vs Effect:

- **Events** are for notifications and state changes (fire-and-forget)
- **Effects** are for managing asynchronous operations and reactive streams

## Feature

An interface that takes three type parameters: `State`, `Command`, and `Event`.

**A functional unit** or aggregate of business logic within isolated functionality.

### Properties:

- `state`: A read-only state flow that exposes the current state.
- `events`: A flow that exposes the events produced by the feature.

### Methods:

- `suspend execute(command)`: Submits a command for processing.
- `close()`: Terminates all operations and cleans up resources.

### BaseFeature Implementation:

The `BaseFeature` class provides a reference implementation:

- Uses a `Channel` for command processing with `UNLIMITED` capacity
- Processes commands sequentially using `scan` operator
- Separates events from side effects:
    - **Events** are emitted externally
    - **Effects** are processed internally (flow collections, deferred executions)
- Provides automatic cleanup when closed

```kotlin
val feature = BaseFeature(
    initialState = MyState(),
    scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
    reducer = MyReducer()
)
```

## Reducer

A functional interface that takes three generic type parameters: `State`, `Command`, and `Event`.

A **stateless component** responsible for reducing the input command to a new state, events, and effects.

### Methods:

- `reduce(state, command)`: Reduces the `State` with the given `Command` and returns a `Transition` containing new
  state, events, and effects.

- `transition(state)`: Creates a `Transition` with the given `State` and empty
  events/effects lists. This is a convenience method for creating initial transitions.

### Helper DSL:

- `action(key, fallback, block)`: Creates an `Effect.Action`. Useful for one-off asynchronous operations like a single
  API call or database transaction.

- `stream(key, flow, strategy, fallback)`: Creates an `Effect.Stream`. It allows the feature to react to long-running
  data streams with a specific `Strategy`.

- `cancel(key)`: Creates an `Effect.Cancel`. Immediately terminates any ongoing job (`Action` or `Stream`) associated
  with the provided key.

### Reducer Composition:

Reducers can be combined using the `combine` operator, which merges both events and effects:

```kotlin
val combinedReducer = firstReducer.combine(secondReducer)
```

The combine operator works as follows:

- `state`: Uses the state from the second reducer (applied after the first)

- `events`: Concatenates events from both reducers (`src.events + dst.events`)

- `effects`: Concatenates effects from both reducers (`src.effects + dst.effects`)

## Transition

A data class that represents a state transition.

### Properties:

- `state`: The new `State`.

- `events`: A list of `Event`s emitted during the transition (for notifications).

- `effects`: A list of `Effect`s produced during the transition (for side effect management).

### Helper DSL:

- `event(event)`: Adds a single event to the transition

- `events(vararg events)`: Adds multiple events to the transition

- `effect(effect)`: Adds a single side effect to be processed

- `effects(vararg effects)`: Adds multiple side effects to the transition

## Effect System

The effect system provides structured side effect management:

```kotlin
class MyReducer : Reducer<MyState, MyCommand, MyEvent> {
    override fun reduce(state: MyState, command: MyCommand): Transition<MyState, MyEvent> {
        return when (command) {
            is MyCommand.LoadData -> transition(state)
                .effect(
                    stream(
                        key = "data_stream",
                        flow = dataRepository.observeData(),
                        fallback = { throwable -> MyCommand.HandleError(throwable) }
                    )
                )

            is MyCommand.PerformAction -> transition(state)
                .effect(
                    action(
                        key = "deferred_action",
                        block = {
                            val result = performComplexCalculation()
                            MyCommand.OnResult(result)
                        },
                        fallback = { throwable -> MyCommand.HandleError(throwable) }
                    )
                )

            is MyCommand.Stop -> transition(state)
                .effect(cancel(key = "data_stream"))
        }
    }
}
```

# Mathematical Proof

## Definition

Let $S$ be the set of states, $C$ be the set of commands, $Ev$ be the set of events, and $Ef$ be the set of effects.

We define a function $R: S \times C \rightarrow (S, Ev, Ef)$, which represents the reduction function that takes a state
and a command as input and returns a new state, a set of events, and a set of effects.

## Proposition

The function $R$ satisfies the following properties:

- **Associativity**: For all $s \in S$, $c_1, c_2 \in C$, we have:
  $$R(R(s, c_1), c_2) = R(s, [c_1, c_2])$$
  where $[c_1, c_2]$ denotes the composition of commands $c_1$ and $c_2$.

- **Commutativity (under specific conditions)**: For all $s \in S$, $c_1, c_2 \in C$ such that $c_1 \circ c_2 = c_2
  \circ c_1$, we have:
  $$R(s, c_1) = R(s, c_2)$$

## Proof of Associativity

Let $s \in S$, $c_1, c_2 \in C$. We need to show that:
$$R(R(s, c_1), c_2) = R(s, c_1 \circ c_2)$$

1. **Apply Command $c_1$**:
   $$R(s, c_1) = (s_1, ev_1, ef_1)$$
   where $s_1$ is the new state, $ev_1$ are the events generated, and $ef_1$ are the effects generated by applying $c_1$
   to state $s$.

2. **Apply Command $c_2$ to the New State $s_1$**:
   $$R(s_1, c_2) = (s_2, ev_2, ef_2)$$
   where $s_2$ is the new state after applying $c_2$ to $s_1$, $ev_2$ are the events generated, and $ef_2$ are the
   effects generated.

3. **Sequential Application of Commands $c_1$ and $c_2$**:
   $$R(s, c_1 \circ c_2) = (s_2, ev_1 \cup ev_2, ef_1 \cup ef_2)$$
   where $c_1 \circ c_2$ denotes applying $c_1$ first, resulting in $s_1$, $ev_1$, and $ef_1$, and then applying $c_2$
   to $s_1$, resulting in $s_2$, $ev_2$, and $ef_2$.

Since both $R(R(s, c_1), c_2)$ and $R(s, c_1 \circ c_2)$ yield the same state $s_2$, combined events $ev_1 \cup ev_2$,
and combined effects $ef_1 \cup ef_2$, we have:
$$R(R(s, c_1), c_2) = R(s, c_1 \circ c_2)$$

This shows that the reduction function satisfies associativity in the context of command composition.

## Proof of Commutativity

For commutativity under specific conditions where commands are commutative:

Let $s \in S$, $c_1, c_2 \in C$. We need to show that:
$$R(s, c_1 \circ c_2) = R(s, c_2 \circ c_1)$$

1. **Apply Command $c_1$ and then $c_2$**:
   $$R(s, c_1) = (s_1, ev_1, ef_1)$$
   $$R(s_1, c_2) = (s_2, ev_2, ef_2)$$
   where $s_2$ is the state resulting from applying $c_2$ to $s_1$, $ev_2$ are the events generated, and $ef_2$ are the
   effects generated.

2. **Apply Command $c_2$ and then $c_1$**:
   $$R(s, c_2) = (s_1', ev_1', ef_1')$$
   $$R(s_1', c_1) = (s_2', ev_2', ef_2')$$
   where $s_2'$ is the state resulting from applying $c_1$ to $s_1'$, $ev_2'$ are the events generated, and $ef_2'$ are
   the effects generated.

Since $c_1$ and $c_2$ are commutative (i.e., $c_1 \circ c_2 = c_2 \circ c_1$), the states, events, and effects should be
the same:
$$(s_2, ev_1 \cup ev_2, ef_1 \cup ef_2) = (s_2', ev_1' \cup ev_2', ef_1' \cup ef_2')$$

Thus, we have:
$$R(s, c_1 \circ c_2) = R(s, c_2 \circ c_1)$$

This demonstrates the commutativity of the reduction function under the specific condition of commutative commands.

## Conclusion

We have successfully proved that the reduction function $R$ satisfies both associativity and commutativity under the
given conditions.
This ensures that the reduction function behaves predictably and consistently when applying commands in different
sequences, which is essential for ensuring the correctness and reliability of the system.

The associativity property ensures that the order in which commands are applied does not affect the final state, events,
and effects, while the commutativity property ensures that commands can be applied in any order without affecting the
result under specific conditions.
These properties provide a solid foundation for ensuring the correctness and reliability of the system, influencing its
design and maintenance.

# Comparison with popular patterns

## Model-View-Controller (MVC)

- **Structure**: MVC separates data (Model) from the UI (View), with a Controller mediating between them.

- **Contrast**: In **Reduce & Conquer**, the `Feature` and `Reducer` replace both the Model's state logic and the
  Controller's orchestration.

- **Advantage**: Unlike MVC, where controllers often become "massive" and maintain mutable state, the `Reducer` is a
  stateless functional interface, making the logic predictable and easy to test in isolation.

## Model-View-ViewModel (MVVM)

- **Structure**: MVVM relies on data binding and ViewModels to expose state to the UI.

- **Contrast**: While a `Feature` can be used inside a ViewModel, it is not bound to the UI lifecycle.

- **Advantage**: **Reduce & Conquer** provides a more rigid contract. MVVM often leads to fragmented logic where
  multiple functions mutate the state directly. Here, state can only change through a formal `Transition` emitted by the
  `Reducer`.

## Model-View-Intent (MVI) & Redux

- **Structure**: These patterns use a unidirectional data flow (UDF) where "Intents" or "Actions" update a global or
  local store.

- **Contrast**: **Reduce & Conquer** refines the MVI concept by strictly separating **Events** (one-time notifications)
  from **Effects** (asynchronous operations).

- **Advantage**: Redux often struggles with side effect "middleware" (like Thunk or Saga) which can be verbose. In
  **Reduce & Conquer**, the `Effect` system is built-in and type-safe, using Coroutines and `AtomicFU` to manage
  lifecycle and cancellation without external plugins.

## The Elm Architecture (TEA)

- **Structure**: TEA uses a `Model`, `Update`, and `Msg` cycle, famous for its reliability.

- **Contrast**: **Reduce & Conquer** is essentially a JVM/Kotlin adaptation of TEA, but optimized for the modern
  Coroutine-based ecosystem.

- **Advantage**: It introduces the `Effect.Stream` and `Effect.Action` types, which allow for native handling of Flow
  and suspend functions directly within the architecture, something TEA achieves through "Commands" but with less native
  support for reactive streams.

## Actor Model

- **Structure**: Actors are independent, concurrent computational units that communicate via asynchronous message
  passing. Each actor maintains its own private state and processes messages sequentially from its "mailbox."

- **Contrast**: A `Feature` operates similarly to an Actor: it encapsulates state, processes messages (`Command`) via a
  `Channel` (serving as the mailbox), and isolates its internal logic. However, **Reduce & Conquer** provides a more
  formal mathematical structure with explicit state transitions and a dedicated side effect system built directly into
  the architecture.

- **Advantage**: Unlike traditional Actor frameworks (like Akka) which require complex setup and explicit supervision
  hierarchies, **Reduce & Conquer** is lightweight and leverages Kotlin-native structured concurrency (
  `Channel.UNLIMITED` +
  `scan` operator) to guarantee thread-safe sequential processing. Crucially, while Actors often scatter business logic
  across various message handlers, **Reduce & Conquer** centralizes all transition logic in a pure `Reducer` function —
  following the mathematical model:
  $$S \times C \rightarrow (S, Ev, Ef)$$
  This makes the system deterministic, easier to debug, and far more testable than the often unpredictable emergent
  behavior of raw Actor systems.

## Finite State Machines (FSM)

- **Structure**: An FSM moves between a finite number of states based on inputs.

- **Contrast**: The `Reducer` is a mathematical implementation of a State Transition Function:
  $S \times C \rightarrow (S, Ev, Ef)$.

- **Advantage**: While many FSM libraries are purely synchronous, **Reduce & Conquer** is an "Async-FSM." It allows
  transitions to trigger long-running operations (`Effect.Stream`) that eventually feed back into the machine as new
  commands, making it ideal for complex protocols like OAuth handshakes or multi-step file processing.

## Command Query Responsibility Segregation (CQRS)

- **Structure**: CQRS separates the models for reading and writing data.

- **Contrast**: The `execute(Command)` method represents the "Write" side, while the `StateFlow` represents the "Read"
  side (the projection).

- **Advantage**: By using a `Reducer`, the "Write" logic is centralized and deterministic. The "Read" side is always a
  consistent snapshot of the state, ensuring that the UI or any consuming service never sees an intermediate or
  corrupted state.

## Event-Driven Architecture (EDA)

- **Structure**: Systems react to a stream of events.

- **Contrast**: **Reduce & Conquer** treats every input as a `Command` and can produce a stream of `Event` objects for
  external subscribers.

- **Advantage**: It provides "Structured Reactivity." Instead of a chaotic web of event listeners, every event is a
  byproduct of a state transition, providing a clear audit trail of why an event was fired.

# Clean Architecture

**Clean Architecture** is a software design pattern that separates the application's business logic into layers, each
with its own responsibilities.

The main idea is to create a clear separation of concerns, making it easier to maintain, test, and scale the system.

```mermaid
graph LR
    subgraph "Presentation Layer"
        View["View"] --> Feature["Feature"]
        Feature["Feature"] --> Reducer["Reducer"]
    end

    subgraph "Domain Layer"
        UseCase["Use Case"] --> Repository["Repository"]
        UseCase["Use Case"] --> Entity["Entity"]
    end

    subgraph "Infrastructure Layer"
        direction TB
        Dao["DAO"] --> Database["Database"]
        Service["Service"] --> FileSystem["File System"]
        Service["Service"] --> NetworkClient["Network Client"]
    end

    Reducer --> UseCase
    Repository --> Dao
    Repository --> Service
```

**Clean Architecture** can be represented as follows:

```kotlin
View(
    Feature(           // Manages Events (notifications) and Effects (side operations)
        Reducer(       // Produces State, Events, and Effects
            UseCase(
                Repository(
                    Service
                )
            )
        )
    )
)
```

> [!TIP]
> Organize your package structure by overall model or functionality rather than by purpose.
> This type of architecture is called **"screaming"**.

## The architecture is composed of the following layers:

### Entities

Representing the business domain, such as users, products, or orders.

### Use Cases

Defining the actions that can be performed on the entities, such as logging in, creating an order, or updating a user.

### Interface Adapters

Handling communication between the application and external systems, such as databases, networks, or file systems.

### Frameworks and Drivers

Providing the necessary infrastructure for the application to run, such as web servers, databases, or operating systems.

As a general-purpose pattern, `Reduce & Conquer` can be used to implement the `Presentation` Layer, coordinate
`Use Cases`, or manage state in other layers of `Clean Architecture`.

## Working with data flows

> [!NOTE]
> Although this example uses Jetpack Compose, the Feature can be easily consumed by any Flow-based system (CLI, Ktor
> server-side, etc.)

Version 3.0.0 simplifies working with data flows through the `Effect.Stream` type. The `BaseFeature` automatically
manages the lifecycle of flow collections.

```kotlin
data class User(val id: String)

interface UserRepository {
    fun observeUsers(): Flow<List<User>>
}

data class UserState(val users: List<User> = emptyList())

sealed interface UserCommand {
    data object LoadUsers : UserCommand

    data class UpdateUsers(val users: List<User>) : UserCommand

    data class HandleError(val throwable: Throwable) : UserCommand
}

sealed interface UserEvent {
    data class NotifyError(val message: String) : UserEvent
  
    data class ShowSuccess(val message: String) : UserEvent
}

class UserReducer(
    private val userRepository: UserRepository,
) : Reducer<UserState, UserCommand, UserEvent> {
    override fun reduce(state: UserState, command: UserCommand): Transition<UserState, UserEvent> {
        return when (command) {
            UserCommand.LoadUsers -> transition(state)
                .effect(
                    stream(
                        key = "users_flow",
                        flow = userRepository.observeUsers().map(UserCommand::UpdateUsers),
                        fallback = { throwable -> UserCommand.HandleError(throwable) }
                    )
                )

            is UserCommand.UpdateUsers -> transition(state.copy(users = command.users))
                .event(UserEvent.ShowSuccess("Users updated"))

            is UserCommand.HandleError -> transition(state)
                .event(UserEvent.NotifyError("Error: ${command.throwable.message}"))
        }
    }
}

@Composable
fun UserScreen(feature: Feature<UserState, UserCommand, UserEvent>) {
    val state by feature.state.collectAsState()

    LaunchedEffect(Unit) {
        feature.execute(UserCommand.LoadUsers)
        
        feature.events.collect { event ->
            when (event) {
                is UserEvent.NotifyError -> {
                    // Show error notification
                    showSnackbar(event.message)
                }
            }
        }
    }

    // UI rendering...
}
```

## Testing

It is assumed that all the important logic is contained in the `Reducer`, which means that the testing pipeline can be
roughly represented as follows:

```kotlin
val reducer = MyReducer()

val (actualState, actualEvents, actualEffects) = reducer.reduce(initialState, command)

assertEquals(expectedState, actualState)
assertEquals(expectedEvents, actualEvents)
assertEquals(expectedEffects, actualEffects)
```

# Proof of concept

A cross-platform Pokédex application built using the Compose Multiplatform UI Framework.

```mermaid
graph TD
    subgraph "Use Case"
        GetMaxAttributeValue["Get Max Attribute Value"]
        GetDailyPokemon["Get Daily Pokemon"]
        GetPokemons["Get Pokemons"]
        InitializeFilters["Initialize Filters"]
        GetFilters["Get Filters"]
        SelectFilter["Select Filter"]
        UpdateFilter["Update Filter"]
        ResetFilter["Reset Filter"]
        ResetFilters["Reset Filters"]
        CardsReducer["Cards Reducer"]
        ChangeSort["Change Sort"]
    end

    subgraph "Navigation"
        NavigationView["Navigation View"] --> NavigationFeature["Navigation Feature"]
        NavigationFeature["Navigation Feature"] --> NavigationReducer["Navigation Reducer"]
    end

    NavigationReducer["Navigation Reducer"] --> NavigationCommand["Navigation Command"]
    NavigationCommand["Navigation Command"] --> DailyView["Daily View"]
    NavigationCommand["Navigation Command"] --> PokedexView["Pokedex View"]

    subgraph "Daily"
        DailyView["Daily View"] --> DailyFeature["Daily Feature"]
        DailyFeature["Daily Feature"] --> DailyReducer["Daily Reducer"]
    end

    DailyReducer["Daily Reducer"] --> GetMaxAttributeValue["Get Max Attribute Value"]
    DailyReducer["Daily Reducer"] --> GetDailyPokemon["Get Daily Pokemon"]

    subgraph "Pokedex"
        PokedexView["Pokedex View"] --> PokedexFeature["Pokedex Feature"]
        PokedexFeature["Pokedex Feature"] --> PokedexReducer["Pokedex Reducer"]
        PokedexReducer["Pokedex Reducer"] --> CardsReducer["Cards Reducer"]
        PokedexReducer["Pokedex Reducer"] --> FilterReducer["Filter Reducer"]
        PokedexReducer["Pokedex Reducer"] --> SortReducer["Sort Reducer"]
    end

    PokedexReducer["Pokedex Reducer"] --> CardsReducer["Cards Reducer"]
    CardsReducer["Cards Reducer"] --> GetMaxAttributeValue["Get Max Attribute Value"]
    CardsReducer["Cards Reducer"] --> GetPokemons["Get Pokemons"]
    PokedexReducer["Pokedex Reducer"] --> FilterReducer["Filter Reducer"]
    FilterReducer["Filter Reducer"] --> InitializeFilters["Initialize Filters"]
    FilterReducer["Filter Reducer"] --> GetFilters["Get Filters"]
    FilterReducer["Filter Reducer"] --> SelectFilter["Select Filter"]
    FilterReducer["Filter Reducer"] --> UpdateFilter["Update Filter"]
    FilterReducer["Filter Reducer"] --> ResetFilter["Reset Filter"]
    FilterReducer["Filter Reducer"] --> ResetFilters["Reset Filters"]
    PokedexReducer["Pokedex Reducer"] --> SortReducer["Sort Reducer"]
    SortReducer["Sort Reducer"] --> CardsReducer["Cards Reducer"]
    SortReducer["Sort Reducer"] --> ChangeSort["Change Sort"]
```

## Features

### Navigation feature functionality:

- Switching between Daily and Pokedex screens (functionality).

### Daily feature functionality:

- Get a Pokemon of the Day card based on the current day's timestamp

### Pokedex feature functionality:

- Getting a grid of Pokemon cards
- Search by name
- Multiple filtering by criteria
- Reset filtering
- Sorting by criteria

> [!NOTE]
> The Pokemon card is a double-sided rotating card where
> - front side contains name, image and type affiliation
> - back side contains name and hexagonal skill graph

## Libraries

- Jetpack Compose Multiplatform
- Kotlin Coroutines
- Kotlin Flow
- Kotlin AtomicFU
- Kotlin Datetime
- Kotlin Serialization Json
- Koin Dependency Injection
- Kotlin Multiplatform UUID
- Kotlin Coroutines Test
- Mockk

# More examples

- [Haskcore](https://github.com/numq/haskcore) - A modern, lightweight standalone desktop IDE with LSP support, built
  with Kotlin & Compose Desktop for Haskell development
- [StarsNoMore](https://github.com/numq/StarsNoMore) - An application for getting a summary of statistics and traffic of
  a user's GitHub repositories
- [Klarity](https://github.com/numq/Klarity) - Jetpack Compose Desktop media player library demonstration (example)
  project
- [camera-capture](https://github.com/numq/camera-capture) - Part of a project (mobile application) that provides the
  ability to take pictures with a smartphone camera and use them in the ComfyUI workflow
- [compose-desktop-media-player](https://github.com/numq/compose-desktop-media-player) - Examples of implementing a
  media (audio/video) player for Jetpack Compose Desktop using various libraries