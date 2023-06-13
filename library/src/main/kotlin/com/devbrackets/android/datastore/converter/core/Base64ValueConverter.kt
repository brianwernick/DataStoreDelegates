package com.devbrackets.android.datastore.converter.core

import android.util.Base64
import com.devbrackets.android.datastore.converter.ValueConverter

/**
 * A [ValueConverter] that converts the incoming value to a Base64 string.
 */
class Base64ValueConverter : ValueConverter<ByteArray?, String?> {
  override fun toConverted(originalValue: ByteArray?): String? {
    return originalValue?.let {
      Base64.encodeToString(it, Base64.DEFAULT)
    }
  }

  override fun toOriginal(convertedValue: String?): ByteArray? {
    return convertedValue?.let {
      Base64.decode(it, Base64.DEFAULT)
    }
  }
}