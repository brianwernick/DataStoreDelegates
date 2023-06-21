package com.devbrackets.android.datastore.converter.core

import com.devbrackets.android.datastore.converter.ValueConverter
import kotlin.reflect.KClass

/**
 * Converts between an [Enum] and a [String] to store the
 * [Enum] in preferences. The [String] value represents
 * the `name` of the [Enum]
 */
class EnumValueConverter<E: Enum<E>, T: E?>(
  private val enumClass: KClass<E>,
  private val errorHandler: ErrorHandler<E, T> = DefaultErrorHandler<E,T>()
): ValueConverter<T, String?> {
  override fun toConverted(originalValue: T): String? {
    return originalValue?.name
  }

  @Suppress("UNCHECKED_CAST")
  override fun toOriginal(convertedValue: String?): T {
    return convertedValue?.let { name ->
      enumClass.java.enumConstants?.firstOrNull {
        it.name.equals(name, true)
      } ?: errorHandler.onUnknownName(convertedValue, enumClass)
    } as T
  }

  /**
   * Defines how [EnumValueConverter] errors should be handled.
   */
  fun interface ErrorHandler<E: Enum<E>, T:E?> {
    /**
     * Called when a name (stored value) doesn't map to a defined Enum value for [enumClass].
     * This can happen if the name of a value is changed after it was stored.
     *
     * @param name The name for the Enum value that can't be automatically mapped
     * @param enumClass the [KClass] representing the destination Enum
     * @return The Enum value to use for the given [name]
     */
    fun onUnknownName(name: String?, enumClass: KClass<E>): T
  }

  /**
   * An implementation of [ErrorHandler] that throws an [IllegalArgumentException] when
   * any error occurs.
   */
  class DefaultErrorHandler<E: Enum<E>, T: E?>: ErrorHandler<E, T> {
    override fun onUnknownName(name: String?, enumClass: KClass<E>): T {
      throw IllegalArgumentException("No Enum value of \"$name\" can be found for ${enumClass.simpleName}")
    }
  }
}