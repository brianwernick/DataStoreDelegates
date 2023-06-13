package com.devbrackets.android.datastore.converter.crypto.cipher

import android.security.keystore.KeyProperties
import com.devbrackets.android.datastore.converter.crypto.key.SecretKeyProvider
import com.devbrackets.android.datastore.converter.crypto.spec.TransformationSpec
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec

internal class CipherProvider(
  private val keyAlias: String,
  private val keyProvider: SecretKeyProvider
) {
  fun encryptionCipher(transformationSpec: TransformationSpec): Cipher {
    return Cipher.getInstance(transformationSpec.toString()).apply {
      init(Cipher.ENCRYPT_MODE, keyProvider.getKey(keyAlias, transformationSpec))
    }
  }

  fun decryptionCipher(
    transformationSpec: TransformationSpec,
    initializationVector: ByteArray
  ): Cipher {
    val parameterSpec = getParameterSpec(transformationSpec, initializationVector)

    return Cipher.getInstance(transformationSpec.toString()).apply {
      init(Cipher.DECRYPT_MODE, keyProvider.getKey(keyAlias, transformationSpec), parameterSpec)
    }
  }

  private fun getParameterSpec(
    spec: TransformationSpec,
    initializationVector: ByteArray
  ): AlgorithmParameterSpec {
    return if (spec.blockMode.equals(KeyProperties.BLOCK_MODE_GCM, true)) {
      GCMParameterSpec(128, initializationVector)
    } else {
      IvParameterSpec(initializationVector)
    }
  }
}