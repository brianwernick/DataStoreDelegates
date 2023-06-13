package com.devbrackets.android.datastore.converter.crypto.key

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.devbrackets.android.datastore.converter.crypto.spec.TransformationSpec
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

internal class SecretKeyProvider(
  private val keyStore: KeyStore
) {
  /**
   * Retrieves the [SecretKey] to use for the specified [alias] and [transformationSpec].
   * This will create the [SecretKey] it it doesn't already exist.
   *
   * @param alias The alias to use for the [SecretKey] in the [KeyStore]. Note that some
   *              identifying information for the [transformationSpec] will be appended.
   * @param transformationSpec The [TransformationSpec] that should be associated with the
   *                           [SecretKey] and used by the Cipher to encrypt/decrypt data.
   */
  @Synchronized
  fun getKey(alias: String, transformationSpec: TransformationSpec): SecretKey {
    // The SecretKey dependents on the transformationSpec
    val keyAlias = "$alias-${transformationSpec.hashCode()}"

    if (keyStore.containsAlias(keyAlias)) {
      return getSecretKey(keyAlias)
    }

    return newSecretKey(keyAlias, transformationSpec)
  }

  private fun newSecretKey(alias: String, transformationSpec: TransformationSpec): SecretKey {
    val generator = KeyGenerator.getInstance(transformationSpec.algorithm, keyStore.provider).apply {
      val specPurpose = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
      val spec = KeyGenParameterSpec.Builder(alias, specPurpose)
        .setBlockModes(transformationSpec.blockMode)
        .setEncryptionPaddings(transformationSpec.padding)
        .build()

      init(spec)
    }

    return generator.generateKey()
  }

  private fun getSecretKey(alias: String): SecretKey {
    val entry = keyStore.getEntry(alias, null)
    if (entry !is KeyStore.SecretKeyEntry) {
      throw IllegalStateException("Expected a \"SecretKeyEntry\" but received \"${entry::class.simpleName}\"")
    }

    return entry.secretKey
  }
}