package com.devbrackets.android.datastore.converter.core

import com.devbrackets.android.datastore.converter.ValueConverter
import java.nio.charset.Charset

/**
 * A [ValueConverter] that converts a [String] to a [ByteArray]. This
 * is useful when chaining with other converters that require a [ByteArray]
 * to operate such as the [com.devbrackets.android.datastore.converter.crypto.EncryptedValueConverter]
 *
 * @param charset The [Charset] to encode and decode the string with
 */
class StringBytesValueConverter(
  private val charset: Charset = Charsets.UTF_8
) : ValueConverter<String?, ByteArray?> {
  override fun toConverted(originalValue: String?): ByteArray? {
    return originalValue?.toByteArray(charset)
  }

  override fun toOriginal(convertedValue: ByteArray?): String? {
    return convertedValue?.let {
      String(it, charset)
    }
  }
}