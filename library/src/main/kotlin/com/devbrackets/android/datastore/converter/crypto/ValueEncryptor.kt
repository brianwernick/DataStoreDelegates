package com.devbrackets.android.datastore.converter.crypto

/**
 * The contract used for encrypting and decrypting content. This is
 * used by the [EncryptedValueConverter] to encrypt and decrypt content
 * when converting between the incoming and outgoing values.
 */
interface ValueEncryptor {
  fun encrypt(value: ByteArray): ByteArray
  fun decrypt(encryptedValue: ByteArray): ByteArray
}