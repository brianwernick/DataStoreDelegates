package com.devbrackets.android.datastore.converter.crypto

import com.devbrackets.android.datastore.converter.crypto.cipher.CipherProvider
import com.devbrackets.android.datastore.converter.crypto.key.SecretKeyProvider
import com.devbrackets.android.datastore.converter.crypto.spec.TransformationSpec
import com.devbrackets.android.datastore.converter.crypto.spec.TransformationSpecProvider
import com.devbrackets.android.datastore.converter.crypto.wrapper.MessageWrapper
import com.devbrackets.android.datastore.converter.crypto.wrapper.WrappedMessage
import java.security.KeyStore

class DefaultValueEncryptor internal constructor(
  private val cipherProvider: CipherProvider,
  private val messageWrapper: MessageWrapper,
  private val transformationSpecProvider: TransformationSpecProvider
) : ValueEncryptor {
  override fun encrypt(value: ByteArray): ByteArray {
    return encrypt(value, transformationSpecProvider.getSupportedSpec())
  }

  override fun decrypt(encryptedValue: ByteArray): ByteArray {
    return decrypt(messageWrapper.unwrap(encryptedValue))
  }

  private fun encrypt(value: ByteArray, transformationSpec: TransformationSpec): ByteArray {
    val cipher = cipherProvider.encryptionCipher(transformationSpec)
    val encryptedValue = cipher.doFinal(value)

    return messageWrapper.wrap(cipher.iv, transformationSpec, encryptedValue)
  }

  private fun decrypt(wrapper: WrappedMessage): ByteArray {
    val cipher = cipherProvider.decryptionCipher(wrapper.transformationSpec, wrapper.initializationVector)
    val messageLength = wrapper.wrappedMessage.size - wrapper.messageOffset

    return cipher.doFinal(wrapper.wrappedMessage, wrapper.messageOffset, messageLength)
  }

  class Builder(
    private val keyAlias: String
  ) {
    private var keyStore: KeyStore? = null

    /**
     * Defines the [KeyStore] to use for storing the encryption keys. If this isn't
     * defined then a [KeyStore] with the type `AndroidKeyStore` will be used.
     */
    fun setKeyStore(keyStore: KeyStore): Builder {
      this.keyStore = keyStore
      return this
    }

    fun build(): DefaultValueEncryptor {
      val actualKeyStore = keyStore ?: KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
      }

      val cipherProvider = CipherProvider(
        keyAlias = keyAlias,
        keyProvider = SecretKeyProvider(actualKeyStore)
      )

      val transformationSpecProvider = TransformationSpecProvider()

      return DefaultValueEncryptor(
        cipherProvider = cipherProvider,
        messageWrapper = MessageWrapper(transformationSpecProvider),
        transformationSpecProvider = transformationSpecProvider
      )
    }
  }
}