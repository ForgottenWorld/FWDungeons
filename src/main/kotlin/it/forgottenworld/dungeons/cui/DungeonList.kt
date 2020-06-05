package it.forgottenworld.dungeons.cui

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.util.ChatPaginator
fun getJoinClickable(instance: DungeonInstance, leader: Boolean, locked: Boolean, full: Boolean, player: Player) =
        TextComponent(
                when {
                    leader -> "CREATE"
                    locked -> "PRIVATE"
                    full -> "FULL"
                    else -> "JOIN"
                }).apply {
            color = when {
                full -> ChatColor.RED
                locked -> ChatColor.GOLD
                else -> ChatColor.GREEN
            }
            if (!full && !locked)
                clickEvent =
                        ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/fwdungeons joininst ${instance.dungeon.id} ${instance.id} ${player.name}")
        }

fun getPageClickable(text: String, page: Int) =
        TextComponent(text).apply {
            color = ChatColor.AQUA
            clickEvent =
                    ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/fwdungeons dungeon list $page")
        }


fun getInteractiveDungeonList(player: Player, page: Int) =
    TextComponent().apply {
        if (page >= 0 && page <= FWDungeonsController.dungeons.count() - 1) {
            val d = FWDungeonsController.dungeons.values.toList()[page]
            addExtra(TextComponent("======================[Dungeons]======================\n").apply {
                addExtra("=== ${d.name}\n\n")
                d.instances.forEachIndexed { ii, inst ->
                    val party = inst.party
                    var count = 0
                    "Room $ii | Leader: ".let{ count += it.length; addExtra(it) }
                    addExtra(
                            TextComponent(party?.leader?.name ?: "none")
                                    .apply {
                                        if (text != "none") color = ChatColor.GOLD
                                    }.also{ count += it.toPlainText().length })
                    addExtra(" ".repeat(54 - count))
                    addExtra("[")
                    addExtra(
                            getJoinClickable(
                                    inst,
                                    party == null,
                                    party?.isLocked == true,
                                    party?.isFull == true,
                                    player).also { count += it.toPlainText().length } )
                    text
                    addExtra("]${"\n".repeat(15 - d.instances.count())}")
                    if (page > 0) {
                        addExtra("=[")
                        addExtra(getPageClickable("PREVIOUS", page - 1))
                        addExtra("]")
                    } else addExtra("===========")
                    addExtra("====================================")
                    if (page < FWDungeonsController.dungeons.count() - 1) {
                        addExtra("[")
                        addExtra(getPageClickable("NEXT", page + 1))
                        addExtra("]=\n")
                    }
                    else addExtra("=======\n")
                }
            })
        }
    }