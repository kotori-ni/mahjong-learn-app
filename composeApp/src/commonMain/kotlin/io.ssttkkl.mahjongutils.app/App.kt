package io.ssttkkl.mahjongutils.app

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.appbar.AppTopBar
import io.ssttkkl.mahjongutils.app.screens.furoshanten.furoShantenScene
import io.ssttkkl.mahjongutils.app.screens.shanten.RouteShanten
import io.ssttkkl.mahjongutils.app.screens.shanten.shantenScene
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost

@Composable
fun App() {
    PreComposeApp {
        val appState = rememberAppState()
        CompositionLocalProvider(
            LocalAppState provides appState,
        ) {
            AppContent(appState)
        }
    }
}

@Composable
fun AppContent(appState: AppState) {
    MaterialTheme {
        Scaffold(
            scaffoldState = appState.scaffoldState,
            topBar = {
                AppTopBar(appState)
            },
            drawerContent = {
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = it,
                    modifier = Modifier.systemBarsPadding(),
                    snackbar = { snackbarData -> Snackbar(snackbarData) }
                )
            },
        ) {
            NavHost(
                navigator = appState.navigator,
                initialRoute = RouteShanten.route
            ) {
                shantenScene()
                furoShantenScene()
            }
        }
    }
}