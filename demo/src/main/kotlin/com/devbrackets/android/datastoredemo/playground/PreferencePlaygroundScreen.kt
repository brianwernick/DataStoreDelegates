package com.devbrackets.android.datastoredemo.playground

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devbrackets.android.datastoredemo.common.DemoTheme
import com.devbrackets.android.datastoredemo.data.model.AccountToken
import com.devbrackets.android.datastoredemo.data.model.Month

@Composable
fun PreferencePlaygroundScreen(
  viewModel: PreferencePlaygroundViewModel
) {
  val month = viewModel.month.collectAsState(Month.JAN)
  val accountToken = viewModel.accountToken.collectAsState(null)
  val encryptedToken = viewModel.encryptedAccountToken.collectAsState(null)

  PreferencePlaygroundScreen(
    month = month,
    onMonthSelected = {
      viewModel.setMonth(it)
    },
    accountToken = accountToken,
    encryptedAccountToken = encryptedToken,
    onAccountTokenUpdated = {
      viewModel.setAccountToken(it)
    }
  )
}

@Preview
@Composable
private fun PreviewPreferencePlaygroundScreen() {
  val month = remember { mutableStateOf(Month.JAN) }
  val accountToken = remember { mutableStateOf<AccountToken?>(null) }
  val encryptedAccountToken = remember { mutableStateOf("") }

  DemoTheme {
    PreferencePlaygroundScreen(
      month = month,
      onMonthSelected = {
        month.value = it
      },
      accountToken = accountToken,
      encryptedAccountToken = encryptedAccountToken,
      onAccountTokenUpdated = {
        accountToken.value = it
      }
    )
  }
}

@Composable
private fun PreferencePlaygroundScreen(
  month: State<Month>,
  onMonthSelected: (Month) -> Unit,
  accountToken: State<AccountToken?>,
  encryptedAccountToken: State<String?>,
  onAccountTokenUpdated: (AccountToken) -> Unit
) {
  ScreenScaffold { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(paddingValues)
        .padding(WindowInsets.navigationBars.asPaddingValues())
        .padding(WindowInsets.ime.asPaddingValues())
        .padding(top = 16.dp, bottom = 56.dp)
    ) {
      Header(
        text = "Month",
        modifier = Modifier.fillMaxWidth()
      )
      MonthSelector(
        month = month,
        onMonthSelected = onMonthSelected,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 32.dp)
      )

      Spacer(modifier = Modifier.height(32.dp))

      Header(
        text = "Account Token",
        modifier = Modifier.fillMaxWidth()
      )
      AccountTokenFields(
        accountToken = accountToken,
        encryptedAccountToken = encryptedAccountToken,
        onAccountTokenUpdated = onAccountTokenUpdated,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 32.dp)
      )
    }
  }
}

@Composable
private fun ScreenScaffold(
  content: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(text = "DataStore Delegates Demo")
        }
      )
    },
    content = content
  )
}

@Composable
private fun Header(
  text: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .height(48.dp)
      .padding(horizontal = 32.dp)
  ) {
    Text(
      text = text,
      modifier = Modifier.align(Alignment.CenterStart),
      fontWeight = FontWeight.Black
    )
  }
}

@Composable
private fun MonthSelector(
  month: State<Month>,
  onMonthSelected: (Month) -> Unit,
  modifier: Modifier = Modifier
) {
  val availableMonths = remember { Month.values().toList() }
  val selectedMonth = remember { mutableStateOf(month.value) }
  LaunchedEffect(month.value) {
    selectedMonth.value = month.value
  }

  SimpleDropdownMenu(
    options = availableMonths,
    selection = selectedMonth,
    onItemSelected = {
      onMonthSelected(it)
    },
    optionTransformer = {
      it.displayText()
    },
    modifier = modifier
  )
}

@Composable
private fun AccountTokenFields(
  accountToken: State<AccountToken?>,
  encryptedAccountToken: State<String?>,
  onAccountTokenUpdated: (AccountToken) -> Unit,
  modifier: Modifier = Modifier
) {
  val updateToken: (authToken: String?, refreshToken: String?) -> Unit = remember {
    { authToken, refreshToken ->
      val newToken = AccountToken(
        authToken = authToken.orEmpty(),
        refreshToken = refreshToken.orEmpty()
      )

      onAccountTokenUpdated(newToken)
    }
  }

  Column(
    modifier = modifier
  ) {
    OutlinedTextField(
      value = accountToken.value?.authToken.orEmpty(),
      onValueChange = {
        updateToken(it, accountToken.value?.refreshToken)
      },
      modifier = Modifier.fillMaxWidth(),
      label = {
        Text(
          text = "Auth Token"
        )
      }
    )

    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
      value = accountToken.value?.refreshToken.orEmpty(),
      onValueChange = {
        updateToken(accountToken.value?.authToken, it)
      },
      modifier = Modifier.fillMaxWidth(),
      label = {
        Text(
          text = "Refresh Token"
        )
      }
    )

    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
      value = encryptedAccountToken.value.orEmpty(),
      onValueChange = {},
      modifier = Modifier.fillMaxWidth(),
      enabled = false,
      readOnly = true,
      label = {
        Text(
          text = "Encrypted Account Token"
        )
      }
    )
  }
}

/**
 * NOTE:
 * The strings are hard-coded for the demo, in actual applications
 * these should be read from string resources.
 */
@Stable
private fun Month.displayText(): String {
  return when(this) {
    Month.JAN -> "January"
    Month.FEB -> "February"
    Month.MAR -> "March"
    Month.APR -> "April"
    Month.MAY -> "May"
    Month.JUN -> "June"
    Month.JUL -> "July"
    Month.AUG -> "August"
    Month.SEP -> "September"
    Month.OCT -> "October"
    Month.NOV -> "November"
    Month.DEC -> "December"
  }
}