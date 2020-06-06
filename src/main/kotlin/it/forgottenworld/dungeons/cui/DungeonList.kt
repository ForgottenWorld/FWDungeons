package it.forgottenworld.dungeons.cui

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import kotlin.math.ceil

private fun getJoinClickable(instance: DungeonInstance, leader: Boolean, locked: Boolean, full: Boolean, inGame: Boolean, player: Player) =
        TextComponent(
                when {
                    leader -> "CREATE"
                    locked -> "PRIVATE"
                    full -> "FULL"
                    inGame -> "IN DUNGEON"
                    else -> "JOIN"
                }).apply {
                    color = when {
                        full || inGame -> ChatColor.RED
                        locked -> ChatColor.GOLD
                        else -> ChatColor.GREEN
                    }
                    if (!full && !locked && !inGame)
                    clickEvent =
                            ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/fwdungeons dungeon join ${instance.dungeon.id} ${instance.id}")
                }

private fun getPageClickable(text: String, page: Int) =
        TextComponent(text).apply {
            color = ChatColor.AQUA
            clickEvent =
                    ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/fwdungeons dungeon list $page")
        }

private fun getCarets(count: Int) =
        TextComponent("${">".repeat(count)} ").apply {
            color = ChatColor.GRAY
        }

private fun getColoredDifficulty(difficulty: Dungeon.Difficulty) =
        TextComponent("${difficulty.toString().toUpperCase()}\n").apply {
            color = when (difficulty) {
                Dungeon.Difficulty.EASY -> ChatColor.DARK_GREEN
                Dungeon.Difficulty.MEDIUM -> ChatColor.GOLD
                Dungeon.Difficulty.HARD -> ChatColor.DARK_RED
            }
        }

fun getInteractiveDungeonList(player: Player, page: Int) =
    TextComponent().apply {
        if (page >= 0 && page <= FWDungeonsController.dungeons.count() - 1) {
            val d = FWDungeonsController.dungeons.values.toList()[page]
            addExtra(TextComponent("====================[ ${ChatColor.DARK_PURPLE}FWDungeons${ChatColor.WHITE} ]====================\n\n").apply {
                addExtra(getCarets(3))
                addExtra("DUNGEON: ${d.name}\n")
                addExtra(getCarets(3))
                addExtra("DESCRIPTION: ${d.description}\n")
                addExtra(getCarets(3))
                addExtra("DIFFICULTY: ")
                addExtra(getColoredDifficulty(d.difficulty))
                addExtra(getCarets(3))
                addExtra(
                        "PLAYERS: ${d.numberOfPlayers.first.toString() +
                            if (d.numberOfPlayers.last != d.numberOfPlayers.first) 
                                "-" + d.numberOfPlayers.last.toString()
                            else ""}\n\n")
                d.instances.forEachIndexed { ii, inst ->
                    val party = inst.party
                    addExtra(getCarets(1))
                    addExtra("Room ${ii+1} | Leader: ")
                    addExtra(TextComponent(party?.leader?.name ?: "none")
                            .apply { if (text != "none") color = ChatColor.LIGHT_PURPLE })
                    addExtra("  [ ")
                    addExtra(getJoinClickable(
                            inst,
                            party == null,
                            party?.isLocked == true,
                            party?.isFull == true,
                            party?.inGame == true,
                            player))
                    addExtra(" ]")
                    party?.let{ addExtra("  [ ${party.playerCount}/${party.maxPlayers} ]\n") }
                            ?: addExtra("\n")
                }
                addExtra("\n".repeat(13 - d.instances.count() - ceil((d.description.length + 17) / 55.0).toInt()))
                if (page > 0) {
                    addExtra("=[ ")
                    addExtra(getPageClickable("PREVIOUS", page - 1))
                    addExtra(" ]")
                } else addExtra("=============")
                addExtra("================================")
                if (page < FWDungeonsController.dungeons.count() - 1) {
                    addExtra("[ ")
                    addExtra(getPageClickable("NEXT", page + 1))
                    addExtra(" ]=")
                }
                else addExtra("=========")
            })
        }
    }