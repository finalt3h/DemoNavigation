# Pager Entry Direction Design

## Goal

Allow the user to choose which side a new pager page enters from. The selected direction persists locally and is restored when the app is reopened.

## Behavior

- Show a two-option selector labeled `Trái` and `Phải` above the pager.
- `Trái` is selected by default when no saved preference exists.
- Selecting `Trái` makes the incoming page move from the left edge toward the right.
- Selecting `Phải` makes the incoming page move from the right edge toward the left.
- Apply a changed selection immediately to subsequent pager transitions.
- Preserve the existing sink, scale, fade, shadow, and stacking behavior of the outgoing page.
- Preserve the existing multi-page fast-fling behavior.

## Persistence

Use Android `SharedPreferences` because this feature stores one small local value and does not need an additional dependency.

- Preference file: `pager_preferences`
- Preference key: `entry_direction`
- Stored values: `left` or `right`
- Missing or invalid values fall back to `left`.

## Components and Data Flow

- Represent the selection with a `PagerEntryDirection` enum.
- A small preference helper reads and writes the enum.
- `StackedHorizontalPagerScreen` loads the saved value once when composed.
- Selecting an option updates Compose state and writes the new value locally.
- The selected direction controls the sign of the pager's incoming movement while keeping gesture progress and outgoing-page transforms intact.

## Error Handling

- A missing preference uses `Trái`.
- An unknown stored value uses `Trái`.
- A failed synchronous preference write does not block the UI; the in-memory selection remains active for the current session.

## Testing

- Unit-test preference-value parsing and the default fallback.
- Unit-test transforms for incoming pages from both the left and right.
- Retain the fast-fling regression test.
- Compose UI test verifies both selector labels and the default selected direction.
- Run all debug unit tests and build the debug APK.
