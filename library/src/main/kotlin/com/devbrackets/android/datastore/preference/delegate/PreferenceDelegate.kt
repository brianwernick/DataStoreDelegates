package com.devbrackets.android.datastore.preference.delegate

import androidx.annotation.WorkerThread
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.devbrackets.android.datastore.converter.ValueConverter
import com.devbrackets.android.datastore.converter.core.NoOpValueConverter
import com.devbrackets.android.datastore.preference.getOrDefault
import com.devbrackets.android.datastore.value
import kotlin.properties.ReadWriteProperty

/**
 * A Kotlin delegate to read and write values in a [Preferences] [DataStore].
 * For example
 * ```kotlin
 * var theme by dataStore.value("uiTheme", Theme.FOLLOW_SYSTEM)
 * ```
 *
 * **NOTE:**
 * Reading a writing [DataStore] values can potentially be slower than expected and it is recommended
 * to perform reads/writes (get/set) on threads other than Main/UI.
 *
 * @param key The unique id used to store and retrieve the preference, typically this is a
 *            descriptive id such as "uiTheme"
 * @param defaultValue The value to return when [key] isn't contained by the [DataStore]
 */
@WorkerThread
inline fun <reified T> DataStore<Preferences>.value(
  key: String,
  defaultValue: T
): ReadWriteProperty<Any, T> {
  return value(
    key = getPreferencesKey(key),
    defaultValue = defaultValue,
    converter = NoOpValueConverter()
  )
}

/**
 * A Kotlin delegate to read and write values in a [Preferences] [DataStore] with value transformations.
 * For example
 * ```kotlin
 * var theme by dataStore.value("uiTheme", Theme.FOLLOW_SYSTEM, EnumValueConverter(Theme::class))
 * ```
 *
 * **NOTE:**
 * Reading a writing [DataStore] values can potentially be slower than expected and it is recommended
 * to perform reads/writes (get/set) on threads other than Main/UI.
 *
 * @param key The unique id used to store and retrieve the preference, typically this is a
 *            descriptive id such as "uiTheme"
 * @param defaultValue The value to return when [key] isn't contained by the [DataStore]
 * @param converter The [ValueConverter] to convert between the use type (e.g. an Enum)
 *                    and a format that the can be stored in preferences. Supported preference types
 *                    are `Int`, `Double`, `String`, `Boolean`, `Float`, and `Long`.
 */
@WorkerThread
inline fun <T, reified S> DataStore<Preferences>.value(
  key: String,
  defaultValue: T,
  converter: ValueConverter<T, S>
): ReadWriteProperty<Any, T> {
  return value(
    key = getPreferencesKey(key),
    defaultValue = defaultValue,
    converter = converter
  )
}

/**
 * A Kotlin delegate to read and write values in a [Preferences] [DataStore] with value transformations.
 * For example
 * ```kotlin
 * var theme by dataStore.value("uiTheme", Theme.FOLLOW_SYSTEM, EnumValueConverter(Theme::class))
 * ```
 *
 * **NOTE:**
 * Reading a writing [DataStore] values can potentially be slower than expected and it is recommended
 * to perform reads/writes (get/set) on threads other than Main/UI.
 *
 * @param key The [Preferences.Key] used to store and retrieve the preference
 * @param defaultValue The value to return when [key] isn't contained by the [DataStore]
 * @param converter The [ValueConverter] to convert between the use type (e.g. an Enum)
 *                    and a format that the can be stored in preferences. Supported preference types
 *                    are `Int`, `Double`, `String`, `Boolean`, `Float`, and `Long`.
 */
@WorkerThread
fun <T, S> DataStore<Preferences>.value(
  key: Preferences.Key<S>,
  defaultValue: T,
  converter: ValueConverter<T, S>
): ReadWriteProperty<Any, T> {
  val defaultStoreValue = converter.toConverted(defaultValue)

  return value(
    valueSetter = { store, value ->
      store.toMutablePreferences().apply {
        this[key] = value
      }
    },
    valueGetter = { store ->
      store.getOrDefault(
        key = key,
        defaultValue = defaultStoreValue
      )
    },
    converter = converter
  )
}

/**
 * Retrieves a [Preferences.Key] for the [key] associated with a value of type [T].
 * This supports all [Preferences.Key] types except the type `Set<String>`.
 *
 * @return A [Preferences.Key] for the type [T]
 * @throws IllegalArgumentException When the type [T] isn't a supported [Preferences.Key] type
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T> getPreferencesKey(key: String): Preferences.Key<T> {
  return when (T::class) {
    Int::class -> intPreferencesKey(key) as Preferences.Key<T>
    Double::class -> doublePreferencesKey(key) as Preferences.Key<T>
    String::class -> stringPreferencesKey(key) as Preferences.Key<T>
    Boolean::class -> booleanPreferencesKey(key) as Preferences.Key<T>
    Float::class -> floatPreferencesKey(key) as Preferences.Key<T>
    Long::class -> longPreferencesKey(key) as Preferences.Key<T>
    else -> {
      throw IllegalArgumentException("Preference type of \"${T::class}\" is not supported, must be one of Int, Double, String, Boolean, Float, or Long")
    }
  }
}