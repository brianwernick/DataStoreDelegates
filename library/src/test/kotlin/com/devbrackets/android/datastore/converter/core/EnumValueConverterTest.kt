package com.devbrackets.android.datastore.converter.core

import org.junit.Assert
import org.junit.Test

class EnumValueConverterTest {

  @Test
  fun toConvertedNull() {
    // Given
    val converter = EnumValueConverter(TestEnum::class)

    // When
    val actual = converter.toConverted(null)

    // Then
    Assert.assertNull(actual)
  }

  @Test
  fun toConvertedExists() {
    // Given
    val converter = EnumValueConverter(TestEnum::class)

    // When
    val actual = converter.toConverted(TestEnum.FIRST)

    // Then
    Assert.assertEquals("FIRST", actual)
  }

  @Test
  fun toOriginalNull() {
    // Given
    val converter = EnumValueConverter(TestEnum::class)

    // When
    val actual = converter.toOriginal(null)

    // Then
    Assert.assertNull(actual)
  }

  @Test
  fun toOriginalExists() {
    // Given
    val converter = EnumValueConverter(TestEnum::class)

    // When
    val actual = converter.toOriginal(TestEnum.SECOND.name)

    // Then
    Assert.assertEquals(TestEnum.SECOND, actual)
  }

  @Test
  fun toOriginalNotExists() {
    // Given
    val converter = EnumValueConverter(TestEnum::class)

    // When / Then
    Assert.assertThrows(IllegalArgumentException::class.java) {
      converter.toOriginal("FOURTH")
    }
  }

  private enum class TestEnum {
    FIRST,
    SECOND,
    THIRD
  }
}