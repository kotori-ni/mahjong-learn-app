package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineScope

//底部菜单状态类
@OptIn(ExperimentalMaterial3Api::class)
class AppBottomSheetState(
    //定义菜单内容
    val content: @Composable () -> Unit
) {
    //控制底部菜单是否可见
    var visible: Boolean by mutableStateOf(false)
    //表示底部表单在开始时是关闭的
    val sheetState: SheetState = SheetState(false)

    //伴生对象,表示一个没有内容且不可见的底部表单状态
    companion object {
        val NONE = AppBottomSheetState {}
    }
}

//对话框状态类
class AppDialogState(
    //可能用于处理对话框的关闭请求
    val dialog: @Composable (onDismissRequest: () -> Unit) -> Unit
) {
    //控制对话框的显示和隐藏
    var visible: Boolean by mutableStateOf(false)

    //伴生对象,表示一个没有内容且不可见的对话框状态
    companion object {
        val NONE = AppDialogState {}
    }
}

class AppState(
    val coroutineScope: CoroutineScope,
    val navigator: Navigator,
    val windowSizeClass: WindowSizeClass,
) {
    //设置导航抽屉初始为关闭状态
    val drawerState = DrawerState(DrawerValue.Closed)
    //用于显示短暂信息
    val snackbarHostState = SnackbarHostState()
    //初始无对话框显示
    var appDialogState by mutableStateOf(AppDialogState.NONE)
    //初始无底部菜单显示
    var appBottomSheetState by mutableStateOf(AppBottomSheetState.NONE)
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAppState(
    navigator: Navigator,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(),
): AppState {
    //当 Composable 离开组合时，所有在这个 CoroutineScope 中启动的协程都会被取消
    //这样可以确保协程的生命周期与 Composable 的生命周期一致
    return remember(navigator, coroutineScope, windowSizeClass) {
        AppState(
            navigator = navigator,
            coroutineScope = coroutineScope,
            windowSizeClass = windowSizeClass
        )
    }
}

val LocalAppState = compositionLocalOf<AppState> {
    error("No LocalAppState provided! ")
}
