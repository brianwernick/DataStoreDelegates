package com.devbrackets.android.datastoredemo.data

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.devbrackets.android.datastore.converter.core.Base64ValueConverter
import com.devbrackets.android.datastore.converter.core.EnumValueConverter
import com.devbrackets.android.datastore.converter.core.StringBytesValueConverter
import com.devbrackets.android.datastore.converter.crypto.EncryptedValueConverter
import com.devbrackets.android.datastore.converter.then
import com.devbrackets.android.datastore.delegate.value
import com.devbrackets.android.datastore.flow
import com.devbrackets.android.datastoredemo.data.model.Month
import com.devbrackets.android.datastoredemo.data.token.DemoAccountTokenValueConverter

class Preferences(context: Context) {
  companion object {
    private val tokenConverter = DemoAccountTokenValueConverter()
      .then(StringBytesValueConverter())
      .then(EncryptedValueConverter())
      .then(Base64ValueConverter())
  }

  private val dataStore = PreferenceDataStoreFactory.create {
    context.applicationContext.preferencesDataStoreFile("demo_prefs")
  }

  // A separate DataStore is used for Encrypted preferences because we
  // want to ensure that these are not backed-up (see data_extraction_rules.xml)
  private val localDataStore = PreferenceDataStoreFactory.create {
    context.applicationContext.preferencesDataStoreFile("demo_prefs_local")
  }

  // Month is a simple Preference, in this case it's using a Converter
  // to convert to a supported preference type (String)
  var month by dataStore.value("month", Month.JAN, EnumValueConverter(Month::class))
  val monthFlow = dataStore.flow("month", Month.JAN, EnumValueConverter(Month::class))


  // AccountToken's should be stored more securely, to handle this we use a Converter
  // that encrypts the object and stores the result in Base64
  var accountToken by localDataStore.value("accountToken", null, tokenConverter)
  val accountTokenFlow = localDataStore.flow("accountToken", null, tokenConverter)
  val plainAccountTokenFlow = localDataStore.flow<String?>("accountToken", null)
}