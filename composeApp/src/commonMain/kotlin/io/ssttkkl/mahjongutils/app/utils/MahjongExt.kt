package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import mahjongutils.models.Tile
import mahjongutils.models.TileType
import mahjongutils.models.Wind
import mahjongutils.models.isWind
import mahjongutils.yaku.Yaku
import mahjongutils.yaku.Yakus

// https://en.wikipedia.org/wiki/Mahjong_Tiles_(Unicode_block)
private val tileToEmojiMapping = buildMap {
    Tile.all.forEach {
        this[it] = when (it.type) {
            TileType.M -> "\uD83C" + '\uDC06'.plus(it.realNum)
            TileType.P -> "\uD83C" + '\uDC18'.plus(it.realNum)
            TileType.S -> "\uD83C" + '\uDC0F'.plus(it.realNum)
            TileType.Z -> {
                if (it.isWind) {
                    "\uD83C" + '\uDBFF'.plus(it.realNum)
                } else if (it.num == 5) {
                    "\uD83C\uDC06"
                } else if (it.num == 6) {
                    "\uD83C\uDC05"
                } else {
                    "\uD83C\uDC04"
                }
            }
        }
    }
}

private val emojiToTileMapping =
    tileToEmojiMapping.toList().associate { (tile, emoji) -> emoji to tile }

val Tile.emoji: String
    get() = tileToEmojiMapping[this]!!

fun emojiToTile(emoji: String): Tile {
    return emojiToTileMapping[emoji]
        ?: throw IllegalArgumentException("$emoji is not a tile emoji")
}

@Composable
fun shantenNumText(shantenNum: Int): String {
    return when (shantenNum) {
        -1 -> stringResource(MR.strings.text_hora)
        0 -> stringResource(MR.strings.text_tenpai)
        else -> stringResource(
            MR.strings.text_shanten_num, shantenNum
        )
    }
}

val Wind.localizedName
    get() = when (this) {
        Wind.East -> MR.strings.label_wind_east
        Wind.South -> MR.strings.label_wind_south
        Wind.West -> MR.strings.label_wind_west
        Wind.North -> MR.strings.label_wind_north
    }

val Yaku.localizedName
    get() = when (this) {
        Yakus.Tenhou -> MR.strings.label_yaku_tenhou
        Yakus.Chihou -> MR.strings.label_yaku_chihou
        Yakus.WRichi -> MR.strings.label_yaku_wrichi
        Yakus.Richi -> MR.strings.label_yaku_richi
        Yakus.Ippatsu -> MR.strings.label_yaku_ippatsu
        Yakus.Rinshan -> MR.strings.label_yaku_rinshan
        Yakus.Chankan -> MR.strings.label_yaku_chankan
        Yakus.Haitei -> MR.strings.label_yaku_haitei
        Yakus.Houtei -> MR.strings.label_yaku_houtei
        else -> error("unknown yaku: $this")
    }