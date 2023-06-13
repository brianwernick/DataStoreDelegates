package com.devbrackets.android.datastoredemo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.devbrackets.android.datastoredemo.common.DemoTheme
import com.devbrackets.android.datastoredemo.data.Preferences
import com.devbrackets.android.datastoredemo.navigation.DemoNavHost
import com.devbrackets.android.datastoredemo.playground.PreferencePlaygroundScreen
import com.devbrackets.android.datastoredemo.playground.PreferencePlaygroundViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@ExperimentalAnimationApi
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      DemoTheme {
        Content()
      }
    }
  }

  @Composable
  fun Content() {
    val navController = rememberAnimatedNavController()

    DemoNavHost(
      navController = navController,
      startDestination = "home",
      modifier = Modifier.background(MaterialTheme.colors.background),
    ) {
      composable(
        route = "home"
      ) {
        // NOTE: normally you would use Dagger/Koin/etc. to fetch the ViewModel
        val context = LocalContext.current
        val viewModel = remember(context) {
          val prefs = Preferences(context)
          PreferencePlaygroundViewModel(prefs)
        }

        PreferencePlaygroundScreen(
          viewModel = viewModel
        )
      }
    }
  }
}