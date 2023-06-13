package com.devbrackets.android.datastore.converter.core

import com.devbrackets.android.datastore.converter.ValueConverter
import kotlin.reflect.KClass

/**
 * Converts between an [Enum] and a [String] to store the
 * [Enum] in preferences. The [String] value represents
 * the `name` of the [Enum]
 */
class EnumValueConverter<T: Enum<T>>(
  private val enumClass: KClass<T>,
  private val errorHandler: ErrorHandler<T> = DefaultErrorHandler()
): ValueConverter<T, String> {
  override fun toConverted(originalValue: T): String {
    return originalValue.name
  }

  override fun toOriginal(convertedValue: String): T {
    return enumClass.java.enumConstants?.first {
      it.name.equals(convertedValue, true)
    } ?: errorHandler.onUnknownName(convertedValue, enumClass)
  }

  /**
   * Defines how [EnumValueConverter] errors should be handled.
   */
  fun interface ErrorHandler<T: Enum<T>> {
    /**
     * Called when a name (stored value) doesn't map to a defined Enum value for [enumClass].
     * This can happen if the name of a value is changed after it was stored.
     *
     * @param name The name for the Enum value that can't be automatically mapped
     * @param enumClass the [KClass] representing the destination Enum
     * @return The Enum value to use for the given [name]
     */
    fun onUnknownName(name: String, enumClass: KClass<T>): T
  }

  /**
   * An implementation of [ErrorHandler] that throws an [IllegalArgumentException] when
   * any error occurs.
   */
  class DefaultErrorHandler<T: Enum<T>>: ErrorHandler<T> {
    override fun onUnknownName(name: String, enumClass: KClass<T>): T {
      throw IllegalArgumentException("No Enum value of \"$name\" can be found for ${enumClass.simpleName}")
    }
  }
}