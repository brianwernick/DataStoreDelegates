package com.devbrackets.android.datastoredemo.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devbrackets.android.datastoredemo.common.DemoTheme

@Preview
@Composable
private fun PreviewSimpleDropdownMenu() {
  val options = remember {
    listOf("First", "Second", "Third")
  }

  val selection = remember {
    mutableStateOf("First")
  }

  DemoTheme {
    Box(
      modifier = Modifier.fillMaxSize()
    ) {
      SimpleDropdownMenu(
        options = options,
        selection = selection,
        onItemSelected = {
          selection.value = it
        },
        optionTransformer = { it },
        modifier = Modifier
          .padding(top = 24.dp)
          .fillMaxWidth()
          .padding(horizontal = 32.dp)
      )
    }
  }
}

@Composable
fun <T> SimpleDropdownMenu(
  options: List<T>,
  selection: MutableState<T>,
  onItemSelected: (T) -> Unit,
  optionTransformer: (T) -> String,
  modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }
  val selectedValue = remember(selection.value) {
    optionTransformer(selection.value)
  }

  val selectedBackground = MaterialTheme.colors.primary.copy(alpha = 0.1f)

  Box(
    modifier = Modifier
      .wrapContentSize(Alignment.TopStart)
      .then(modifier)
  ) {
    StationaryItem(
      displayText = selectedValue,
      onClick = {
        expanded = !expanded
      },
      modifier = Modifier.fillMaxWidth()
    )

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = {
        expanded = false
      }
    ) {
      options.forEach { option ->
        val displayText = remember(option) {
          optionTransformer(option)
        }

        DropdownMenuItem(
          onClick = {
            onItemSelected(option)
            expanded = false
          },
          modifier = Modifier
            .fillMaxWidth()
            .ifTrue(option == selection.value) {
              background(selectedBackground)
            }
            .defaultMinSize(minHeight = 48.dp)
        ) {
          Text(
            text = displayText,
            modifier = Modifier.align(Alignment.CenterVertically)
          )
        }
      }
    }
  }
}

@Composable
private fun StationaryItem(
  displayText: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // NOTE:
  // The `clickable` modifier doesn't really work on a TextField so we use a box
  // to wrap it since the TextField is Read-Only
  Box(
    modifier = modifier
  ) {
    OutlinedTextField(
      value = displayText,
      onValueChange = {},
      modifier = Modifier.fillMaxWidth(),
      trailingIcon = {
        Icon(
          imageVector = Icons.Rounded.ArrowDropDown,
          contentDescription = null
        )
      },
      readOnly = true
    )
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(TextFieldDefaults.MinHeight)
        .clip(MaterialTheme.shapes.small)
        .clickable(onClick = onClick)
    )
  }
}

/**
 * Provides a simple switched [Modifier] extension to more
 * easily optionally include a modifier based on the [check]
 */
private fun Modifier.ifTrue(
  check: Boolean,
  provider: Modifier.() -> Modifier
): Modifier {
  return if (check) {
    this.then(provider())
  } else {
    this
  }
}