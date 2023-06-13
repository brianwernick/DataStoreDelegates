package com.devbrackets.android.datastore.converter.crypto.wrapper

import com.devbrackets.android.datastore.converter.crypto.spec.TransformationSpec

@Suppress("ArrayInDataClass")
internal data class WrappedMessage(
  /**
   * The Initialization Vector (IV) used when constructing the cryptographic
   * cipher to encrypt and decrypt the message (see [wrappedMessage])
   */
  val initializationVector: ByteArray,

  /**
   * The [TransformationSpec] used for the encryption of the message stored in
   * [wrappedMessage].
   */
  val transformationSpec: TransformationSpec,

  /**
   * The value that is sent for storage which includes the encrypted message,
   * and the associated metadata such as the [initializationVector] and
   * [transformationSpec]. When decrypting the message you will need to read
   * starting from the [messageOffset]
   */
  val wrappedMessage: ByteArray,

  /**
   * The offset in the [wrappedMessage] where the encrypted message actually
   * starts.
   */
  val messageOffset: Int
)