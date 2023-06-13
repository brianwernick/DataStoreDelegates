package com.devbrackets.android.datastoredemo.playground

import androidx.lifecycle.ViewModel
import com.devbrackets.android.datastoredemo.data.Preferences
import com.devbrackets.android.datastoredemo.data.model.AccountToken
import com.devbrackets.android.datastoredemo.data.model.Month

class PreferencePlaygroundViewModel(
  private val prefs: Preferences
): ViewModel() {
  val month = prefs.monthFlow
  fun setMonth(month: Month) {
    prefs.month = month
  }

  val accountToken = prefs.accountTokenFlow
  val encryptedAccountToken = prefs.plainAccountTokenFlow
  fun setAccountToken(token: AccountToken) {
    prefs.accountToken = token
  }
}