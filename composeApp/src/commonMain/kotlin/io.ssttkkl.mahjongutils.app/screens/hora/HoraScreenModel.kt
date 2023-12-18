package io.ssttkkl.mahjongutils.app.screens.hora

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.screens.base.ResultScreenModel
import mahjongutils.models.Furo
import mahjongutils.models.Tile
import mahjongutils.models.Wind
import mahjongutils.yaku.Yaku
import mahjongutils.yaku.Yakus

class FuroModel {
    var tiles: List<Tile> by mutableStateOf(emptyList())
    var ankan: Boolean by mutableStateOf(false)

    val isKan: Boolean
        get() = tiles.size == 4 && tiles.all { it == tiles.first() }

    fun toFuro(): Furo {
        return Furo(tiles, ankan)
    }
}

class HoraScreenModel : ResultScreenModel<HoraCalcResult>() {
    var tiles by mutableStateOf<List<Tile>>(emptyList())
    val furo = mutableStateListOf<FuroModel>()
    var agari by mutableStateOf<Tile?>(null)
    var tsumo by mutableStateOf<Boolean>(false)
    var dora by mutableStateOf<String>("0")
    var selfWind by mutableStateOf<Wind?>(null)
    var roundWind by mutableStateOf<Wind?>(null)
    var extraYaku by mutableStateOf<Set<Yaku>>(emptySet())

    var tilesErrMsg by mutableStateOf<String?>(null)
    val furoErrMsg = mutableStateListOf<String?>(null)
    var agariErrMsg by mutableStateOf<String?>(null)
    var doraErrMsg by mutableStateOf<String?>(null)

    fun availableExtraYaku(): Set<Yaku> {
        return Yakus.allExtraYaku
    }

    fun allExtraYaku(): Set<Yaku> {
        return Yakus.allExtraYaku
    }

    override suspend fun onCalc(appState: AppState): HoraCalcResult {
        val args = HoraArgs(
            tiles,
            furo.map { it.toFuro() },
            agari!!,
            tsumo,
            dora.toIntOrNull() ?: 0,
            selfWind,
            roundWind,
            extraYaku
        )
        return args.calc()
    }
}