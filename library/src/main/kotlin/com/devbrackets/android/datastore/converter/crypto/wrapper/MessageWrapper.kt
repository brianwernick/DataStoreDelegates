package com.devbrackets.android.datastore.converter.crypto.wrapper

import com.devbrackets.android.datastore.converter.crypto.spec.TransformationSpec
import com.devbrackets.android.datastore.converter.crypto.spec.TransformationSpecProvider
import javax.crypto.Cipher

internal class MessageWrapper(
  private val transformationSpecProvider: TransformationSpecProvider
) {
  /**
   * Wraps the encrypted [message] with additional information about the Initialization Vector ([iv])
   * and [transformationSpec] used during the encryption process. This will be used during decryption to
   * ensure that we are using the correct.
   *
   * **NOTE:**
   * This duplicates the data from [message] which isn't efficient with large data-sets,
   * however it should be fine for the length of [message]s stored in Preferences.
   *
   * @param iv The Initialization Vector used when constructing the [Cipher] to encrypt and decrypt the [message]
   * @param transformationSpec The [TransformationSpec] used by the [Cipher] to encrypt and decrypt the [message]
   * @param message The encrypted value to wrap for storage
   * @return The [message] wrapped with information related to the [iv] and [transformationSpec]
   */
  fun wrap(
    iv: ByteArray,
    transformationSpec: TransformationSpec,
    message: ByteArray
  ): ByteArray {
    val prefix = buildPrefix(iv, transformationSpec)
    val totalSize = prefix.size + message.size

    return ByteArray(totalSize).apply {
      System.arraycopy(prefix, 0, this, 0, prefix.size)
      System.arraycopy(message, 0, this, prefix.size, message.size)
    }
  }

  fun unwrap(message: ByteArray): WrappedMessage {
    val wrapperVersion = message[0].toInt()
    if (wrapperVersion != 1) {
      throw IllegalArgumentException("Unable to unwrap message, unsupported version $wrapperVersion")
    }

    return unwrapV1(message)
  }

  /**
   * Builds the prefix information for an encrypted message that contains the [iv] and
   * [spec] information.
   *
   * **Version 1:**
   * The prefix information is stored in a [ByteArray] of the necessary values, starting
   * with the version number (Byte) and start positions (Int); followed by the [iv] and
   * [spec].
   *
   * @param iv The initialization vector for the Cipher used during the encryption process
   * @param spec The [TransformationSpec] used by the Cipher during the encryption process
   */
  private fun buildPrefix(
    iv: ByteArray,
    spec: TransformationSpec
  ): ByteArray {
    val transformation = spec.toString().encodeToByteArray()

    val versionByte = 1.toByte()
    val ivLength = iv.size.toByteArray()
    val transformationLength = transformation.size.toByteArray()

    val totalLength = iv.size + transformation.size + 9
    return ByteArray(totalLength).apply {
      // Headers (9 Bytes)
      set(0, versionByte)
      System.arraycopy(ivLength, 0, this, 1, 4)
      System.arraycopy(transformationLength, 0, this, 5, 4)

      // Content
      System.arraycopy(iv, 0, this, 9, iv.size)
      System.arraycopy(transformation, 0, this, 9 + iv.size, transformation.size)
    }
  }

  /**
   * Unwraps the [message] using the v1 wrapper method
   */
  private fun unwrapV1(message: ByteArray): WrappedMessage {
    // Headers
    val ivLength = message.toInt(1)
    val transformationLength = message.toInt(5)
    val headersLength = 9

    // Content
    val iv = ByteArray(ivLength).apply {
      System.arraycopy(message, headersLength, this, 0, ivLength)
    }

    val transformationStart = headersLength + ivLength
    val transformation = message.decodeToString(transformationStart, transformationStart + transformationLength)
    val spec = transformationSpecProvider.decodeSpec(transformation)

    val messageOffset = headersLength + ivLength + transformationLength

    return WrappedMessage(
      initializationVector = iv,
      transformationSpec = spec,
      wrappedMessage = message,
      messageOffset = messageOffset
    )
  }

  private fun Int.toByteArray(): ByteArray {
    return ByteArray(4).apply {
      this[0] = (this@toByteArray shr 0).toByte()
      this[1] = (this@toByteArray shr 8).toByte()
      this[2] = (this@toByteArray shr 16).toByte()
      this[3] = (this@toByteArray shr 24).toByte()
    }
  }

  private fun ByteArray.toInt(startPosition: Int): Int {
    return (this[startPosition + 3].toInt() shl 24) or
      (this[startPosition + 2].toInt() and 0xff shl 16) or
      (this[startPosition + 1].toInt() and 0xff shl 8) or
      (this[startPosition + 0].toInt() and 0xff)
  }
}