package com.devbrackets.android.datastoredemo.data.model

data class AccountToken(
  val authToken: String,
  val refreshToken: String
) {
  fun isBlank(): Boolean {
    return authToken.isBlank() && refreshToken.isBlank()
  }
}
