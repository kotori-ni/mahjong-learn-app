package io.ssttkkl.mahjongutils.app

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppScaffold
import io.ssttkkl.mahjongutils.app.components.appscaffold.rememberAppState
import io.ssttkkl.mahjongutils.app.screens.about.AboutScreen
import io.ssttkkl.mahjongutils.app.components.appscaffold.NavigationScreen
import io.ssttkkl.mahjongutils.app.screens.furoshanten.FuroShantenScreen
import io.ssttkkl.mahjongutils.app.screens.hanhu.HanhuScreen
import io.ssttkkl.mahjongutils.app.screens.hora.HoraScreen
import io.ssttkkl.mahjongutils.app.screens.shanten.ShantenScreen
import io.ssttkkl.mahjongutils.app.theme.AppTheme
import kotlinx.coroutines.launch

// 导航栏
private val navigatableScreens: List<NavigationScreen> = listOf(
    ShantenScreen,      //牌理
    FuroShantenScreen,  //副露牌理
    HoraScreen,         //和牌分析
    HanhuScreen,        //番符得点计算
    AboutScreen         //关于
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

@Composable
fun App(typography: Typography = MaterialTheme.typography) {
    AppTheme(typography = typography) {
        Navigator(ShantenScreen) { navigator ->

            //判断是否使用导航抽屉
            val windowSizeClass: WindowSizeClass = calculateWindowSizeClass()
            //在小屏幕的情况下使用导航抽屉(宽度小于Expanded,高度不为Compact)
            val useNavigationDrawer =
                !(windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
                        && windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact)

            val appState = rememberAppState(navigator, windowSizeClass = windowSizeClass)

            AppScaffold(
                appState,
                navigatableScreens,
                useNavigationDrawer,
                navigationIcon = { canGoBack ->
                    //处于底层,不可返回上一页
                    if (!canGoBack) {
                        //显示导航抽屉菜单图标
                        if (useNavigationDrawer) {
                            Icon(Icons.Filled.Menu, "", Modifier.clickable {
                                //切换导航抽屉状态(设置点击动作)
                                appState.coroutineScope.launch {
                                    if (appState.drawerState.isClosed) {
                                        appState.drawerState.open()
                                    } else {
                                        appState.drawerState.close()
                                    }
                                }
                            })
                        }
                    }
                    // 可返回上一页,显示返回图标 
                    else {
                        Icon(Icons.Filled.ArrowBack, "", Modifier.clickable {
                            appState.navigator.pop()
                        })
                    }
                }
            )
        }
    }
}
