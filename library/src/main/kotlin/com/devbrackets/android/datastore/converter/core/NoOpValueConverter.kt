package com.devbrackets.android.datastore.converter.core

import com.devbrackets.android.datastore.converter.ValueConverter

/**
 * A [ValueConverter] that acts as a pass-through without performing any
 * conversion.
 */
class NoOpValueConverter<T>: ValueConverter<T, T> {
  override fun toConverted(originalValue: T): T {
    return originalValue
  }

  override fun toOriginal(convertedValue: T): T {
    return convertedValue
  }
}