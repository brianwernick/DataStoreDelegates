package com.devbrackets.android.datastoredemo.data.token

import android.util.JsonReader
import android.util.JsonWriter
import com.devbrackets.android.datastore.converter.ValueConverter
import com.devbrackets.android.datastoredemo.data.model.AccountToken
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter


/**
 * A [ValueConverter] that converts the [AccountToken] to a string (JSON).
 *
 * **NOTE:**
 * This implementation is for demonstration purposes only, it doesn't correctly
 * handle error's.
 */
class DemoAccountTokenValueConverter: ValueConverter<AccountToken?, String?> {
  override fun toConverted(originalValue: AccountToken?): String? {
    if (originalValue == null || originalValue.isBlank()) {
      return null
    }

    val out = ByteArrayOutputStream()

    JsonWriter(OutputStreamWriter(out, "UTF-8")).apply {
      beginObject()
      name("authToken").value(originalValue.authToken)
      name("refreshToken").value(originalValue.refreshToken)
      endObject()
    }.close()

    return out.toString("UTF-8")
  }

  override fun toOriginal(convertedValue: String?): AccountToken? {
    if (convertedValue.isNullOrBlank()) {
      return null
    }

    val inputStream = convertedValue.byteInputStream(Charsets.UTF_8)
    val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

    return reader.readAccountToken().also {
      reader.close()
    }
  }

  private fun JsonReader.readAccountToken(): AccountToken {
    var authToken = ""
    var refreshToken = ""

    beginObject()

    while (hasNext()) {
      when(nextName()) {
        "authToken" -> {
          authToken = nextString()
        }
        "refreshToken" -> {
          refreshToken = nextString()
        }
        else -> throw IllegalStateException("Unexpected AccountToken field")
      }
    }

    endObject()

    return AccountToken(
      authToken = authToken,
      refreshToken = refreshToken
    )
  }
}