# Pager Entry Direction Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a persistent `Trái / Phải` selector that controls which edge the incoming pager page enters from.

**Architecture:** Introduce a small `PagerEntryDirection` model and `SharedPreferences` repository, then pass the selected direction into the pure pager transform function. The selected edge determines whether a gesture is pushing a page onto the stack or popping the top page off; pop is the visual inverse of push.

**Tech Stack:** Kotlin, Jetpack Compose, Compose Foundation Pager, Android SharedPreferences, JUnit 4, Compose UI tests.

---

### Task 1: Direction model and local persistence

**Files:**
- Create: `app/src/main/java/com/example/demonavigation/PagerEntryDirection.kt`
- Create: `app/src/test/java/com/example/demonavigation/PagerEntryDirectionTest.kt`

- [x] Write failing unit tests proving missing, invalid, `left`, and `right` stored values map to the expected enum.
- [x] Run `./gradlew testDebugUnitTest --tests com.example.demonavigation.PagerEntryDirectionTest` and confirm failure because the model does not exist.
- [x] Add `PagerEntryDirection` with `LEFT("left")`, `RIGHT("right")`, and a parser that defaults to `LEFT`.
- [x] Add `PagerEntryDirectionPreferences` using preference file `pager_preferences` and key `entry_direction`; reads use the parser and writes use `apply()`.
- [x] Re-run the focused test and confirm it passes.

### Task 2: Direction-aware pager transforms

**Files:**
- Modify: `app/src/main/java/com/example/demonavigation/StackedHorizontalPagerScreen.kt`
- Modify: `app/src/test/java/com/example/demonavigation/StackedPagerTransformTest.kt`

- [x] Add failing transform tests proving an incoming page starts on the left for `LEFT` and on the right for `RIGHT`.
- [x] Run `./gradlew testDebugUnitTest --tests com.example.demonavigation.StackedPagerTransformTest` and confirm the new tests fail.
- [x] Add `entryDirection` to `computeStackedPagerTransform`.
- [x] Counter the pager's default movement for the incoming page, then add a full-page visual offset whose sign follows `entryDirection`; preserve outgoing-page sink behavior and segmented fast flings.
- [x] Update existing test calls with the default `LEFT` direction and re-run focused transform tests.

### Task 3: Selector UI and state integration

**Files:**
- Modify: `app/src/main/java/com/example/demonavigation/StackedHorizontalPagerScreen.kt`
- Modify: `app/src/androidTest/java/com/example/demonavigation/StackedHorizontalPagerScreenTest.kt`

- [x] Update the Compose test to expect five pages plus visible `Trái` and `Phải` controls.
- [x] Add a two-option Material 3 selector above the pager with clear selected styling.
- [x] Load the saved direction once using the application context and `remember`.
- [x] On selection, update Compose state immediately and persist through `PagerEntryDirectionPreferences`.
- [x] Pass the selected direction into both z-index and graphics-layer transform calculations.

### Task 4: Verification

**Files:**
- Verify all changed production and test files.

- [x] Run `./gradlew testDebugUnitTest`.
- [x] Run `./gradlew assembleDebug`.
- [x] Run `git diff --check`.
- [x] Review the final diff against every requirement in the approved design spec.
