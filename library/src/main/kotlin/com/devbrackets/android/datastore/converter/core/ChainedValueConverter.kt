package com.devbrackets.android.datastore.converter.core

import com.devbrackets.android.datastore.converter.ValueConverter

/**
 * A simple [ValueConverter] that supports chaining two separate
 * [ValueConverter]s.
 */
class ChainedValueConverter<ORIGINAL, INTERMEDIATE, CONVERTED>(
  private val t1: ValueConverter<ORIGINAL, INTERMEDIATE>,
  private val t2: ValueConverter<INTERMEDIATE, CONVERTED>
): ValueConverter<ORIGINAL, CONVERTED> {
  override fun toConverted(originalValue: ORIGINAL): CONVERTED {
    return t1.toConverted(originalValue).let {
      t2.toConverted(it)
    }
  }

  override fun toOriginal(convertedValue: CONVERTED): ORIGINAL {
    return t2.toOriginal(convertedValue).let {
      t1.toOriginal(it)
    }
  }
}