package com.devbrackets.android.datastore.converter.crypto

import com.devbrackets.android.datastore.converter.ValueConverter

/**
 * A [ValueConverter] that handles encrypting the value when storing and
 * decrypting on retrieval. This should be combined with other [ValueConverter]s
 * to convert between the expected object and the [ByteArray] used for encryption.
 */
class EncryptedValueConverter(
  private val encryptor: ValueEncryptor = DefaultValueEncryptor.Builder("defaultEncryptorKey").build(),
  private val errorHandler: ErrorHandler = DefaultErrorHandler()
) : ValueConverter<ByteArray?, ByteArray?> {
  override fun toConverted(originalValue: ByteArray?): ByteArray? {
    if (originalValue == null) {
      return null
    }

    return try {
      encryptor.encrypt(originalValue)
    } catch (e: Exception) {
      errorHandler.onEncryptionError(e, originalValue)
    }
  }

  override fun toOriginal(convertedValue: ByteArray?): ByteArray? {
    if (convertedValue == null) {
      return null
    }

    return try {
      encryptor.decrypt(convertedValue)
    } catch (e: Exception) {
      errorHandler.onDecryptionError(e, convertedValue)
    }
  }

  /**
   * Defines how [EncryptedValueConverter] errors should be handled during
   * encryption and decryption
   */
  interface ErrorHandler {
    fun onEncryptionError(e: Exception, originalValue: ByteArray): ByteArray
    fun onDecryptionError(e: Exception, convertedValue: ByteArray): ByteArray
  }

  /**
   * An [ErrorHandler] that re-throws the exception encountered during encryption
   * or decryption.
   */
  class DefaultErrorHandler : ErrorHandler {
    override fun onEncryptionError(e: Exception, originalValue: ByteArray): ByteArray {
      throw e
    }

    override fun onDecryptionError(e: Exception, convertedValue: ByteArray): ByteArray {
      throw e
    }
  }
}