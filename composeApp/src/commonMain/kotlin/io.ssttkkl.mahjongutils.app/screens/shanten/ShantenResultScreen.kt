package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowMessageOnFailure
import io.ssttkkl.mahjongutils.app.components.panel.CardPanel
import io.ssttkkl.mahjongutils.app.components.table.ShantenAction
import io.ssttkkl.mahjongutils.app.components.table.ShantenActionTable
import io.ssttkkl.mahjongutils.app.components.table.ShantenActionTableType
import io.ssttkkl.mahjongutils.app.components.tiles.Tiles
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.format
import mahjongutils.models.Tile
import mahjongutils.shanten.ShantenWithGot
import mahjongutils.shanten.ShantenWithoutGot
import mahjongutils.shanten.asWithGot
import mahjongutils.shanten.asWithoutGot

private val contentModifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.medium)

@Composable
private fun ShantenNumCard(shantenNum: Int) {
    val text = when (shantenNum) {
        -1 -> Res.string.text_hora
        0 -> Res.string.text_tenpai
        else -> Res.string.text_shanten_num.format(shantenNum)
    }

    CardPanel(Res.string.label_shanten_num, modifier = contentModifier) {
        Text(
            text,
            Modifier.then(contentModifier)
        )
    }
}

@Composable
private fun TilesCard(
    label: String,
    tiles: List<Tile>,
    caption: String,
    tileModifier: Modifier = Modifier.height(30.dp)
) {
    CardPanel(label, modifier = contentModifier) {
        Column {
            Tiles(
                tiles,
                contentModifier,
                tileModifier
            )
            Text(
                caption,
                Modifier.padding(top = Spacing.medium)
                    .then(contentModifier),
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
private fun TilesInHandCard(tiles: List<Tile>, withGot: Boolean) {
    TilesCard(
        Res.string.label_tiles_in_hand,
        tiles,
        if (withGot) Res.string.text_tiles_with_got else Res.string.text_tiles_without_got,
        Modifier.height(36.dp)
    )
}

@Composable
private fun TilesWithNumCard(
    label: String,
    tiles: Collection<Tile>,
    tileNum: Int
) {
    TilesCard(
        label,
        tiles.sorted(),
        Res.string.text_tiles_num.format(tileNum)
    )
}

@Composable
fun ShantenResultScreen(tiles: List<Tile>, shanten: ShantenWithoutGot) {
    val scrollState = rememberScrollState()

    Column(Modifier.fillMaxWidth().verticalScroll(scrollState)) {
        Spacer(Modifier.height(Spacing.large))

        TilesInHandCard(tiles, false)

        Spacer(Modifier.height(Spacing.medium))

        ShantenNumCard(shanten.shantenNum)

        Spacer(Modifier.height(Spacing.medium))

        TilesWithNumCard(
            Res.string.label_advance_tiles,
            shanten.advance,
            shanten.advanceNum
        )

        Spacer(Modifier.height(Spacing.medium))

        shanten.goodShapeAdvance?.let { goodShapeAdvance ->
            shanten.goodShapeAdvanceNum?.let { goodShapeAdvanceNum ->
                TilesWithNumCard(
                    Res.string.label_good_shape_advance_tiles,
                    goodShapeAdvance,
                    goodShapeAdvanceNum
                )
            }
        }
    }
}

@Composable
fun ShantenResultScreen(tiles: List<Tile>, shanten: ShantenWithGot) {
    val scrollState = rememberScrollState()

    Column(Modifier.fillMaxWidth().verticalScroll(scrollState)) {
        Spacer(Modifier.height(Spacing.large))

        TilesInHandCard(tiles, false)

        Spacer(Modifier.height(Spacing.medium))

        ShantenNumCard(shanten.shantenNum)

        Spacer(Modifier.height(Spacing.medium))

        // shanten to actions (asc sorted)
        val groups: List<Pair<Int, List<ShantenAction>>> = remember(shanten) {
            val groupedShanten = mutableMapOf<Int, MutableList<ShantenAction>>()
            shanten.discardToAdvance.forEach { (discard, shantenAfterDiscard) ->
                val group =
                    groupedShanten.getOrPut(shantenAfterDiscard.shantenNum) { mutableListOf() }
                group.add(ShantenAction.Discard(discard, shantenAfterDiscard))
            }

            shanten.ankanToAdvance.forEach { (ankan, shantenAfterAnkan) ->
                val group =
                    groupedShanten.getOrPut(shantenAfterAnkan.shantenNum) { mutableListOf() }
                group.add(ShantenAction.Ankan(ankan, shantenAfterAnkan))
            }

            groupedShanten.toList().sortedBy { it.first }
        }

        if (shanten.shantenNum != -1) {
            groups.forEach { (shantenNum, actions) ->
                val label = if (shantenNum == shanten.shantenNum)
                    Res.string.label_shanten_action.format(shantenNum)
                else
                    Res.string.label_shanten_action_backwards.format(shantenNum)

                CardPanel(label, contentModifier) {
                    val type = when (shantenNum) {
                        0 -> ShantenActionTableType.WithGoodShapeImprovement
                        1 -> ShantenActionTableType.WithGoodShapeAdvance
                        else -> ShantenActionTableType.Normal
                    }
                    ShantenActionTable(actions, type, contentModifier)
                }

                Spacer(Modifier.height(Spacing.medium))
            }
        }
    }

}

@Composable
fun ShantenResultScreen(args: ShantenArgs) {
    Calculation(
        args,
        {
            args.calc()
        },
        onFailure = {
            PopAndShowMessageOnFailure(it)
        }
    ) {
        if (it.shantenInfo is ShantenWithoutGot) {
            ShantenResultScreen(args.tiles, it.shantenInfo.asWithoutGot)
        } else {
            ShantenResultScreen(args.tiles, it.shantenInfo.asWithGot)
        }
    }
}