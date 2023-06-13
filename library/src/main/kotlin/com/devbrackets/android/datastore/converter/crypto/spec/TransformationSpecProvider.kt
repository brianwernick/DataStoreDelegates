package com.devbrackets.android.datastore.converter.crypto.spec

import android.security.keystore.KeyProperties

internal class TransformationSpecProvider {
  private val locatedSpec by lazy {
    findSupportedSpec()
  }

  /**
   * Checks the system for supported cryptographic algorithms, blocks, and padding
   * modes and builds a [TransformationSpec] to use.
   *
   * @return A [TransformationSpec] that is supported by the system
   */
  fun getSupportedSpec(): TransformationSpec {
    return locatedSpec
  }

  fun decodeSpec(transformation: String): TransformationSpec {
    val segments = transformation.split('/')
    if (segments.size != 3) {
      throw IllegalArgumentException("Unable to decode the transformation \"$transformation\" due to an unexpected number of segments")
    }

    val algorithm = segments[0]
    val blockMode = segments[1]
    val padding = segments[2]

    return TransformationSpec(
      algorithm = algorithm,
      blockMode = blockMode,
      padding = padding
    )
  }

  private fun findSupportedSpec(): TransformationSpec {
    // TODO: Use Security.getProviders() to loop over available providers
    // and determine if the below is supported, if it isn't then we should
    // update to a supported algorithm, block, and padding transformation

    return TransformationSpec(
      algorithm = KeyProperties.KEY_ALGORITHM_AES,
      blockMode = KeyProperties.BLOCK_MODE_GCM,
      padding = KeyProperties.ENCRYPTION_PADDING_NONE
    )
  }
}