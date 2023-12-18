package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.panel.TopCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenAction
import io.ssttkkl.mahjongutils.app.components.resultdisplay.ShantenNumCardPanel
import io.ssttkkl.mahjongutils.app.components.resultdisplay.TilesTopCardPanel
import io.ssttkkl.mahjongutils.app.components.table.ShantenActionTable
import io.ssttkkl.mahjongutils.app.components.table.ShantenActionTableType
import io.ssttkkl.mahjongutils.app.components.tiles.Tiles
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.format
import io.ssttkkl.mahjongutils.app.utils.shantenNumText
import mahjongutils.shanten.ShantenWithFuroChance

@Composable
fun FuroShantenResultContent(args: FuroChanceShantenArgs, shanten: ShantenWithFuroChance) {
    val scrollState = rememberScrollState()

    with(Spacing.current) {
        Column(Modifier.fillMaxWidth().verticalScroll(scrollState)) {
            VerticalSpacerBetweenPanels()

            TilesTopCardPanel(
                Res.string.label_tiles_in_hand,
                args.tiles,
                tracingElement = {
                    Text(Res.string.label_tile_discarded_by_other)
                    Tiles(listOf(args.chanceTile), Modifier.height(36.dp))
                }
            )

            VerticalSpacerBetweenPanels()

            ShantenNumCardPanel(shanten.shantenNum)

            VerticalSpacerBetweenPanels()

            // shanten to actions (asc sorted)
            val groups: List<Pair<Int, List<ShantenAction>>> = remember(shanten) {
                val groupedShanten = mutableMapOf<Int, MutableList<ShantenAction>>()
                fun getGroup(shantenNum: Int) =
                    groupedShanten.getOrPut(shantenNum) { mutableListOf() }

                shanten.chi.forEach { (tatsu, shantenAfterChi) ->
                    shantenAfterChi.discardToAdvance.forEach { (discard, shantenAfterAction) ->
                        getGroup(shantenAfterAction.shantenNum).add(
                            ShantenAction.Chi(
                                tatsu,
                                discard,
                                shantenAfterAction
                            )
                        )
                    }
                }

                shanten.pon?.let { shantenAfterPon ->
                    shantenAfterPon.discardToAdvance.forEach { (discard, shantenAfterAction) ->
                        getGroup(shantenAfterAction.shantenNum).add(
                            ShantenAction.Pon(
                                args.chanceTile,
                                discard,
                                shantenAfterAction
                            )
                        )
                    }
                }

                shanten.minkan?.let { shantenAfterAction ->
                    getGroup(shantenAfterAction.shantenNum).add(
                        ShantenAction.Minkan(
                            args.chanceTile,
                            shantenAfterAction
                        )
                    )
                }

                shanten.pass?.let { shantenAfterAction ->
                    getGroup(shantenAfterAction.shantenNum).add(
                        ShantenAction.Pass(shantenAfterAction)
                    )
                }

                groupedShanten.toList().sortedBy { it.first }
            }

            groups.forEach { (shantenNum, actions) ->
                val label_shanten_action = if (shantenNum == shanten.shantenNum)
                    Res.string.label_shanten_action
                else
                    Res.string.label_shanten_action_backwards

                val label = label_shanten_action.format(
                    shantenNumText(shantenNum)
                )

                TopCardPanel(label, {
                    val type = when (shantenNum) {
                        0 -> ShantenActionTableType.WithGoodShapeImprovement
                        1 -> ShantenActionTableType.WithGoodShapeAdvance
                        else -> ShantenActionTableType.Normal
                    }
                    ShantenActionTable(actions, type)
                })

                VerticalSpacerBetweenPanels()
            }
        }
    }
}