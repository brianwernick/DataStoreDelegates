package com.devbrackets.android.datastore.converter.crypto.spec

internal data class TransformationSpec(
  val algorithm: String,
  val blockMode: String,
  val padding: String
) {
  override fun toString(): String {
    return "${algorithm}/${blockMode}/${padding}"
  }
}