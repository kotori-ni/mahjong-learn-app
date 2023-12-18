package io.ssttkkl.mahjongutils.app.components.resultdisplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.divider.VerticalDivider
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.components.tiles.Tile
import io.ssttkkl.mahjongutils.app.components.tiles.Tiles
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.format
import mahjongutils.models.Tatsu
import mahjongutils.models.Tile
import mahjongutils.shanten.Improvement
import mahjongutils.shanten.ShantenWithoutGot

sealed class ShantenAction {
    abstract val shantenAfterAction: ShantenWithoutGot

    data class Discard(
        val tile: Tile,
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction()

    data class Ankan(
        val tile: Tile,
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction()

    data class Chi(
        val tatsu: Tatsu,
        val discard: Tile,
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction()

    data class Pon(
        val tile: Tile,
        val discard: Tile,
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction()

    data class Minkan(
        val tile: Tile,
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction()

    data class Pass(
        override val shantenAfterAction: ShantenWithoutGot
    ) : ShantenAction()
}

@Composable
fun ShantenActionCardContent(
    action: ShantenAction
) {
    val shanten = action.shantenAfterAction
    with(Spacing.current) {
        Row {
            ShantenActionContent(
                action,
                Modifier.width(120.dp)
            )

            VerticalDivider()

            Column(Modifier.weight(1f)) {
                TilesWithNumPanel(
                    if (shanten.shantenNum == 0)
                        Res.string.label_waiting_tiles
                    else
                        Res.string.label_advance_tiles,
                    shanten.advance.sorted(),
                    shanten.advanceNum
                )

                shanten.goodShapeAdvance?.let { goodShapeAdvance ->
                    VerticalSpacerBetweenPanels()

                    TilesWithNumPanel(
                        Res.string.label_good_shape_advance_tiles,
                        goodShapeAdvance.sorted(),
                        shanten.goodShapeAdvanceNum ?: 0
                    )
                }

                shanten.goodShapeImprovement?.let { goodShapeImprovement ->
                    VerticalSpacerBetweenPanels()

                    ImprovementsPanel(
                        Res.string.label_good_shape_improvement_tiles,
                        goodShapeImprovement,
                        shanten.goodShapeImprovementNum ?: 0
                    )
                }
            }
        }
    }
}

@Composable
private fun ShantenActionContent(
    action: ShantenAction,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
) {
    Row(modifier, horizontalArrangement, verticalAlignment) {
        when (action) {
            is ShantenAction.Discard -> {
                Text(Res.string.text_shanten_action_discard)
                Tile(action.tile)
            }

            is ShantenAction.Ankan -> {
                Text(Res.string.text_shanten_action_ankan)
                Tile(action.tile)
            }

            is ShantenAction.Chi -> {
                Tiles(listOf(action.tatsu.first, action.tatsu.second))
                Text(
                    Res.string.text_shanten_action_chi
                            + Res.string.text_shanten_action_and
                            + Res.string.text_shanten_action_discard
                )
                Tile(action.discard)
            }

            is ShantenAction.Pon -> {
                Text(
                    Res.string.text_shanten_action_pon
                            + Res.string.text_shanten_action_and
                            + Res.string.text_shanten_action_discard
                )
                Tile(action.discard)
            }

            is ShantenAction.Minkan -> {
                Text(Res.string.text_shanten_action_minkan)
                Tile(action.tile)
            }

            is ShantenAction.Pass -> {
                Text(Res.string.text_shanten_action_pass)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ImprovementsPanel(
    label: String,
    improvement: Map<Tile, List<Improvement>>,
    improvementNum: Int
) {
    Panel(label) {
        improvement.map {
            // tile, discard_tiles, advance_num
            // 摸tile, 打discard_tiles, 进advance_num张
            Triple(
                it.key,
                it.value.map { it.discard },
                it.value.firstOrNull()?.advanceNum ?: 0
            )
        }.forEach { (tile, discardTiles, advanceNum) ->
            FlowRow {
                Text(Res.string.text_draw)
                Tiles(listOf(tile))
                Text(Res.string.text_shanten_action_and + Res.string.text_shanten_action_discard)
                Tiles(discardTiles)
                Text(
                    Res.string.text_comma
                            + Res.string.text_waiting_tiles_num.format(advanceNum)
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        Text(
            Res.string.text_tiles_num.format(improvementNum),
            style = MaterialTheme.typography.labelMedium
        )
    }
}