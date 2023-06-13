package com.devbrackets.android.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.devbrackets.android.datastore.converter.ValueConverter
import com.devbrackets.android.datastore.converter.core.NoOpValueConverter
import com.devbrackets.android.datastore.delegate.getPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

/**
 * A Kotlin delegate to retrieve a [SharedFlow] for values in a [Preferences] [DataStore].
 * For example
 * ```kotlin
 * val themeFlow = dataStore.sharedFlow("uiTheme", Theme.FOLLOW_SYSTEM, viewModelScope)
 * ```
 *
 * @param key The unique id used to store and retrieve the preference, typically this is a
 *            descriptive id such as "uiTheme"
 * @param defaultValue The value to return when [key] isn't contained by the [DataStore]
 * @param scope The [CoroutineScope] to use when constructing the [SharedFlow]
 * @param started The strategy that controls when sharing is started and stopped
 * @param replay The number of values replayed to new subscribers (cannot be negative, defaults to zero).
 */
inline fun <reified T> DataStore<Preferences>.sharedFlow(
  key: String,
  defaultValue: T,
  scope: CoroutineScope,
  started: SharingStarted = SharingStarted.WhileSubscribed(replayExpirationMillis = 0),
  replay: Int = 0
): SharedFlow<T> {
  return this.sharedFlow(
    key = key,
    defaultValue = defaultValue,
    converter = NoOpValueConverter(),
    scope = scope,
    started = started,
    replay = replay
  )
}

/**
 * A Kotlin delegate to retrieve a [SharedFlow] for values in a [Preferences] [DataStore].
 * For example
 * ```kotlin
 * val themeFlow = dataStore.sharedFlow("uiTheme", Theme.FOLLOW_SYSTEM, EnumValueConverter(Theme::class), viewModelScope)
 * ```
 *
 * @param key The unique id used to store and retrieve the preference, typically this is a
 *            descriptive id such as "uiTheme"
 * @param defaultValue The value to return when [key] isn't contained by the [DataStore]
 * @param converter The [ValueConverter] to convert between the use type (e.g. an Enum)
 *                    and a format that the can be stored in preferences. Supported preference types
 *                    are `Int`, `Double`, `String`, `Boolean`, `Float`, and `Long`.
 * @param scope The [CoroutineScope] to use when constructing the [SharedFlow]
 * @param started The strategy that controls when sharing is started and stopped
 * @param replay The number of values replayed to new subscribers (cannot be negative, defaults to zero).
 */
inline fun <reified T, reified S> DataStore<Preferences>.sharedFlow(
  key: String,
  defaultValue: T,
  converter: ValueConverter<T, S>,
  scope: CoroutineScope,
  started: SharingStarted = SharingStarted.WhileSubscribed(replayExpirationMillis = 0),
  replay: Int = 0
): SharedFlow<T> {
  return this.flow(
    key = key,
    defaultValue = defaultValue,
    converter = converter
  ).shareIn(
    scope = scope,
    started = started,
    replay = replay
  )
}

/**
 * A Kotlin delegate to retrieve a [Flow] for values in a [Preferences] [DataStore].
 * For example
 * ```kotlin
 * val themeFlow = dataStore.flow("uiTheme", Theme.FOLLOW_SYSTEM)
 * ```
 *
 * @param key The unique id used to store and retrieve the preference, typically this is a
 *            descriptive id such as "uiTheme"
 * @param defaultValue The value to return when [key] isn't contained by the [DataStore]
 */
inline fun <reified T> DataStore<Preferences>.flow(
  key: String,
  defaultValue: T
): Flow<T> {
  return flow(
    key = key,
    defaultValue = defaultValue,
    converter = NoOpValueConverter()
  )
}

/**
 * A Kotlin delegate to retrieve a [Flow] for values in a [Preferences] [DataStore].
 * For example
 * ```kotlin
 * val themeFlow = dataStore.flow("uiTheme", Theme.FOLLOW_SYSTEM, EnumValueConverter(Theme::class))
 * ```
 *
 * @param key The unique id used to store and retrieve the preference, typically this is a
 *            descriptive id such as "uiTheme"
 * @param defaultValue The value to return when [key] isn't contained by the [DataStore]
 * @param converter The [ValueConverter] to convert between the use type (e.g. an Enum)
 *                    and a format that the can be stored in preferences. Supported preference types
 *                    are `Int`, `Double`, `String`, `Boolean`, `Float`, and `Long`.
 */
inline fun <reified T, reified S> DataStore<Preferences>.flow(
  key: String,
  defaultValue: T,
  converter: ValueConverter<T, S>
): Flow<T> {
  val prefKey: Preferences.Key<S> = getPreferencesKey(key)

  return data.map { prefs ->
    prefs.getOrDefault(
      key = prefKey,
      defaultValue = defaultValue,
      converter = converter
    )
  }
}

/**
 * Retrieves the [Preferences] value associated with the [key],
 * if no value is defined for [key] in [Preferences] then the
 * [defaultValue] will be returned.
 *
 * @param key The [Preferences.Key] used to retrieve the [Preferences] value
 * @param defaultValue The value to return when [key] isn't defined in [Preferences]
 */
inline fun <reified T> Preferences.getOrDefault(
  key: Preferences.Key<T>,
  defaultValue: T
): T {
  return getOrDefault(
    key = key,
    defaultValue = defaultValue,
    converter = NoOpValueConverter()
  )
}

/**
 * Retrieves the [Preferences] value associated with the [key],
 * if no value is defined for [key] in [Preferences] then the
 * [defaultValue] will be returned.
 *
 * @param key The [Preferences.Key] used to retrieve the [Preferences] value
 * @param defaultValue The value to return when [key] isn't defined in [Preferences]
 * @param converter The [ValueConverter] used to convert between the stored type [S]
 *                  and the expected read/write type [T]
 */
inline fun <reified T, S> Preferences.getOrDefault(
  key: Preferences.Key<S>,
  defaultValue: T,
  converter: ValueConverter<T, S>
): T {
  if (contains(key)) {
    return get(key)?.let { converter.toOriginal(it) } as T
  }

  return defaultValue
}