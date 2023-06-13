package com.devbrackets.android.datastoredemo.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

private val lightColors = Colors(
  primary = Color(0xFFD1A536),
  primaryVariant = Color(0xFF7E6A39),

  secondary = Color(0xFFD65728),
  secondaryVariant = Color(0xFF9E401D),
  background = Color(0xFFE2E0DB),
  surface = Color(0xFFE2E0DB),
  error = Color(0xFF721804),

  onPrimary = Color(0xFF242320),
  onSecondary = Color(0xFF242320),
  onBackground = Color(0xFF242320),
  onSurface = Color(0xFF242320),
  onError = Color(0xFFDDCBC7),
  isLight = true,
)

private val darkColors = lightColors.copy(
  primary = Color(0xFF242320),
  primaryVariant = Color(0xFF4B463B),

  background = Color(0xFF242320),
  surface = Color(0xFF242320),

  onPrimary = Color(0xFFE2E0DB),
  onBackground = Color(0xFFE2E0DB),
  onSurface = Color(0xFFE2E0DB),
  isLight = false
)

@Composable
fun DemoTheme(
  isDark: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {

  val colors = remember(isDark) {
    when (isDark) {
      true -> darkColors
      else -> lightColors
    }
  }

  MaterialTheme(
    colors = colors,
    typography = MaterialTheme.typography,
    shapes = MaterialTheme.shapes,
    content = content
  )
}