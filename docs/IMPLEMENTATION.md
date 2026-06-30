# FauxCart - Implementation Notes

Companion to `docs/SRS.md`. Summarises what was built, where things live, and the known follow-ups.

## Architecture (Clean MVVM, one shared module)

All logic lives in `shared/src/commonMain`. Strict one-way dependency flow:

```
ui (Compose)  ->  presentation (ShoppingViewModel)  ->  domain (interfaces)  <-  data (impls)
```

- `domain/model` - `Product`, `ShoppingUiState`, `Screen`, `CheckoutStage` (immutable).
- `domain/repository` - `ProductRepository`, `SavingsRepository` interfaces (ISP).
- `domain/usecase` - `SavingsCalculator`.
- `data/dto` - `ProductDto` (kotlinx.serialization wire model, mapped to domain).
- `data/source` - `ProductDataSource` + `JsonProductDataSource` (the only place that reads `products.json`).
- `data/repository` - `ProductRepositoryImpl` (catalogue inflation, cached) and `SavingsRepositoryImpl`.
- `data/local` - `SavingsStore` interface + `provideSavingsStore()` expect/actual (iOS uses NSUserDefaults; Android/web in-memory for now).
- `presentation` - `ShoppingViewModel` (single `StateFlow`, coroutine-simulated payment/delivery, survives rotation).
- `di` - `AppContainer` (manual composition root).
- `ui` - theme, motion tokens, reusable components, and the five screens.

The UI never touches the JSON; it depends only on the ViewModel, which depends only on repository interfaces (Dependency Inversion).

## Where the five signature animations live

- Add to cart - `ui/detail/DetailScreen.kt` (`FlyingChip` arc) + cart badge pop in `ui/FauxCartApp.kt`.
- Scrolling - `ui/catalog/CatalogScreen.kt` (`animateItem` staggered fade) + press scale in `ProductCard`.
- Detail open - `ui/FauxCartApp.kt` directional `AnimatedContent` + hero spring-scale in `DetailScreen`.
- Checkout - `ui/checkout/CheckoutScreen.kt` (`AnimatedContent` stages + Compottie processing/tracker bound to `deliveryProgress`).
- Congratulations - `ui/reward/RewardScreen.kt` (Compottie celebration + count-up + streak pop).

Lottie is rendered cross-platform by Compottie via `ui/components/LottieBox.kt`, which falls back to a native control if the asset is missing or reduce-motion is on. Placeholder Lottie files are in `composeResources/files/` (celebration/processing/delivery).

## Running

- Android: `./gradlew :androidApp:assembleDebug`
- Web (Wasm): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
- iOS: open `iosApp` in Xcode and run.

Rotation: the `ShoppingViewModel` survives configuration changes, so cart, ordering and delivery progress persist; text fields use `rememberSaveable`; the catalogue uses an adaptive grid so portrait and landscape both render.

## Known follow-ups (deliberate, not bugs)

- Library versions (`compottie` 2.0.0-rc04 - the newest on Maven Central for this artifact; `kotlinx-serialization-json` 1.9.0; `kotlinx-coroutines` 1.10.2). Note `compottie` rc04 was published against an older Kotlin; if the iOS/web (klib) targets complain about Kotlin version compatibility with Kotlin 2.4, either find a newer Compottie build or switch the Lottie set-pieces to the native-Compose fallback (a `LottieBox`-only change). Android (JVM) is unaffected.
- `LottieBox` is written against the Compottie 2.x API; if the resolved version differs, only that one file needs adjustment.
- Product imagery uses a deterministic gradient placeholder (`ui/components/ProductImage.kt`) so the app runs with zero extra dependencies. Drop in a multiplatform image loader (e.g. Coil 3 `coil-compose`) to load the real `imageUrl`s - it is a single-spot change in `ProductImage`.
- Durable savings persistence is implemented for iOS (NSUserDefaults); Android and web currently use the in-memory store (survives rotation, not process death). Android needs a Context-backed `DataStore`/`SharedPreferences`; web can wrap `localStorage`.
- The Lottie files are placeholders; replace with bespoke After Effects exports for production polish.

## Navigation & profile (update)

The app now has a floating bottom navigation bar (`ui/components/FauxBottomBar.kt`) with four tabs:
Home, Wishlist, Notifications (labelled "Alerts"), Profile. It hovers over the tab roots and slides
away during the Detail/Cart/Checkout/Reward flows.

- Nav model lives in `domain/model/Screen.kt` (`Tab` enum + `Screen.tab()` / `isTabRoot()` helpers).
- Home (`ui/home/HomeScreen.kt`) has category tabs across the top (All / Fragrance / Tech / Lifestyle).
- Wishlist (`ui/wishlist`) lists saved items; the heart toggle lives on the detail screen.
- Notifications (`ui/notifications`) are generated from state (streak, savings, recent orders, wishlist).
- Profile (`ui/profile`) has an avatar, editable username + email, items-ordered count, streak rewards,
  daily transactions and order history. Order history is recorded per checkout (`OrderRecord`,
  timestamped via the `util/Clock.kt` expect/actual `nowMillis`).
- Icons and the top-bar brand mark are hand-drawn with Canvas (`ui/components/FauxIcons.kt`) - no
  Material icon dependency.

Profile edits and order history are session-scoped (kept in the ViewModel, survive rotation);
streak and total saved still persist via the savings store.

## Tests

`shared/src/commonTest` covers catalogue inflation (count + id uniqueness + caching), the savings calculation, `ShoppingUiState` derivations, and the currency formatter. Run with `./gradlew :shared:allTests` (or the per-target test tasks).
