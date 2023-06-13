package com.devbrackets.android.datastore.delegate

import androidx.annotation.WorkerThread
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.devbrackets.android.datastore.converter.ValueConverter
import com.devbrackets.android.datastore.converter.core.NoOpValueConverter
import com.devbrackets.android.datastore.getOrDefault
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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
inline fun <reified T : Any?> DataStore<Preferences>.value(
  key: String,
  defaultValue: T
): ReadWriteProperty<Any, T> {
  return value(
    key = key,
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
inline fun <reified T, reified S> DataStore<Preferences>.value(
  key: String,
  defaultValue: T,
  converter: ValueConverter<T, S>
): ReadWriteProperty<Any, T> {
  return object : ReadWriteProperty<Any, T> {
    private val prefKey: Preferences.Key<S> = getPreferencesKey(key)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
      runBlocking {
        set(value)
      }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
      return runBlocking {
        data.first().getOrDefault(
          key = prefKey,
          defaultValue = defaultValue,
          converter = converter
        )
      }
    }

    private suspend fun set(value: T): T {
      this@value.edit { prefs ->
        prefs[prefKey] = converter.toConverted(value)
      }

      return value
    }
  }
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