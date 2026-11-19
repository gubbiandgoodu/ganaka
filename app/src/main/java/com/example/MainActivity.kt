package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.AppTab
import com.example.model.AppThemeMode
import com.example.ui.MainViewModel
import com.example.ui.components.DeveloperDialog
import com.example.ui.components.LocalHapticFeedbackEnabled
import com.example.ui.components.TopNavigationBar
import com.example.ui.screens.GstScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MasterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.GanakaTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()
            val isDarkTheme = when (settings.themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            GanakaTheme(darkTheme = isDarkTheme) {
                GanakaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GanakaApp(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showDeveloperPopup by viewModel.showDeveloperPopup.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val colors = GanakaTheme.colors

    CompositionLocalProvider(LocalHapticFeedbackEnabled provides settings.hapticFeedbackEnabled) {
        // BackHandler: if not on MASTER tab, return to MASTER on back press
        BackHandler(enabled = currentTab != AppTab.MASTER) {
            viewModel.selectTab(AppTab.MASTER)
        }

        val bgModifier = if (colors.backgroundBrush != null) {
            Modifier.background(colors.backgroundBrush)
        } else {
            Modifier.background(colors.background)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(bgModifier)
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                    ) {
                        TopNavigationBar(
                            currentTab = currentTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        AppTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                        AppTab.MASTER -> MasterScreen(viewModel = viewModel)
                        AppTab.GST -> GstScreen(viewModel = viewModel)
                        AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }

            if (showDeveloperPopup) {
                DeveloperDialog(onDismiss = { viewModel.closeDeveloperPopup() })
            }
        }
    }
}
