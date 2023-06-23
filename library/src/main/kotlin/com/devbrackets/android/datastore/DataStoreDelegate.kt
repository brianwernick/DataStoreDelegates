package com.devbrackets.android.datastore

import androidx.annotation.WorkerThread
import androidx.datastore.core.DataStore
import com.devbrackets.android.datastore.converter.ValueConverter
import com.devbrackets.android.datastore.converter.core.NoOpValueConverter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * A Kotlin delegate to read and write values in a [DataStore].
 * For example
 * ```kotlin
 * var theme by dataStore.value("uiTheme", Theme.FOLLOW_SYSTEM)
 * ```
 *
 * **NOTE:**
 * Reading a writing [DataStore] values can be slower than expected and it is recommended
 * to perform reads/writes (get/set) on threads other than Main/UI.
 *
 * @param valueGetter The function that handles retrieving the value from the [DataStore] of [DS]
 * @param valueSetter The function that handles setting the value in the [DataStore] of [DS]
 */
@WorkerThread
fun <T, DS> DataStore<DS>.value(
  valueSetter: suspend (store: DS, value: T) -> DS,
  valueGetter: suspend (store: DS) -> T
): ReadWriteProperty<Any, T> {
  return value(
    valueSetter = valueSetter,
    valueGetter = valueGetter,
    converter = NoOpValueConverter()
  )
}

/**
 * A Kotlin delegate to read and write values in a [DataStore].
 * For example
 * ```kotlin
 * var theme by dataStore.value("uiTheme", Theme.FOLLOW_SYSTEM)
 * ```
 *
 * **NOTE:**
 * Reading a writing [DataStore] values can be slower than expected and it is recommended
 * to perform reads/writes (get/set) on threads other than Main/UI.
 *
 * @param valueGetter The function that handles retrieving the value from the [DataStore] of [DS]
 * @param valueSetter The function that handles setting the value in the [DataStore] of [DS]
 * @param converter The [ValueConverter] to convert between the use type [T] (e.g. an Enum)
 *                    and a format that the can be stored in preferences [S]. Supported preference types
 *                    are `Int`, `Double`, `String`, `Boolean`, `Float`, and `Long`.
 */
@WorkerThread
fun <T, S, DS> DataStore<DS>.value(
  valueSetter: suspend (store: DS, value: S) -> DS,
  valueGetter: suspend (store: DS) -> S,
  converter: ValueConverter<T, S>,
): ReadWriteProperty<Any, T> {
  return object : ReadWriteProperty<Any, T> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
      runBlocking {
        this@value.updateData { store ->
          valueSetter(store, converter.toConverted(value))
        }
      }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
      return runBlocking {
        data.first().let {
          converter.toOriginal(valueGetter(it))
        }
      }
    }
  }
}