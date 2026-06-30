# Software Requirements Specification (SRS)

## "FauxCart" - E-Commerce Simulator

| | |
|---|---|
| **Document version** | 1.1 (Draft) |
| **Date** | 30 June 2026 |
| **Author** | Arsalan Peerzada |
| **Project** | FakeEcommerce (product name "FauxCart") |
| **Base package** | `com.arsalan.fake_ecommerce` |
| **Status** | Draft for review |

> Changes in v1.1: product renamed from "Dopamine" to "FauxCart"; added a full animation specification (Section 4.10) with cross-platform Lottie (Compottie) as a first-class requirement.

> This is a draft specification. Architectural decisions described here are recommendations and should be confirmed before implementation begins.

---

## 1. Introduction

### 1.1 Purpose

This SRS defines the requirements for **FauxCart**, a hyper-realistic e-commerce *simulator*. The application reproduces the full ritual of online shopping - browsing, micro-interactions, adding to a cart, completing a checkout form, and tracking a simulated delivery - **without ever performing a real financial transaction**.

The product is a behavioural tool: it delivers the psychological "dopamine hit" of anticipation and decision-making in order to curb impulse spending and late-night cravings. The completion experience deliberately reframes the moment around what the user *saved* rather than what they spent.

This document is the single reference for what the application must do (functional requirements), how well it must do it (non-functional requirements), and the architectural principles the codebase must uphold (SOLID, DRY, the ACID *spirit*, the Repository pattern, Clean MVVM and disciplined OOP).

### 1.2 Scope

In scope:

- A shared, cross-platform application (Android, iOS, Web/Wasm) built on Compose Multiplatform and Kotlin Multiplatform.
- A product catalogue of 200-300 browsable items, generated in-memory from a small set of detailed baseline products held in a local JSON resource.
- Cart management, a simulated checkout flow with mocked network latency, a simulated delivery tracker, and a savings/streak reward screen.
- Full survival of screen-rotation and configuration changes without data loss.

Out of scope:

- Any real payment processing, real network calls, server, or cloud database.
- User accounts, authentication, analytics back-ends, or persistence of personal data to a server.
- Real product imagery rights (placeholder image URLs are used).

### 1.3 Definitions and abbreviations

- **CMP** - Compose Multiplatform.
- **KMP** - Kotlin Multiplatform.
- **MVVM** - Model-View-ViewModel.
- **Repository** - the abstraction that mediates between the ViewModel and the data sources.
- **Dopamine site** - the viral genre FauxCart belongs to: a simulator that reproduces a rewarding ritual without its real-world consequence.
- **Compottie** - a Compose Multiplatform Lottie renderer (pure-Kotlin, no native bridge) that plays Lottie/dotLottie animations on Android, iOS and web/Wasm.
- **Lottie** - a JSON-based vector animation format exported from After Effects (via Bodymovin), played at runtime.
- **UiState** - the single immutable object describing everything the UI needs to render at a moment in time.
- **Catalogue inflation** - generating the full 200-300 item catalogue in memory from a small baseline set.

### 1.4 References

- Compose Multiplatform and Kotlin/Wasm documentation (JetBrains).
- AndroidX Lifecycle `ViewModel` (multiplatform artifact) for state retention.
- `kotlinx.serialization` for JSON parsing.
- Compottie - `alexzhirkevich/compottie` (https://github.com/alexzhirkevich/compottie). From v2.0 it ships a pure-Kotlin renderer working on Android, iOS, web and Wasm; this is the cross-platform Lottie engine FauxCart uses. (Native airbnb Lottie is Android-only in a shared module and is therefore explicitly rejected.)

These should be verified against their current official versions at implementation time; this document does not pin version numbers.

### 1.5 Overview

Section 2 gives the overall description and constraints. Section 3 lists functional and non-functional requirements. Section 4 defines the architecture and the coding principles to be enforced. Section 5 covers the data model and the JSON strategy. Section 6 gives acceptance criteria and a traceability summary.

---

## 2. Overall description

### 2.1 Product perspective

FauxCart is a **self-contained, 100% client-side** application. There is no server, no cloud database, and no real I/O beyond reading one bundled JSON resource file. All "network" behaviour (payment processing, delivery tracking) is *simulated* inside the shared layer using Kotlin Coroutines (`delay(...)`).

The application is one shared codebase compiled to three platforms. The business and presentation logic lives entirely in `commonMain`; each platform module contributes only a thin entry point and any unavoidable platform-specific glue.

```
FakeEcommerce/
+- shared/            <- all logic: Model, Repository, ViewModel, Compose UI
+- androidApp/        <- Android entry point (Activity)
+- iosApp/            <- iOS entry point (SwiftUI host + MainViewController)
+- webApp/            <- Web entry point (Wasm/JS)
```

### 2.2 Product functions (summary)

The application allows a user to: browse a large product catalogue with fluid scrolling; inspect product detail; add and remove items from a cart; review the cart total; complete a checkout form; watch a simulated payment and delivery-tracking sequence; and arrive at a reward screen that shows the amount "saved" and an ongoing streak. Each of these is expanded into testable requirements in Section 3.

### 2.3 User characteristics

The single user persona is a self-aware consumer who wants the ritual of shopping without the spend. They expect a polished, tactile, app-store-quality experience: smooth animations, responsive controls and immediate feedback. No technical knowledge is assumed.

### 2.4 Constraints

- **Zero infrastructure cost** - no server or paid services may be introduced.
- **Single shared module** - presentation and business logic must reside in `commonMain`; platform `*Main` source sets hold only entry points and `expect/actual` implementations.
- **No real transactions** - the application must never collect or transmit real payment data. Checkout fields are simulated and must not be persisted or sent anywhere.
- **Offline-first** - the application must function with no network connection.
- **Configuration-change resilience** - state must survive screen rotation and equivalent recompositions on all platforms.

### 2.5 Assumptions and dependencies

- The target toolchain supports the multiplatform `lifecycle-viewmodel-compose` artifact so that a single `ViewModel` definition in `commonMain` can survive Android configuration changes.
- Placeholder image URLs (e.g. `picsum.photos`) are reachable for image display, but the catalogue itself loads from a bundled resource and does not depend on the network.
- `kotlinx.serialization` is available for all three targets.

---

## 3. Specific requirements

Identifiers: **FR** = functional requirement, **NFR** = non-functional requirement. "Shall" denotes a mandatory requirement.

### 3.1 Functional requirements

**FR-1 Catalogue loading.** On first launch the application shall read the bundled `products.json` resource through the data layer (never directly from the UI) and parse it with `kotlinx.serialization`.

**FR-2 Catalogue inflation.** The application shall expand the baseline products into a catalogue of 200-300 unique items by appending a unique index suffix to each baseline `id`, producing distinct items that share baseline content. This shall happen once, in memory, in the data layer.

**FR-3 Browsing.** The user shall be able to scroll the full catalogue in a vertically scrolling, lazily-loaded list/grid that remains smooth at the full item count.

**FR-4 Product detail.** The user shall be able to open a product to view its name, price, full description and image.

**FR-5 Add to cart.** From the catalogue or detail view, the user shall be able to add an item to the cart with tactile feedback (spring-physics button animation).

**FR-6 Remove from cart / view cart.** The user shall be able to view the cart, see each line item and the running total, and remove items.

**FR-7 Checkout form.** The user shall be able to fill a checkout form (e.g. name, address, simulated card fields). Input is validated for completeness/format only; it is never transmitted or persisted.

**FR-8 Simulated payment.** On confirming checkout the application shall enter an "ordering" state, display progress, and simulate processing latency with a coroutine `delay`. No real transaction occurs.

**FR-9 Delivery tracking.** After simulated payment, the application shall animate a delivery-progress indicator from 0.0 to 1.0 representing the simulated rider's journey.

**FR-10 Reward / savings screen.** On completion the application shall present a rewarding screen reframing the event around savings - e.g. "You just saved $145 and avoided a compulsive purchase" - and update a running `totalSaved` figure and a streak.

**FR-11 Streak continuity.** The application shall maintain and display an ongoing streak that encourages repeat "saving" sessions.

**FR-12 State restoration on rotation.** All of the above state (catalogue scroll position, cart contents, checkout progress, delivery progress, total saved) shall survive screen rotation and configuration changes without loss or visible reset.

**FR-13 Add-to-cart animation.** Adding an item shall play a multi-stage, tactile animation: a spring-physics button press, a "fly-to-cart" motion of the product image toward the cart icon, and a cart-badge bump/count pop. (Section 4.10.1)

**FR-14 Scrolling motion.** The catalogue shall use motion-rich lazy scrolling: staggered item entrance as cards appear, subtle parallax/scale on the focused item, and a springy over-scroll/settle. Scrolling shall stay smooth at the full item count. (Section 4.10.2)

**FR-15 Detail-open transition.** Opening a product shall use a shared-element transition - the tapped card's image and title morph continuously into the detail screen rather than a hard cut - and reverse on back. (Section 4.10.3)

**FR-16 Checkout-flow motion.** The checkout shall animate between steps with `AnimatedContent`, animate field validation states, and play a Lottie progress/processing animation during the simulated payment, transitioning into an animated delivery-tracker (moving rider along a route, progress 0.0 to 1.0). (Section 4.10.4)

**FR-17 Congratulations / reward animation.** On completion the app shall play a celebratory Lottie set-piece (e.g. confetti / success burst) synchronised with an animated count-up of the amount saved and a streak-increment animation. This is the emotional payoff and shall be the highest-polish moment in the app. (Section 4.10.5)

### 3.2 Non-functional requirements

**NFR-1 Performance.** Catalogue scrolling shall remain visually smooth (target ~60 fps on a mid-range device) with the full 200-300 items, using lazy composition.

**NFR-2 Responsiveness / tactility.** Interactive controls shall provide immediate animated feedback using `Animatable`/`spring` physics; `AnimatedContent` and shared-element transitions shall drive screen and state changes. Motion is a core feature, not decoration - see the dedicated motion specification in Section 4.10. Every animation shall run at the platform's display refresh rate without jank.

**NFR-3 Portability.** A single shared codebase shall run on Android, iOS and Web (Wasm) with no per-platform business logic.

**NFR-4 Maintainability.** The codebase shall follow SOLID, DRY and clean MVVM (Section 4) so that a new screen or data source can be added without modifying unrelated classes.

**NFR-5 Reliability / consistency.** The `UiState` shall always be internally consistent (Section 4.4); partial or contradictory states (e.g. "ordering" while the cart is empty) shall not be representable in a way that reaches the UI.

**NFR-6 Privacy and data protection.** No real personal or payment data is collected or transmitted. Any text typed into simulated checkout fields stays in memory only and is discarded when the flow ends. (This keeps the product clear of personal-data handling obligations by design.)

**NFR-7 Offline operation.** Core flows (browse, cart, checkout simulation, reward) shall work fully offline.

**NFR-8 Accessibility.** Controls shall expose content descriptions and respect platform font scaling.

---

## 4. Architecture and engineering principles

This section is the contract for *how* the code is written. It maps each principle the brief requested onto concrete rules for this project.

### 4.1 Layered architecture (Clean MVVM)

Strict, one-directional dependency flow. The UI never touches the data source directly (this is an explicit requirement from the brief).

```
+-----------------------------------------------------------+
|  VIEW  (Compose @Composable, commonMain/.../ui)           |
|  - Stateless; renders UiState; emits user intents         |
+----------------------------↓ observes / ↑ intents---------+
|  VIEWMODEL  (commonMain/.../presentation)                 |
|  - Holds StateFlow<ShoppingUiState>; coroutine logic;     |
|    simulated latency; survives rotation                   |
+----------------------------↓ calls interface--------------+
|  REPOSITORY  (interface in /domain, impl in /data)        |
|  - Single source of truth for products & cart;            |
|    owns catalogue inflation                                |
+----------------------------↓-------------------------------+
|  DATA SOURCE  (commonMain/.../data)                       |
|  - Reads & deserialises products.json (kotlinx.serial)    |
+-----------------------------------------------------------+
```

Rule: a layer may depend only on the layer directly beneath it, and the ViewModel depends on the **Repository interface**, not its implementation.

### 4.2 SOLID

- **S - Single Responsibility.** Each class has one reason to change. The data source only reads/parses JSON; the repository only assembles and serves catalogue/cart data; the ViewModel only orchestrates state; composables only render. The reward-savings calculation lives in its own use case/helper, not inside a screen.
- **O - Open/Closed.** New behaviour is added by introducing new implementations (e.g. a second `ProductDataSource`) rather than editing existing classes. Repository and data source are interfaces precisely so they are open for extension, closed for modification.
- **L - Liskov Substitution.** Any `ProductRepository` implementation (real JSON-backed, or an in-memory fake for tests/previews) must be interchangeable without the ViewModel behaving incorrectly.
- **I - Interface Segregation.** Prefer small, focused interfaces (e.g. separate `CatalogRepository` and `CartRepository` rather than one fat interface) so consumers depend only on what they use.
- **D - Dependency Inversion.** High-level policy (ViewModel) depends on abstractions (repository interfaces). Concrete implementations are injected via the constructor. A lightweight manual DI container or a multiplatform DI library wires the graph at the app entry point - the ViewModel never constructs its own dependencies.

### 4.3 DRY

- A single `Product` model and a single `ShoppingUiState` are defined once in the domain layer and reused everywhere.
- Catalogue inflation logic exists in exactly one place (the repository/data layer), never duplicated per platform or per screen.
- Reusable composables (product card, primary button, price label) are factored out; formatting (currency, savings text) lives in one shared utility.
- Simulated latency is centralised behind one helper rather than scattered `delay(...)` calls.

### 4.4 ACID - interpretation for a client-side app

ACID is classically a property of *database transactions*. FauxCart has **no database**, so ACID does not apply literally. The requested principle is honoured in spirit by treating each cart/checkout mutation as an atomic state transition over the single in-memory source of truth:

- **Atomicity** - a checkout either completes fully (payment simulated, delivery started, savings recorded) or leaves state unchanged; there are no half-applied orders. State transitions produce a new complete `UiState` in one step.
- **Consistency** - `UiState` invariants always hold (e.g. `isOrdering` is only true with a non-empty cart; `deliveryProgress` stays within 0.0-1.0; `totalSaved` is monotonic). Illegal states are made unrepresentable through the model design and a single mutation path.
- **Isolation** - all state mutations funnel through the ViewModel and run on a single, confined coroutine context (via `MutableStateFlow.update { ... }`), so concurrent intents cannot interleave into a corrupt state.
- **Durability** - the only value that must outlive a session - `totalSaved` and the streak - is persisted to lightweight platform key-value storage via an `expect/actual` abstraction; transient cart/checkout state need not be durable.

This reinterpretation is noted explicitly so reviewers do not expect a transactional database that the architecture deliberately omits.

### 4.5 Repository pattern

A `ProductRepository` (and, if split per ISP, a `CartRepository`) interface sits in the domain layer. Its JSON-backed implementation in the data layer owns: loading the resource, deserialising it, and performing catalogue inflation. The ViewModel depends only on the interface. This directly satisfies the brief's requirement that "reading from the JSON file must not be like directly calling into the view classes".

### 4.6 OOP discipline

- Domain entities (`Product`) are immutable `data class`es; state is changed by producing copies, never by mutating fields.
- Encapsulation: mutable state (`MutableStateFlow`) is private inside the ViewModel; the View sees only a read-only `StateFlow`.
- Abstraction via interfaces (repository, data source, key-value store) with concrete implementations hidden behind them.
- Polymorphism through substitutable repository/data-source implementations (real vs fake).
- Composition over inheritance: behaviour is assembled from small collaborators rather than deep class hierarchies.

### 4.7 Screen rotation and configuration changes

Rotation is a first-class requirement (FR-12).

- The state holder is an AndroidX `ViewModel` (multiplatform `lifecycle-viewmodel-compose` artifact), obtained in composables via `viewModel { ... }`. On Android this survives configuration changes automatically, so cart, ordering and delivery state are retained across rotation without manual save/restore.
- Purely transient UI state (text-field contents, scroll position) is held with `rememberSaveable` so it also survives rotation and short-lived recomposition.
- Values that must survive full process death (`totalSaved`, streak) are persisted through the `expect/actual` key-value abstraction described in 4.4.
- The Compose UI is built responsively (using `WindowSizeClass`/adaptive layouts) so portrait and landscape both render correctly rather than merely preserving state.

### 4.8 Asynchronous and simulated network model

All simulated I/O uses Kotlin Coroutines launched in the ViewModel's `viewModelScope`. Payment and delivery are modelled as suspend functions with `delay(...)`; delivery progress is animated by emitting incremental `deliveryProgress` values. No thread-blocking calls are made on the main dispatcher.

### 4.9 Proposed package layout (`commonMain`)

```
com.arsalan.fake_ecommerce
+- domain/
|   +- model/        Product, ShoppingUiState
|   +- repository/   ProductRepository, CartRepository (interfaces)
|   +- usecase/      InflateCatalog, CalculateSavings (optional)
+- data/
|   +- source/       ProductDataSource (reads products.json)
|   +- repository/   ProductRepositoryImpl (inflation + cart logic)
|   +- local/        KeyValueStore (expect) for totalSaved/streak
+- presentation/
|   +- ShoppingViewModel
+- ui/
|   +- catalog/, detail/, cart/, checkout/, reward/   (composables)
|   +- components/   reusable cards, buttons, price labels
|   +- theme/
+- di/               manual DI / dependency wiring
+- App.kt            root composable (nav + scaffolding)
```

The existing wizard placeholders (`Greeting.kt`, `GreetingUtil.kt`) will be removed when implementation starts.

### 4.10 Motion specification (core feature)

Motion is the product. FauxCart's entire value - the "dopamine hit" - is delivered through animation, so this section is a hard requirement set, not a nice-to-have. Two complementary engines are used, both fully cross-platform (Android, iOS, web/Wasm):

- **Compottie** for *set-piece* vector animations authored as Lottie/dotLottie JSON (the celebration burst, the processing spinner, the delivery rider, empty/success states). Lottie is chosen so designers can drop in After Effects-quality motion without hand-coding it, and Compottie's pure-Kotlin renderer plays the same JSON identically on all three targets.
- **Native Compose animation APIs** for *interaction* motion that must track gestures and state in real time: `Animatable`, `animate*AsState`, `updateTransition`, `spring`/`tween`/`keyframes`, `AnimatedContent`, `AnimatedVisibility`, and `SharedTransitionLayout` for shared-element morphs.

General motion principles: prefer spring physics over fixed-duration tweens for anything the user touches (natural, interruptible); keep durations in the 150-400 ms range for UI transitions; every transition must be reversible and interruptible (no janky "wait for the animation to finish"); respect the platform reduce-motion / accessibility setting by substituting a quick fade.

A single reusable `MotionTokens`/animation-spec object centralises spring stiffness, damping and durations (DRY) so the whole app feels coherent.

**4.10.1 Add to cart.** Sequence: (1) button compresses via `spring` on press and rebounds on release; (2) a copy of the product image animates along a curved path from the card to the cart icon using `Animatable` offset + scale (the "fly to cart"); (3) the cart badge bumps and the count pops with an overshoot spring; (4) optional light haptic on supported platforms. Adding must remain responsive even if the user taps several items quickly (animations must overlap, not queue).

**4.10.2 Scrolling.** `LazyColumn`/`LazyVerticalGrid` with: staggered entrance (`animateItem` placement + fade/slide as items first appear), subtle scale/elevation change on the centred item for depth, and a soft spring settle on fling. Performance budget per NFR-1: smooth at the full 200-300 items; heavy per-frame work is avoided and images are loaded asynchronously.

**4.10.3 Opening a detail page.** A `SharedTransitionLayout` morphs the tapped card's image and title into their positions on the detail screen; surrounding content fades/slides in with a short stagger. Back navigation reverses the same shared-element transition so the image returns to its grid slot. No hard cut between list and detail.

**4.10.4 Checkout.** Steps (cart → form → processing → tracking) transition via `AnimatedContent` with directional slide+fade. Form fields animate focus and validation (e.g. shake on invalid, tick on valid). The simulated payment shows a **Compottie** processing/loader animation driven by the `isOrdering` state. The delivery tracker is a **Compottie** rider animation whose progress is bound to `deliveryProgress` (0.0-1.0), accompanied by a native animated route/progress bar.

**4.10.5 Congratulations / reward.** The highest-polish moment. On completion: a full-screen **Compottie** celebration (confetti / success burst) plays once; simultaneously the saved amount animates as a count-up (`Animatable` from 0 to the saved value with easing) and the streak counter increments with a spring pop. Copy reinforces the reframing ("You just saved $145 and avoided a compulsive purchase. Keep the streak going!"). The screen settles into a calm resting state with a clear call to start another session.

**Asset strategy.** Lottie JSON/dotLottie files are bundled under `composeResources` (or fetched via Compottie's URL spec where a remote source is acceptable) and listed as project assets. Sourcing the Lottie files (LottieFiles or custom After Effects exports) is a design task to schedule before the celebration and tracker screens are built; until final assets exist, placeholder Lottie files are used so the integration can be developed and tested.

---

## 5. Data requirements

### 5.1 Domain model

```kotlin
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String
)

data class ShoppingUiState(
    val catalogItems: List<Product> = emptyList(),
    val cartItems: List<Product> = emptyList(),
    val isOrdering: Boolean = false,
    val deliveryProgress: Float = 0f,   // 0.0 .. 1.0
    val totalSaved: Double = 0.0
)
```

A separate serialization DTO may mirror `Product` for `kotlinx.serialization` if it is preferable to keep the domain model free of annotations (a clean-architecture nicety, not mandatory).

### 5.2 Source data and inflation

The baseline products live in `shared/src/commonMain/composeResources/files/products.json` (the dataset supplied in the brief). The repository inflates them to the target scale in memory, for example:

```kotlin
val base = dataSource.loadCatalog()              // ~20 detailed baseline items
val catalog = List(COPIES) { index ->
    base.map { it.copy(id = "${it.id}_$index") }
}.flatten()                                       // 200-300 unique items
```

`COPIES` is chosen so the final count lands in the 200-300 range. Inflation is the repository's responsibility and is unit-testable in isolation.

### 5.3 Persistence

Only `totalSaved` and the streak are persisted, through a small `expect/actual` key-value store (Android `SharedPreferences`/DataStore, iOS `NSUserDefaults`, Web `localStorage`). No other data is persisted; no personal or simulated-payment input is ever written to disk or transmitted.

---

## 6. Acceptance criteria and traceability

The build is considered to meet this specification when:

1. A single `commonMain` codebase compiles and runs on Android, iOS and Web (Wasm). *(NFR-3)*
2. The catalogue shows 200-300 items, all with distinct IDs, loaded via the repository - with no JSON access anywhere in the UI layer. *(FR-1, FR-2, FR-4.5)*
3. Browsing the full catalogue stays smooth via lazy composition. *(FR-3, NFR-1)*
4. Add-to-cart, cart review/total, and item removal all work with animated feedback. *(FR-5, FR-6, NFR-2)*
5. Checkout simulates latency, animates delivery 0.0 to 1.0, and reaches a savings/streak reward screen. *(FR-7, FR-8, FR-9, FR-10, FR-11)*
6. Rotating the device at any point - mid-browse, mid-cart, mid-checkout, mid-delivery - preserves all state with no reset. *(FR-12, NFR-5)*
7. The ViewModel depends only on repository interfaces; swapping in a fake repository requires no ViewModel change (verified by a unit test). *(SOLID-D, SOLID-L, 4.5)*
8. No real personal or payment data is collected, persisted or transmitted. *(NFR-6)*
9. All five signature animations are present and smooth on Android, iOS and web: add-to-cart fly + badge, staggered/springy scroll, shared-element detail open, animated checkout + Lottie processing/tracker, and the Lottie celebration with count-up and streak pop. Reduce-motion falls back to a fade. *(FR-13 to FR-17, NFR-2, 4.10)*

### 6.1 Verification approach

- Unit tests in `commonTest` for: catalogue inflation (count and ID uniqueness), savings calculation, and `UiState` transition invariants (Section 4.4) using a fake repository.
- Manual rotation testing on Android (the platform where configuration changes are most disruptive) plus visual checks on iOS and Web.

---

*End of SRS v1.1 (Draft). Open questions for confirmation: (a) exact target item count (e.g. 280 vs 300); (b) whether the savings figure is the cart total or a separate "would-have-spent" value; (c) source of the Lottie assets (off-the-shelf LottieFiles vs custom After Effects exports) for the celebration and delivery-tracker screens.*
