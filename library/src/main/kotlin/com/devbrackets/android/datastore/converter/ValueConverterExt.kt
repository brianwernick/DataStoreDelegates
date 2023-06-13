package com.devbrackets.android.datastore.converter

import com.devbrackets.android.datastore.converter.core.ChainedValueConverter
import com.devbrackets.android.datastore.converter.core.NonNullValueConverter

/**
 * Chains the source [ValueConverter] with the [nextConverter]. When
 * storing data the source will be called before [nextConverter] while the inverse
 * will occur when reading.
 *
 * An example usage is:
 * ```kotlin
 * val chainedConverter = EncryptedValueConverter().then(Base64ValueConverter())
 * ```
 *
 * This method is short-hand for the [ChainedValueConverter].
 *
 * @param nextConverter The [ValueConverter] that should be called after the target [ValueConverter]
 */
fun <ORIGINAL, INTERMEDIATE, CONVERTED> ValueConverter<ORIGINAL, INTERMEDIATE>.then(
  nextConverter: ValueConverter<INTERMEDIATE, CONVERTED>
): ValueConverter<ORIGINAL, CONVERTED> {
  return ChainedValueConverter(this, nextConverter)
}

/**
 * Converts the source [ValueConverter] to a [ValueConverter] with non-nullable types.
 * This method is short-hand for the [NonNullValueConverter].
 *
 * @param errorHandler The [NonNullValueConverter.ErrorHandler] to use for unexpected `null` values
 */
fun <ORIGINAL, CONVERTED> ValueConverter<ORIGINAL?, CONVERTED?>.nonNull(
  errorHandler: NonNullValueConverter.ErrorHandler<ORIGINAL, CONVERTED> = NonNullValueConverter.defaultErrorHandler()
): ValueConverter<ORIGINAL, CONVERTED> {
  return NonNullValueConverter(this, errorHandler)
}